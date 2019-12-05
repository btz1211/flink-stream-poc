name := "listing-data-aggregator"

version := "0.1"

organization := "com.urbancompass.listing"

enablePlugins(AkkaGrpcPlugin)

resolvers ++= Seq(
  "Apache Development Snapshot Repository" at "https://repository.apache.org/content/repositories/snapshots/",
  Resolver.mavenLocal
)

val flinkVersion = "1.8.1"


val flinkDependencies = Seq(
  "org.apache.flink" %% "flink-scala" % flinkVersion % "provided",
  "org.apache.flink" %% "flink-streaming-scala" % flinkVersion % "provided",
  "org.apache.flink" %% "flink-connector-kafka" % flinkVersion,
  "org.apache.flink" % "flink-json" % flinkVersion,
  "org.apache.flink" %% "flink-table-planner" % flinkVersion,
  "org.apache.flink" %% "flink-table-api-scala-bridge" % flinkVersion,
  "org.apache.flink" % "flink-avro" % flinkVersion,
  "org.apache.flink" % "flink-statebackend-rocksdb_2.11" % flinkVersion,
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.10.1",
  "com.sksamuel.avro4s" %% "avro4s-core" % "3.0.0-RC2"
)


lazy val root = (project in file(".")).
  settings(
    libraryDependencies ++= flinkDependencies,
    libraryDependencies += "com.twitter" %% "util-core" % "19.7.0"
  )

