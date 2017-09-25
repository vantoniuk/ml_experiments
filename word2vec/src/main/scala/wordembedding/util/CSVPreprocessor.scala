package wordembedding.util

import au.com.bytecode.opencsv.{CSVParser => ThrdPartyCSVParser}

import scala.util.Try

/**
  * Preprocessor to simplify the job of tokenizers for word2vec process, for example if
  * additional cleanup needed
  */
trait CSVPreprocessor {
  def apply(in: String): Try[Array[String]]
}

object CSVPreprocessor {
  val default = new OpenCSVPreprocessor(new ThrdPartyCSVParser())
}

class OpenCSVPreprocessor(parser: ThrdPartyCSVParser) {
  def parse(csvString: String): Try[Array[String]] = {
    Try(parser.parseLine(csvString).map(_.trim))
  }
}
