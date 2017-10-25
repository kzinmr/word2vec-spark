/**
  * Created by Kazuki Inamura on 2017/08/04.
  */
import java.text.SimpleDateFormat
import java.util.Calendar

import org.apache.spark._
import org.apache.spark.api.java.JavaSparkContext

import scala.collection.convert.WrapAsScala._
//import scala.collection.JavaConversions._

import com.atilika.kuromoji.ipadic.{Token, Tokenizer}

import org.apache.spark.mllib.feature.{Word2Vec, Word2VecModel}


object Word2VecApp extends App {

  val ipath = "corpus_wikipedia.txt.100M" //"gs://word2vec-spark/corpus_wikipedia.txt"
  val opath = "gs://word2vec-spark/wordcounts-out/word2vec.model"
  val cores = "*"

  val conf = new SparkConf().setMaster("local[%s]".format(cores)).setAppName("Local App")
  val local_sc = new SparkContext(conf)

  //  val conf = new SparkConf().setAppName("Cloud App")
  //  val cluster_sc = new JavaSparkContext(conf)

  val sc = local_sc //cluster_sc

  val startTime = System.nanoTime

  try{
  val input = sc.textFile(ipath).repartition(sc.defaultParallelism * 3).filter(_.length > 1).map(line => {
    val builder = new Tokenizer.Builder()
    val tokens : java.util.List[Token] = builder.build().tokenize(line)
    val output : StringBuilder = new StringBuilder()
    tokens.foreach(token => {
      //System.out.println(token.getSurface() + "\t" + token.getAllFeatures());
      output.append(token.getSurface())
      output.append(" ")
    })
    output.toString()
  }).
    flatMap(line => line.split(" ").toSeq)
  //map(line => line.split(" ").toSeq)

  // Word Count
  val fmt = new SimpleDateFormat("d-M-y")
  val dt = fmt.format(Calendar.getInstance().getTime())
  val wordCounts = input.map(word => (word, 1)).reduceByKey((a, b) => a + b)
  //val outputs = wordCounts.collect()
  wordCounts.saveAsTextFile("./wordcounts-out/%s/".format(dt)) //"gs://word2vec-spark/wordcounts-out/"

  // Word2Vec Training
  //  val df = sqlContext.createDataFrame(input).toDf("doc")
  //  val tokenizer = new Tokenizer().setInputCol("doc").setOutputCol("tok")
  //  val tokenizedDf = tokenizer.transform(df)
  //  val word2vec = new Word2Vec().setInputCol("doc").setOutputCol("features").setMinCount(5).setVectorSize(250).setNumPartitions(1).fit(df)

  //  val model: Word2VecModel = new Word2Vec().setMinCount(5).setVectorSize(250).setNumPartitions(1).fit(input)
  //  model.save(sc, opath)
  //val sameModel = Word2VecModel.load(sc, "myModelPath")
  } finally {
    sc.stop()
  }
  println("Elapsed time: " + "%,d".format(System.nanoTime - startTime) + " ns")
}
