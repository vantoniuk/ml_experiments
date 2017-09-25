import com.google.inject.AbstractModule
import com.google.inject._
import play.api.{ Configuration, Environment }
import wordembedding.word2vec._
import wordembedding.util._

/**
  * Module create in root folder will be automatically used by Play Framework,
  * so we can annotate with @Provides annotations methods which provide the instances for injection
  * @param environment
  * @param configuration
  */
class Module(environment: Environment,
              configuration: Configuration) extends AbstractModule with Loggable {

  @Provides @Singleton
  def wordEmbeddingFactory: WordEmbeddingFactory = {
    val wrod2vecConfiguration: Configuration = configuration.get[Configuration]("word2vec")

    val modelFile: String = wrod2vecConfiguration.get[String]("model_file")

    logger.info(s"Creating word embedding factory from model file: ${modelFile}")

    new TextFileWordEmbeddingFactory(modelFile)
  }

  @Provides @Singleton @Inject
  def wordEmbedding(factory: WordEmbeddingFactory): WordEmbedding = {
    logger.info("Creating word embedding instance...")

    factory
      .create
      .getOrElse(throw new IllegalStateException("can't create word embedding, check the model file"))
  }

  def configure() = {}
}
