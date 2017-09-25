package builds

import sbt._

object Libs {

  val dl4jVer = "0.9.1"

  val datavecDataCodec    = "org.datavec"        %  "datavec-data-codec"              % dl4jVer
  val dl4jCore            = "org.deeplearning4j" %  "rl4j-core"                       % dl4jVer
  val dl4jNlp             = "org.deeplearning4j" %  "deeplearning4j-nlp"              % dl4jVer
  val nd4jCuda75          = "org.nd4j"           %  "nd4j-cuda-7.5"                   % dl4jVer
  val nd4jCuda75Platform  = "org.nd4j"           %  "nd4j-cuda-7.5-platform"          % dl4jVer
  val nd4jCuda80Platform  = "org.nd4j"           %  "nd4j-cuda-8.0-platform"          % dl4jVer
  val nd4jNative          = "org.nd4j"           %  "nd4j-native"                     % dl4jVer
  val nd4jNativePlatform  = "org.nd4j"           %  "nd4j-native-platform"            % dl4jVer

  val playTest            = "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test

  val logback             = "ch.qos.logback"     %  "logback-classic"                 % "1.1.7"
  val opencsv             = "net.sf.opencsv"     % "opencsv"                          % "2.3"
  val scallop             = "org.rogach"        %% "scallop"                          % "3.1.0"
}
