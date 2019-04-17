import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object UdfExample extends App {
  /* https://github.com/jaceklaskowski/spark-workshop/blob/gh-pages/exercises/spark-sql-exercise-Collect-values-per-group.md */

  val spark = SparkSession
    .builder()
    .getOrCreate()

  import spark.implicits._

  val nums = spark.range(5).withColumn("group", 'id % 2)

  nums.show

  val df1 = nums.groupBy($"group").agg(collect_list($"id") as "ids")

  df1.show
}
