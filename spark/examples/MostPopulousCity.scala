import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object MostPopulousCity extends App {
  /* https://github.com/jaceklaskowski/spark-workshop/blob/gh-pages/exercises/spark-sql-exercise-Finding-Most-Populated-Cities-Per-Country.md */

  val spark = SparkSession
    .builder()
    .getOrCreate()

  import spark.implicits._

  val cities = Seq(
    ("Warsaw", "Poland", "1 764 615"),
    ("Cracow", "Poland", "769 498"),
    ("Paris", "France", "2 206 488"),
    ("Villeneuve-Loubet", "France", "15 020"),
    ("Pittsburgh PA", "United States", "302 407"),
    ("Chicago IL", "United States", "2 716 000"),
    ("Milwaukee WI", "United States", "595 351")).toDF("name", "country", "population")

  cities.show

  val cities2 = cities
    .withColumn("pop_num", translate($"population", " ", "") cast "Long")

  val cities3 = cities2
    .groupBy($"country")
    .agg(max($"pop_num") as "max_population")

  val cities4 = cities2.join(cities3).where($"max_population" === $"pop_num")

  val cities5 = cities4.select($"name", cities2("country"), $"population")

  cities5.show
}
