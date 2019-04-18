import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions.Window

object BestsellersWindowingExample extends App {
  /* https://github.com/jaceklaskowski/spark-workshop/blob/gh-pages/exercises/spark-sql-exercise-Finding-1st-and-2nd-Bestsellers-Per-Genre.md */

  val spark = SparkSession
    .builder()
    .getOrCreate()

  import spark.implicits._

  val books = Seq(
    (1,"Hunter Fields","romance",15),
    (2,"Leonard Lewis","thriller",81),
    (3,"Jason Dawson","thriller",90),
    (4,"Andre Grant","thriller",25),
    (5,"Earl Walton","romance",40),
    (6,"Alan Hanson","romance",24),
    (7,"Clyde Matthews","thriller",31),
    (8,"Josephine Leonard","thriller",1),
    (9,"Owen Boone","sci-fi",27),
    (10,"Max McBride","romance",75)).toDF("id", "title", "genre", "quantity")

  books.show

  val genres = Window
    .partitionBy("genre")
    .orderBy($"quantity".desc)

  val books2 = books
    .withColumn("rank", rank over genres)
    .filter($"rank" < 3)

  books2.show
}
