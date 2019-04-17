import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object UdfExample extends App {
  /* https://github.com/jaceklaskowski/spark-workshop/blob/gh-pages/exercises/spark-sql-exercise-Using-UDFs.md */

  val spark = SparkSession
    .builder()
    .getOrCreate()

  import spark.implicits._

  /* Create a test dataframe to work with. */
  val df1 = Seq("hello", "world").toDF("test")

  df1.show

  def myUpperCaseFunc(s: String): String = s.toUpperCase

  val myUpperUDF = udf[String, String](myUpperCaseFunc)

  val df2 = df1.withColumn("test".toUpperCase, myUpperUDF($"test"))

  df2.show
}
