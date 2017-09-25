package wordembedding.word2vec

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer

import scala.util.Try

/**
  * General word embedding factory
  */
trait WordEmbeddingFactory {
  def create: Try[WordEmbedding]
}

/**
  * Word embedding factory that loads the model from text file
  */

class TextFileWordEmbeddingFactory(textFilePath: String) extends WordEmbeddingFactory {
  def create: Try[WordEmbedding] = {
    Try(WordVectorSerializer.readWord2VecModel(textFilePath))
      .map(word2Vec => new Word2VecEmbedding(word2Vec))
  }
}


