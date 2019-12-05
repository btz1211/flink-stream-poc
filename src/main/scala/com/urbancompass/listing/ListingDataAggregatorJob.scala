
import com.urbancompass.listing.stream.{BuildingDataStream, ListingDataStream}
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.contrib.streaming.state.RocksDBStateBackend
import org.apache.flink.streaming.api.CheckpointingMode
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.table.api.java.StreamTableEnvironment
import org.apache.flink.types.Row
/**
  * Copyright (C) 2019 Urban Compass, Inc.
  */
object ListingDataAggregatorJob {
  val BOOTSTRAP_SERVERS = "bootstrap-servers"
  val LISTING_DATA_TOPIC = "listing-data-topic"
  val BUILDING_DATA_TOPIC = "building-data-topic"
  val CONSUMER_GROUP_ID = "consumer-group-id"

  def main(args: Array[String]): Unit = {
    // get parameters
    val params = ParameterTool.fromArgs(args)
    val bootstrapServers = params.getRequired(BOOTSTRAP_SERVERS)
    val listingDataTopic = params.getRequired(LISTING_DATA_TOPIC)
    val buildingDataTopic = params.getRequired(BUILDING_DATA_TOPIC)
    val consumerGroupId = params.getRequired(CONSUMER_GROUP_ID)

    println(params.toMap)

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val tEnv = StreamTableEnvironment.create(env)
//    configEnv(env, "file:///Users/terry.zheng/apache-flink")

    val buildingTable = new BuildingDataStream(env, tEnv,
      bootstrapServers, buildingDataTopic, consumerGroupId).getTable

    val listingTable = new ListingDataStream(env, tEnv,
    bootstrapServers, listingDataTopic, consumerGroupId).getTable

    // join data
    val enrichedListingTable = tEnv.sqlQuery(
      """
        | SELECT
        |   l.listingIdSha,
        |   l.listingData,
        |   b.buildingData
        | FROM tListing l
        | LEFT JOIN tBuilding b ON l.buildingIdSha = b.buildingIdSha
      """.stripMargin
    )

    tEnv.registerTable("enriched_listing", enrichedListingTable)

    tEnv.toRetractStream(enrichedListingTable, classOf[Row])
      .print()

    env.execute()
  }

  def configEnv(env: StreamExecutionEnvironment, statePersistentPath: String): Unit = {
    val backend = new RocksDBStateBackend(statePersistentPath, true)

    env.setStateBackend(backend)

    env.enableCheckpointing(5000)
    // set mode to exactly-once (this is the default)
    env.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE)
    // make sure 500 ms of progress happen between checkpoints
    env.getCheckpointConfig.setMinPauseBetweenCheckpoints(500)
    // prevent the tasks from failing if an error happens in their checkpointing, the checkpoint will just be declined.
    env.getCheckpointConfig.setFailOnCheckpointingErrors(false)
    // allow only one checkpoint to be in progress at the same time
    env.getCheckpointConfig.setMaxConcurrentCheckpoints(1)

    val config = env.getConfig
    config.enableObjectReuse()
  }
}
