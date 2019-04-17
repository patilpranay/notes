import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object MultipleAggregates extends App {
  /* https://github.com/jaceklaskowski/spark-workshop/blob/gh-pages/exercises/spark-sql-exercise-Multiple-Aggregations.md */

  val spark = SparkSession
    .builder()
    .getOrCreate()

  import spark.implicits._

  val nums = spark.range(5).withColumn("group", 'id % 2)

  nums.show

  val df1 = nums
    .groupBy($"group")
    .agg(
      sort_array(collect_list($"id"), false) as "ids",
      max($"id") as "max_id",
      min($"id") as "min_id")

  df1.show
}
