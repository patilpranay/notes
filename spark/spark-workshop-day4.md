# Spark Workshop Day 4

## Spark Tidbits
- Most basic level of execution is the task
- Views and tables are only representations of external data
  - Spark unifies access to external data by using views and tables

## Scala Tidbits
```scala
// Incomplete
val ids = input.rdd.zipWithIndex  // Need to rely on RDDs
ids
  .as("left")
  .join(ids.as("right"))
  .where($"left.id" === $"right.id" + 1)
  .select("left.c", "right.c", "left.id")
  .orderBy($"left.id")
  .show
```

### RDD Lineage
- Example RDD Lineage
  - You have input RDD
  - Input RDD -> Transformation -> RDD2
  - RDD2 -> Transformation -> RDD3
  - RDD3, RDD4 -> Join -> RDD5
```scala
println(q.queryExecution.toRDD.toDebugString)  // q is a DataFrame
```

## Structured Queries
- **Structured query** is a query over data that is described by a schema
- **Schema** is a tuple of three elements
  - Name
  - Type
  - Nullability
- **S**tructured **Q**uery **L**anguage
  - Nice, clean, easy to understand
  - Declarative language
    - You declare what to do, not how
```scala
/* Dataset API for Scala. */
spark.table("t1")
  .join(spark.table("t2"), "id")
  .where($"id" % 2 == 0)
```

### Query Languages in Spark SQL
- High-level declarative query languages
  - Good ol' **SQL**
  - Untyped row-based **DataFrame**
  - Typed **Dataset**
- Another "axis" are programming languages
  - Scala, Java, Python, R
  
### Data Structures
- **DataFrame** - a collection of rows with a schema
- **Row** and **RowEncoder**
- DataFrame = **Dataset[Row]**
  - Typed alias
- **Dataset** - strongly-typed API for working with structured data in Scala
- **Encoder** - Serialization and deserialization API
  - Converts a JVM object of type T to and from an **InternalRow**
  - **ExpressionEncoder** is the one and only implementation
  
#### Project Catalyst
- **Catalyst** - Tree Manipulation Framework
- All structured queries are trees - that's why this framework is so important
  - Trees are everywhere
- **TreeNode** is a node with child nodes
  - Children are TreeNodes again
  - Recursive data structure
```scala
abstract class TreeNode[BaseType <: TreeNode[BaseType]] extends Product {
  // scalastye:on
  self: BaseType =>
  
  val origin: Origin = CurrentOrigin.get
  
  /**
    * Returns a Seq of the children of this node.
    * Children should not change. Immutability required for containsChild optimization
    */
  def children: Seq[BaseType]
```
- **Rules** to manipulate TreeNodes
  - Rules executed sequentially
  - Loops supported
- Every Spark command is essentially a tree of subcommands
- Every query is a tree of smaller queries
- LogicalPlan is a tree
```scala
println(df.queryExecution.logical.numberedTreeString)
println(df.queryExecution.optimizedPlan.numberedTreeString)
println(df.queryExecution.executedPlan.numberedTreeString)
```

#### Catalyst Expressions
- **Expression** - an executable node (in a Catalyst tree)
- Evaluates to a JVM object given InternalRow
```scala
def eval(input: InternalRow = numm): Any
```

### Query Execution
- **QueryExecution** is the heart of any structured query
- There is a one to one relationship between Query and QueryExecution
- Use `Dataset.explain` to know the plans
- Use `Dataset.queryExecution` to access the phases
- `QueryExecution.toRdd` to generate a **RDD**

### Different Stages from SQL Query to RDD
- **Spark Analyzer** - validates logical query plans and expressions
  - Rule executor of logical plans
- **Catalyst Query Optimizer**
  - Base of query optimizers
  - Optimizes logical plans and expressions
  - Predefined batches of rules
  - Allows for **extendedOperatorOptimizationRules**
- **Spark Logical Optimizer**
  - Custom Catalyst query optimizer
  - Adds new optimization rules
