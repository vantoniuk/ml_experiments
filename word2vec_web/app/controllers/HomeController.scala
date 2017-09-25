package controllers

import javax.inject._

import play.api._
import play.api.mvc._
import wordembedding.util.Loggable
import wordembedding.word2vec.WordEmbedding

/**
  * Data type used to carry original sentence and similar words
  * @param sentence
  * @param similarWords
  */
case class SimilarWords(sentence: String, similarWords: List[String])

@Singleton
class HomeController @Inject()(cc: ControllerComponents, wordEmbedding: WordEmbedding) extends AbstractController(cc) with Loggable {

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index(None))
  }

  def findSimilarWords(sentence: String, limit: Int) = Action { implicit request: Request[AnyContent] =>
    logger.info(s"Similar words lookup for ${sentence}")

    // split original sentence by spaces
    val splitSentence = sentence.toLowerCase().split("\\s+")

    logger.info(s"Parsed sentence ${splitSentence.mkString(", ")}")

    // perform similar words lookup using provided word embedding model
    val similarWords = wordEmbedding.similarWords(limit, splitSentence:_*)
    
    Ok(views.html.index(Some(SimilarWords(sentence, similarWords))))
  }
}
