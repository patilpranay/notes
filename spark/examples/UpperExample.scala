import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._

object UpperExample extends App {

  if (args.length < 2) {
    println("Not enough args provided.")
    System.exit(1)
  }

  val spark = SparkSession
    .builder()
    .getOrCreate()

  import spark.implicits._

  val input = spark
    .read
    .option("header", true)
    .csv(args(0))

  /*
  // This code block only makes a single column into upper case.
  val name = args(1)

  val isOK = input
    .schema
    .fields
    .filter(f => f.name == name)
    .exists(f => f.dataType == StringType)

  if (!isOK) {
    println(s"The column is not of StringType.")
    System.exit(1)
  }

  input
    .withColumn(name.toUpperCase, upper(col(name)))
    .show
  */

  /* This code block takes in column names (through command line) whose values
     are transformed into upper case. */
  val names = args.drop(1)

  val fields = input.schema.fields
  val isStringType = (f: StructField) => f.dataType == StringType
  val stringFields = fields.filter(isStringType)
  val schemaNames = stringFields.map(f => f.name)
  val ns = names.filter(schemaNames.contains)  // Not sure if we need this.

  val finalDF = ns.foldLeft(input) {
    case (df, name) => df.withColumn(name.toUpperCase, upper(col(name)))
  }
  finalDF.show
}
