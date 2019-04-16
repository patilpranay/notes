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
- Your algorithm determines how many tasks you will ultimately create.
  - Have a test run and check performance. If performance is okay, note how many tasks were created and how many executors/cores were used. You can then use these numbers to determine how many resources to give to Spark.
  - Every executor requires its own JVM.
  - The more tasks an executor processes => the more cores that executor needs => the more memory JVM needs.
    - Scale up the number of executors so that the memory belonging to one JVM is not too large.
  - Spark supports dynamic allocation of executors (disabled by default).
    - Disabled by default because it seemed to be not that useful in most cases. Adds extra variable of nondeterminism.
- **Adaptive Query Execution** - look this up
- When you allocate memory for a new executor, that memory is for JVM (not the data).
  - If the data could be fit into memory, you would not be using Spark.
- Dynamic Allocation of Executors
  - You can set a minimum number of executors
  - Spark takes a look at the queue of tasks and creates more executors if needed
- Executors are only there to execute tasks.

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

## Spark Core
- `SparkContext` is the entrypoint to Spark services.
  - Manages the connection to a Spark execution environment.
  - Execution environment defined by URL to Spark master.
- Stay away from RDDs unless you know what you are doing.

### Resilient Distributed Dataset
- The main data abstraction
- It can survive failures. Spark knows how to recover from failure.
- Distributed because Spark spreads out tasks on a single dataset across multiple workers.
- Almost all operations on a RDD creates another RDD.
- RDD is an abstract class with the following properties:
  - Dependencies - parent RDDs if any
  - Partitions
  - **compute** function: Partition => Iterator[T]
  - Optional **Partitioner** to define key hashing (hash, range)
  - Optional **preferred locations** for locality information
- Partitions are buckets and the partitioner decides what data gets placed in what bucket
- Partitions are logical buckets for data.
  - Partitions correspond to Hadoop splits if the data lives on HDFS.
- RDD is partitioned.
- Sparm manages data using partitions. Partitions help to create tasks which are then executed (relationship between partitions and tasks is 1 to 1).
- RDD Operators
  - **Transformation** is a lazy RDD operator that create one or many RDDs
  - **Action** is a RDD operation that produces non-RDD Scala values
- **Shuffle** is Spark's way to re-distribute data so that it's grouped differently across partitions

### Other Core Concepts
- A **DAGScheduler** creates a plan after an action is performed on an RDD.
- A **job** is made up of actions on one or more partitions. Spark will not create jobs for 0 partitions.
- After you perform an action on a RDD, a Job is created. This leads to a **Stage**, which contains **tasks** for the action on every partition. (Remember, partitions and tasks are always 1 to 1.)
- If two sequential RDDs share the same partitioner and the same number of partitions, then the resulting tasks can be combined into one stage.

## Functions

### Standard Functions
- `import org.apache.spark.sql.functions._`
- You should try to use as many standard functions as possible before you create your own. Because Spark controls what standard functions do while custom functions cannot be optimized by Spark.
- In Spark 2.4, there are brand new standard functions for "Array Algebra" and "Map Algebra".

### User Defined Functions
- It is very difficult to optimized user defined functions.
- UDFs are a blackbox for Spark Optimizer and does *not even try* to optimize them.
```
// pure Scala function
val myUpperFn = (input: String) => input.toUpperCase

// user-defined function
val myUpper = udf(myUpperFn)
```
- Use UDFs as standard functions (even though they are not).
- Really really just stay away from UDFs.
- When using UDFs, you need to `DeserializeObject` in order to bring the data into memory.
- Register your UDFs with Spark:
```
//pure Scala function
val myUpperFn = (input: String) => input.toUpperCase

// register Scala function as UDF
spark.udf.register("myUpper", myUpperFn)

// Use myUpper as if it were a standard function
sql("select myUpper(name) from people").show
```
- UDFs are **deterministic** by default.
  - You can check whether a function is determinstic or not.
  - Use **asNondeterministic** to disable determinism.
  - Nondeterministic functions prevent Spark from making certain optimizations.
