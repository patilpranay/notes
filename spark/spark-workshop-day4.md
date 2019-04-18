# Spark Workshop Day 4

## Spark Tidbits
- Most basic level of execution is the task
- Views and tables are only representations of external data
  - Spark unifies access to external data by using views and tables
  
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
