package com.urbancompass.listing.stream

import java.util.Properties

import com.urbancompass.listing.schema.building.{BuildingData, BuildingMessage, BuildingMessageSchema}
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala.createTypeInformation
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.java.StreamTableEnvironment

/**
  * Copyright (C) 2019 Urban Compass, Inc.
  */
class BuildingDataStream(env: StreamExecutionEnvironment,
                         tableEnv: StreamTableEnvironment,
                         bootstrapServers: String,
                         topic: String,
                         groupId: String) {

  private val properties = new Properties()
  properties.setProperty("bootstrap.servers", bootstrapServers)
  properties.setProperty("group.id", groupId)

  // setup kafka consumer for building data
  private val consumer = new FlinkKafkaConsumer[BuildingMessage](topic, new BuildingMessageSchema, properties)
  consumer.setCommitOffsetsOnCheckpoints(true)

  // create building data stream
  private val buildingDataStream = env.addSource(consumer)
    .map(message =>
      BuildingData(
        message.buildingIdSha,
        message.buildingData,
        message.lastUpdatedTimestamp
      )
    )
    .returns(createTypeInformation[BuildingData])
    .keyBy("buildingIdSha")

  tableEnv.registerDataStream("building", buildingDataStream)

  val buildingTemporalTable = tableEnv.sqlQuery(
    """
      | SELECT * FROM building b1
      |   WHERE buildingDataTimestamp = (
      |     SELECT MAX(buildingDataTimestamp) FROM building b2
      |     WHERE b1.buildingIdSha = b2.buildingIdSha)
    """.stripMargin
  )

  tableEnv.registerTable("tBuilding", buildingTemporalTable)

  // public method for retrieving building data stream
  def getTable: Table = buildingTemporalTable
}
