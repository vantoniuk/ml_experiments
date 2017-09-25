package wordembedding.scripts

import java.io.{File, FileWriter}

import wordembedding.util.{CSVParser, Loggable}
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.deeplearning4j.models.word2vec.Word2Vec
import org.deeplearning4j.text.sentenceiterator.{BasicLineIterator, LineSentenceIterator, SentenceIterator}
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor
import org.deeplearning4j.text.tokenization.tokenizerfactory.{DefaultTokenizerFactory, NGramTokenizerFactory, TokenizerFactory}
import org.rogach.scallop.ScallopConf

import scala.io.Source
import scala.util.{Failure, Success, Try}

class WordEmbConf(arguments: Seq[String]) extends ScallopConf(arguments) {
  val sourseFile  = opt[String](required = true, name = "source", descr = "file that contains text sentences for word embedding")
  val outputFile  = opt[String](required = true, name = "output", descr = "file that will contain serialized model")
  val csvColumn   = opt[Int](required = false, name = "csv-column", descr = "index of csv column that contains sentence, starting with 1")
  val linesToTake = opt[Int](required = false, name = "lines", descr = "number of lines to read from source file, can be useful for testing")
  val tempFile    = opt[Boolean](required = false, name = "temp-file", descr = "flag, whether to create temp file, with some preprocessing done")

  verify()
}

object Word2VecEmbedding extends Loggable {
  def main(args: Array[String]): Unit = {
    val conf = new WordEmbConf(args)

    for {
      initialSourceFile <- conf.sourseFile.toOption
      outputFile <- conf.outputFile.toOption
    } {
      // get the source file name.
      val sourceFile = if(conf.tempFile.getOrElse(false)) {
        logger.info("Preparing the temp file")
        prepareTempFile(initialSourceFile, conf.csvColumn.toOption, conf.linesToTake.toOption)
      } else {
        new File(initialSourceFile)
      }

      // training the model
      val word2Vec = trainWord2Vec(sourceFile)

      logger.info("Writing word vectors to text file....")

      //writing the model to file
      WordVectorSerializer.writeWord2VecModel(word2Vec, new File(outputFile))
    }
  }

  private def trainWord2Vec(sourceFile: File): Word2Vec = {
    logger.info("Load & Vectorize Sentences....")
    val iter: SentenceIterator = new BasicLineIterator(sourceFile)

    /*
      NGramTokenizerFactory chosen to detect short sentences 1-3 grams
     */
    val tokenizerFactory: TokenizerFactory = new NGramTokenizerFactory(new DefaultTokenizerFactory, 1, 3)

    /*
      CommonPreprocessor will apply the following regex to each token: [\d\.:,"'\(\)\[\]|/?!;]+
      So, effectively all numbers, punctuation symbols and some special symbols are stripped off.
      Additionally it forces lower case for all tokens.
      It will work for underlying factory as well
    */
    tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor)

    logger.info("Building model....")
    val vec = new Word2Vec.Builder()
      // remove words with low number of occurrences, usually the suggested value is within 1 - 100
      .minWordFrequency(25)
      .iterations(1)
      // we can effort bigger layer size because of large amount of training data
      .layerSize(500)
      .seed(42)
      // we may want to look at longer window than default 5
      .useVariableWindow(5, 7, 9)
      // Mikolov's approach with negative sampling outperforms standard approach without it,
      // based on this paper https://arxiv.org/pdf/1402.3722v1.pdf
      // so we pick positive double value to enable it
      // also the number of negative samples is suggested to be as low as 2-5 for large dataset,
      // based on this paper https://arxiv.org/pdf/1310.4546.pdf
      // value is chosen based on usage in InMemoryLookupTable.java:353
      .negativeSample(0.1)
      // down sample the frequent terms that don't carry much information
      // value is chosen based on usage formula in SkipGram.java:133-141
      .sampling(0.01)
      .iterate(iter)
      .tokenizerFactory(tokenizerFactory)
      .build

    logger.info("Fitting Word2Vec model....")
    vec.fit()

    vec
  }

  /**
    * Cleans the file and saves to temp file
    * @return temp file path
    */
  private def prepareTempFile(inputFilePath: String, columnIndex: Option[Int], linesToTake: Option[Int]): File = {

    val parser = CSVParser.default
    val inputFile = new File(inputFilePath)
    val tempFileName = inputFile.getAbsolutePath.stripSuffix(inputFile.getName) + "temp_" + inputFile.getName
    val tempFile = new File(tempFileName)
    val fileWriter = new FileWriter(tempFile)

    @inline def getLines: Iterator[(String, Int)] = Source
      .fromFile(inputFile)
      .getLines
      .zipWithIndex // getting line indices for debug purposes

    var processedLines = 0L
    var malformedCSVLines = 0L
    
    logger.info("starting the cleanup of csv file")

    val lines = linesToTake.fold({
      logger.info("Working with full file")
      getLines
    })(linesLimit => {
      logger.info(s"Working with first $linesLimit lines from file")
      getLines.take(linesLimit)
    })

    lines.foreach{
      case (csvLine, index) if columnIndex.isDefined =>
        // in case there a malformed csv line 
        val parsedSentence = parser.parse(csvLine)
          // -1 because we ask to choose column index starting with 1 as script argument
          .flatMap(csvFields => Try(csvFields(columnIndex.get - 1)))

        parsedSentence match {
          case Success(sentence) =>
            fileWriter.write(sentence + "\n")
            processedLines += 1
          case Failure(error) =>
            malformedCSVLines += 1
            logger.warn(s"line with index $index could be parsed, skipping...", error)
        }
      case (sentence, _) =>
        fileWriter.write(sentence + "\n")
        processedLines += 1
    }

    fileWriter.close()

    tempFile
  }

}
