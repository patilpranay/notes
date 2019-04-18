# Spark Workshop Day 3

## Aggregate Functions
- **Aggregate functions** accept a group of records as input instead of a single record
  - Unlike regular functions, aggregate functions act on a group of records
- **agg** applies an aggregate function to recrods in Dataset
```
val ds = spark.range(10)
ds.agg(sum('id) as "sum")
```
- **groupBy** groups records in Dataset per discriminator function
  - Is it possible to do a groupBy without accepting column parameter? Yes, it's equivalent to `dataset.agg`
- **groupByKey** similar to **groupBy** operator, but gives typed interface
  - Avoid this because it involves extra loading onto JVM
- What does typed and untyped mean?
  - In Scala, everything is typed.
  - Performance is much better when you used untyped API
    - This means that you are using DataFrames (and Rows) instead of Datasets

## Join Operators
- You can join two Datasets using join operators
  - **join** for untyped Row-based joins
  - Type-preserving **joinWith**
  - **crossJoin** for explicit cartesian joins
- Join conditions in **join** or **filter/where** operators
```
// equivalent to left("id") === right("id")
left.join(right, "id")
left.join(right, "id", "anti")  // explicit join type
left.join(right).filter(left("id") === right("id")
left.join(right).where(left("id") === right("id")
```
|SQL|Names|
|---|-----|
|CROSS|cross|
|INNER|inner|
|FULL OUTER|outer, full, fullouter|
|LEFT ANTI|leftanti|
|LEFT OUTER|leftouter, left|
|LEFT SEMI|leftsemi|
|RIGHT OUTER|rightouter, right|

### Broadcast Join
- Means that the left hand side or right hand side of the join is (by default) < 10 MB
```
left.join(broadcast(right), "token")
```
- Sends the dataset to all of the executors so that every tasks run on the executor does not need to go load the dataset into memory (JVM) every single time.
- Using the `broadcast` keyword is to force the broadcast of a dataset
  - Spark does broadcasting of smaller datasets automatically

### Join Optimizations
#### Bucketing
- When you do join of two tables that are above broadcast join size, Spark does a SortMerge join
- You can avoid SortMerge join by *properly storing your data*
  - If the two different datasets are split into the same number of buckets using the same partitioner, then a join operation does not need to do a SortMerge join
- Requirements:
  - Number of buckets is same on both sides
  - `DataSet.bucketBy` is the same on both sides

#### Join Reordering
- Need to enable cost-based join reordering (not enabled by default)
  - Contributed by Huawei
  - Code was very ugly and large - therefore not a lot of testing done on cost-based join reordering
  - Therefore this feature is not enabled by default

## Scala Tidbits
```
scala> case class LongjingNumber(n: Long, name: String)  // Number is special in Scala.
defined class LongjingNumber

scala> Seq(LongjingNumber(1, "one"), LongjingNumber(2, "two"), LongjingNumber(3, "three")).foldLeft(0L) {
  case (partialSum, LongjingNumber(mrigesh, _)) => partialSum + mrigesh 
}
res1: Long = 10
```
