import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object DatesExample extends App {
  /* https://github.com/jaceklaskowski/spark-workshop/blob/gh-pages/exercises/spark-sql-exercise-Difference-in-Days-Between-Dates-As-Strings.md */

  val spark = SparkSession
    .builder()
    .getOrCreate()

  import spark.implicits._

  val dates = Seq(
    "08/11/2015",
    "09/11/2015",
    "09/12/2015").toDF("date_string")

  dates.show

  val df1 = dates.withColumn("current_date", current_date)

  df1.show

  val df2 = df1.withColumn("to_date", to_date($"date_string", "MM/dd/yyyy"))

  df2.show

  val df3 = df2.withColumn("diff", datediff($"current_date", $"to_date"))

  df3.show
}
