import org.apache.spark.sql.SparkSession

object FlatMapExample extends App {
  // Specify the master: `spark-submit --master spark://localhost:7077
  val spark = SparkSession
    .builder()
    .getOrCreate()

  import spark.implicits._

  val nums = Seq(Seq(1, 2, 3)).toDF("nums")

  println(nums.show())

  val result = nums
    .as[Seq[Int]]
    .flatMap { ns =>
      ns.map { n =>
        (ns, n)
      }
    }.toDF("nums", "num")

  println(result.show())

  // Use for comprehension logic to achieve the same result - more readable.
  val ds = for {
    ns <- nums.as[Seq[Int]]
    n  <- ns
  } yield (ns, n)
  val result2 = ds.toDF("nums", "num")

  println(result2.show())
}
