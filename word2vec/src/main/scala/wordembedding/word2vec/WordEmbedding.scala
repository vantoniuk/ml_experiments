package wordembedding.word2vec

import org.deeplearning4j.models.word2vec.Word2Vec
import scala.collection.JavaConverters._

/**
  * Facade interface that we are going to use to hide third party library
  */
trait WordEmbedding {
  /**
    * Performs similar words lookup
    * @param limit number of similar words to return
    * @param word words for similarity lookup
    * @return collection of similar words in similarity descending order
    */
  def similarWords(limit: Int, word: String*): List[String]
}

class Word2VecEmbedding(model: Word2Vec) extends WordEmbedding {
  def similarWords(limit: Int, words: String*): List[String] = {
    model.wordsNearest(words.asJava, Nil.asJava, limit).asScala.toList
  }
}
