# Spark Workshop Day 3

## Scala Tidbits
```
scala> case class LongjingNumber(n: Long, name: String)  // Number is special in Scala.
defined class LongjingNumber

scala> Seq(LongjingNumber(1, "one"), LongjingNumber(2, "two"), LongjingNumber(3, "three")).foldLeft(0L) {
  case (partialSum, LongjingNumber(mrigesh, _)) => partialSum + mrigesh 
}
res1: Long = 10
```
