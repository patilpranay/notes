# Spark Workshop Day 1
- Always check your Spark session by typing `:type spark`

## Example 1 - Basics of `spark-shell`
- `spark.range(4)`
  - `.show` would return 
 ```
+---+
| id|
+---+
|  0|
|  1|
|  2|
|  3|
+---+
```
- `Unit` is essentially void (Scala)
  - The name of methods that return nothing are procedures.
  - Procedures are bad because they work by changing global state.
  - Changing global state makes multi-threaded programming difficult.
  - Functions are your friend; they take in something, modify that and return it.
  - Write functions that avoid side effects.
- Stay away from variables -> variables introduce mutable state which again prevents multi-threaded programming.
- `:hi` - Lists a history of previous commands.
- `spark.range(1)` - Creates a one row, one column dataframe.
- `implicits` take existing classes and add new methods to do (e.g., in Scala you can take `final` classes like `String` and extends them.
- `val ids = Seq(0,1,2)`.toDF("id")` creates a DataFrame that looks like
```
+---+
| id|
+---+
|  0|
|  1|
|  2|
+---+
```
- `val ids = Seq(Seq(0,1,2)).toDF("ids")` creates a DataFrame that looks like
```
+---------+
|      ids|
+---------+
|[0, 1, 2]|
+---------+
```
- Collecting data means bringing all of the data together onto one single machine.
- If you want to get better at Scala, you need to spend a lot of time with `flatMap`
- Scala is a typesafe language
- Encoders are serialization and deserialization frameworks for Spark SQL.
- Spark keeps data outside of JVM. Why? Because Spark doesn't want data to be garbage collected. More garbage collections -> worse performance
- `ids.flatMap(r => r.getSeq[Int](0))` creates a Dataset that looks like
```
+-----+
|value|
+-----+
|    0|
|    1|
|    2|
+-----+
```
- `ids.wC` tab completes to `ids.withColumn`
- `ids.flatMap(r => r.getSeq[Int](0))` is equivalent to `ids.select(explode('ids) as "ids")`. The latter (which is much faster) creates a Dataset that looks like
```
+---+
|ids|
+---+
|  0|
|  1|
|  2|
+---+
```
- Don't use Scala specific constructs (like `map`, `flatMap`, `filter`) for Spark application code. You are trading performance for readability. This is because everything in Scala is an object, which requires serialization and deserialization.
- General rule: just use `df.withColumn` or `df.select` (when writing Spark code in Scala; PySpark has its own rules)
- `data.printSchema`
- `data.withColumn("words", split($"words", ",")).show`
- `data.withColumn("words", split($"words", ",")).withColumn("w", explode($"words")).show`
- `.show(truncate = false)` shows the entire DataFrame
- `data.withColumn("words", split($"words", ",")).withColumn("w", explode($"words")).where($"word" === $"w").show`

### Description of Internals
- RDD and DataFrames are both descriptions of distributed computations.
  - In Scala, we have Dataset (not DataFrame)
  - Relationship between Task and Partition is 1 to 1
  - Relationship between RDD and DataFrame is 1 to 1
  - Relationship between RDD and Partition is 1 to 1+* (1 to many)
  - Relationship between DataFrame and Partition is 1 to 1+* (1 to many)
  - Relationship between Task and CPU Core is 1 to 1
  - If Spark sends 4 tasks to an Executor, it determines that 4 CPU cores are now busy (without directly communicating with the OS)
  - Its assumed that when you start Spark on a set of machines, then all of these machines are owned by Spark
- Spark is so focused on supporting 4 different languages because lots of people can only code in one language.
- You can extend Spark to make it faster by adding data specific code.

### Determining Partitions
- Spark talks with the distributed filesystem (like HDFS) to figure out how the data is chunked in order to determine how many partitions to make.

## Scala Basics
```
object HelloWorld extends App {
  println("Hello Pittsburgh")
}
```
- For Scala, that's all you need to know to run a basic program.
- Java is more complicated because you would have to do introduce a `main` method. That's why Scala is the second easiest language for beginners to learn (after Python).

```
case class Person(id: Long, name: String)
```
- The following ways of creating a new instance of `Person` are equivalent:
```
new Person(0, "BJ")
Person(0, "BJ")
Person.apply(0, "BJ")
```

### A Simple Spark Application
```
import org.apache.spark.sql.SparkSession

object HelloWorld extends App {
  val file = if (args.length > 0) args(0) else "build.sbt"
  val spark = SparkSession
    .builder
    .master("local[*]")
    .getOrCreate
  spark
    .read
    .option("header", true)
    .csv(file)
    .show(truncate = false)
```
- To create a Spark application, you don't necessarily have to read multiple books on Scala.

## Spark SQL
### Spark Hierarchy
- Apache Spark, Apache Hadoop - both umbrella projects
- Apache Hadoop
  - HDFS
  - YARN
  - MapReduce - basically gone, now Apache Spark
- The only thing Apache Spark was aimed at improving was MapReduce
- Apache Spark (basically MapReduce++)
  - Spark Core: The main abstraction is RDD API
  - Spark SQL: The main abstraction is Dataset
  - Spark Structured Streaming: The main abstraction is Dataset
  - Spark MlLib: The main abstraction is a pipeline
- Is Spark a relational database? No, its missing persistence/storage
- Spark + HDFS = database

### Introduction to Spark SQL
- `SparkSession` is the entrypoint to Spark SQL (and Spark in general these days)
- Load datasets using `SparkSession.read`
- Write datasets using `Dataset.write`
- Loading and writing operators create source and sink nodes in a data flow graph
- Spark SQL provides a high level API for doing distributed computations
- Always specify the schema of your data - otherwise the `load` operation becomes an action to infer the schema
  - When you provide the schema, there is no action to be done. Thus, there is no entry for the job on the UI.
- Spark always nulls all the incorrect values (based on the provided schema)
- Ad-hoc local dataset commands
  - `Seq(...).toDF("col1", "col2", ...)`
  - `Seq(...).toDS(...)`
  - Local because you created the sequence locally so that data is already in memory
