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

## Scala Tidbits
```
scala> case class LongjingNumber(n: Long, name: String)  // Number is special in Scala.
defined class LongjingNumber

scala> Seq(LongjingNumber(1, "one"), LongjingNumber(2, "two"), LongjingNumber(3, "three")).foldLeft(0L) {
  case (partialSum, LongjingNumber(mrigesh, _)) => partialSum + mrigesh 
}
res1: Long = 10
```
