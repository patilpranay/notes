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

