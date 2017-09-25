package wordembedding.util

import au.com.bytecode.opencsv.{CSVParser => ThrdPartyCSVParser}

import scala.util.Try

trait CSVParser {
  def parse(in: String): Try[Array[String]]
}

object CSVParser {
  val default = new OpenCSVParser(new ThrdPartyCSVParser())
}

class OpenCSVParser(parser: ThrdPartyCSVParser) {
  def parse(csvString: String): Try[Array[String]] = {
    Try(parser.parseLine(csvString))
  }
}
