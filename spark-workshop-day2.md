# Spark Workshop Day 2

## IntelliJ IDEA
- `libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.4.1"`
- `sbt package` - To create a .jar file out of Scala code
- Name files as `{whatever you want here}.scala`
- Run `spark-submit {path to .jar file}`

### Example: Reading in a CSV
```
import org.apache.spark.sql.SparkSession

object MyApp extends App {
  val spark = SparkSession
    .builder()
    .master("local[*]")
    .getOrCreate()
  spark
    .read
    .option("header", true)
    .option("inferSchema", true)
    .csv(args(0))
    .show(truncate=false)
}
```

## Spark Tidbits
- Dataset vs DataFrame
  - Datasets are type safe; that's why Scala developers prefer to use Datasets
    - In DataFrames, you don't get any visibility into what is contained in the Rows that make up the DataFrame; DataFrames hide the type
  - However you should always use DataFrame -> they provide better performance
    - I guess because we don't have to always look into Rows to see types of objects inside the Rows
    - Might not even be true actually -> as long as you use `df.withColumn`, `df.select` and `df.where` instead of Scala constructs
  - DataFrame is a type alias for a Dataset containing Rows
    - `type DataFrame = Dataset[Row]` - From Apache Spark source code
- Always use Spark constructs instead of Scala constructs
  - E.g., use `df.withColumn(...)` instead of `df.map(...)`
  - This is because Scala constructs require moving the data into JVM requires a `DeserializeToObject`
- Always use `df.withColumn(...)`! It is optimized. (Repeating for emphasis)
- Filter pushdown is an optimization that could make your queries faster
  - Allows you to only load data that you actullay want to use
- `people.as[Person].filter($"id" > 1).show`
  - The filter query is something that Spark understands which allows Spark to push down the filter is applicable
- Try to keep big data in Parquet since Parquet holds lots of information about the data it contains. This allows Spark to push down filterst
- In order to use `$"columnName"`, you need to `import spark.implicits._`
- `===` for equality!
- Does the ordering of my expressions have an impact on the performance of the query? Possibly, hard to provide a straightforward answer.
  - Spark uses an optimizer that is rule-based. There are hundreds of rules. It is very difficult to give an answer.
- Spark does memory management for you.
  - E.g., when to trigger JVM garbage collection
- How does Spark determine how many executors to use per stage (or task)? Equivalent to the number of partitions possibly

### Dataset Operators
- `as` to convert a Row-based DataFrame to a Dataset
- `createOrReplaceTempView` to create a temporary view
- `explode` - `:type explode($"id")` return `org.apache.spark.sql.Column`

### Executor
- `local[*]` is equivalent to `local[#CPU cores]`
- Spark Standalone is built in; but the quality is not that great
- YARN should probably only be used when you need to also use HDFS
  - YARN is more generic; it doesn't even know that a Spark application requested a certain number of cores
- After you submit a single application to Spark Standalone or Spark YARN, are there any performance benefits for using one or the other? No
- Spark support for Kubernetes is very young, very inexperienced
- Chart below only contains defaults. There are lots of configurations you can make.
```
            | local[*] | Spark Standalone  | YARN
------------+----------+-------------------+---------------------+
# CPU cores | "8"      | ALL               | 2 (1 core/executor) |
------------+----------+-------------------+---------------------+
# executors | 1 driver | 1 executor/worker | 2                   |
```

### Running Spark Standalone with Kubernetes
- Only Java11 supports dockerized Java applications
- However, Spark only supports Java8
- Potential issues:
  - JVM is unable to report correct number of cores and correct memory usage to Spark (since JVM is running inside a Docker container which is not supported in Java8) -> Spark could get confused

## Scala Tidbits
- `$"name"` results in `org.apache.spark.sql.ColumnName = name`
```
scala> val ids = Seq(1,2,3).toDF("id")
ids: org.apache.spark.sql.DataFrame = [id: int]

scala> ids("id")
res1: org.apache.spark.sql.Column = id

scala>ids.apply("id")
res2: org.apache.spark.sql.Column = id
```
- `:type {object}` - Gives you the type of a Scala object in `spark-shell`
- Look up Case Classes

## Rules
1. Always use `df.withColumn`
2. Always use Parquet instead of CSV or JSON
3. Always use HDFS
4. Always use Scala
