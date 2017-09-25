import builds.Libs

lazy val nd4jBackend = Libs.nd4jNativePlatform
// or Libs.nd4jCuda75Platform
// or Libs.nd4jCuda80Platform

lazy val root = project.in(file("."))
    .aggregate(
      word2vec
    )
    .settings(name := "casetext word2vec")
    .settings(commonSettings:_*)

lazy val word2vec = project.in(file("word2vec"))
  .settings(name := "word2vec")
  .settings(commonSettings:_*)
  .settings(libraryDependencies ++= Seq(
    Libs.datavecDataCodec,
    Libs.dl4jCore,
    Libs.dl4jNlp,
    Libs.opencsv,
    Libs.scallop,
    nd4jBackend
  ))

lazy val commonSettings = Seq(
  version := "1.0",
  scalaVersion := "2.12.3",
  resolvers ++= commonResolvers
)

lazy val commonResolvers = Seq(
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  "Sonatype release Repository" at "http://oss.sonatype.org/service/local/staging/deploy/maven2/"
)


