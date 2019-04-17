name := "spark-project-template"

version := "0.1"

scalaVersion := "2.11.12"

// Almost always use spark-sql; stay away from spark-core.
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.4.0"
