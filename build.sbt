name := "word2vec-spark"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies += "org.apache.spark" % "spark-core_2.11" % "2.0.2"// % "provided"
libraryDependencies += "org.apache.spark" % "spark-mllib_2.11" % "2.0.2"// % "provided"

libraryDependencies += "com.atilika.kuromoji" % "kuromoji-ipadic" % "0.9.0"

//resolvers += "Spark Packages Repo" at "http://dl.bintray.com/spark-packages/maven"
//libraryDependencies += "yu-iskw" % "spark-kuromoji-tokenizer" % "1.2.0"