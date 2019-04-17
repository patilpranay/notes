import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object SparkProjectTemplate extends App {

  // Specify the master: `spark-submit --master spark://localhost:7077`
  val spark = SparkSession
    .builder()
    .getOrCreate()

  import spark.implicits._

  // Make the sleep thread to keep the Web UI running for this application.
  // Remove this when not testing.
  Thread.sleep(TimeUnit.DAYS.toMillis(1))
}
