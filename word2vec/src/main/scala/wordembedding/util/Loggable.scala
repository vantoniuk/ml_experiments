package wordembedding.util

import org.slf4j.{Logger, LoggerFactory}

trait Loggable {
  protected val logger: Logger = LoggerFactory.getLogger(this.getClass)
}