- **Spark Planner** - plans optimized logical plans to physical plans
- **Spark Physical Optimizer** preparations
```scala
protected def preparations: Seq[Rule[SparkPlan]] = Seq(
  python.ExtractPythonUDFs,
  PlanSubqueries(sparkSession),
  EnsureRrequirements(sparkSession.sessionState.conf),
  CollapseCodegenStages(sparkSession.sessionState.conf),
  ReuseExchange(sparkSession.sessionState.conf),
  ReuseSubquery(sparkSession.sessionState.conf))
```

### Structured Queries and RDDs
- `QueryExecution.toRDD` - the very last phase in a query execution
- Spark SQL generates an RDD to execute a structured query
  - That's why Jacek calls Spark SQL a generator
```
import org.apache.spark.sql.execution.debug._

val q = spark.range(10).where('id === 4)
q.debug

val q = sql("SELECT * FROM RANGE(10) WHERE id = 4")
q.debugCodegen
```

### Whole-Stage Java Code Generation
- **Whole-Stage CodeGen** physical query optimization
- Collapses a query plan tree into a single optimized function
- Applied to structured queries through **CollapseCodegenStages** physical optimization
  - `spark.sql.codegen.wholeStage` internal property

### Tungsten Execution Backend
- Used for optimizing Spark jobs for CPU and memory efficiency
- Assumed that network and disk I/O are not performance bottlenecks

## Caching and Persistence
- Persisting (or caching) a dataset in memory or disk for faster (local to the executors) access
- `Dataset.cache` persists the Dataset with the default `MEMORY_AND_DISK` storage level
- `Dataset.persist` persists the Dataset with a given storage level
- `Dataset.unpersist` un-persist (removes) any cached block data from memory and disk
- `cache` and `persist` are lazy operators
  - Quick fix to immediately cache is to use `count` right after `cache` since `count` is an action
- Dataset turns into an RDD at execution time
- Persisting a Dataset boils down to persisting the RDD
- RDD blocks are stored on executors only
- Every executor has exactly one BlockManager
  - BlockManager is a key-value store of blocks of data
- **StorageLevel** describes how block data of an RDD should be stored
  - `useDisk`
  - `useMemory`
  - `useOffHeap`
  - `deserialized`
  - `replication` (default: 1)
    - Increase replication whenever you are worried about the stability of your executors
    - You usually want to increase replication so you don't lose a cached result that only exists on a single executor
- Predefined storage levels
  - `MEMORY_ONLY` - If some data doesn't fit in memory, that data will not be cached.
  - `MEMORY_AND_DISK` - If some data doesn't fit in memory, the data will be stored on disk.
- You can take a look at memory usage using the Web UI's storage tab.
  - This tab has always existed.
- **CacheManager** is an in-memory cache (registry) of structured queries (by their logical plans)
  - Shared across SparkSessions through SharedState
  - Available as `spark.sharedState.cacheManager`
  - Spark developers interact with CacheManager using Dataset operators (cache, persist, and unpersist)
  - Different spark sessions can access the same cache
- If underlying data behind cached data changes, your cached data will be stale. Remember this!

## Window Aggregate Functions
- Perform a calculation over a group of records called **window** that are in *some* relation to the current record
- Generate a value for **every row**
  - Unlike basic aggregation that generates **at most** the number of input rows
- Examples
  - Ranking functions
  - Analytic functions
  - Aggregate functions

### Window Specification
- Defines a **window** that includes the rows that are in relation to the current row (for every row)
- (Logically) **partitions** dataset into groups
  - `partitionBy`
- May define **frame boundary**
  - `rangeBetween`
  - `rowsBetween`
```scala
import org.apache.spark.sql.expressions.Window
val departmentById = Window
  .partitionBy("department")
  .orderBy("id")
  .rangeBetween(Window.unboundedPreceding, Window.unboundedFollowing)
```

### Over Column Operator
- `over` column operator defines a **windowing column** (aka **analytic clause**)
- Applies window function over window
```scala
val overUnspecifiedFrame = sum('someColumn).over()
val overRange = rank over someWindow
```
- Use with `withColumn` or `select` operators
```scala
numbers.withColumn("max", max("num") over dividedBy2)
```
