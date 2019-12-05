package com.urbancompass.listing.stream

import java.util.Properties

import com.urbancompass.listing.schema.listing.{Listing, ListingData, ListingMessage, ListingMessageSchema}
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala.createTypeInformation
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.java.StreamTableEnvironment

/**
  * Copyright (C) 2019 Urban Compass, Inc.
  */
class ListingDataStream(env: StreamExecutionEnvironment,
                        tableEnv: StreamTableEnvironment,
                        bootstrapServers: String,
                        topic: String,
                        groupId: String) {

  private val schema = new ListingMessageSchema

  private val properties = new Properties()
  properties.setProperty("bootstrap.servers", bootstrapServers)
  properties.setProperty("group.id", groupId)

  private val consumer = new FlinkKafkaConsumer[ListingMessage](topic, schema, properties)
  consumer.setCommitOffsetsOnCheckpoints(true)

  // create building data stream
  private val listingDataStream = env.addSource(consumer)
    .map(message => {
      ListingData(
        message.listingIdSha,
        message.listingData.buildingIdSha,
        message.listingData,
        message.lastUpdatedTimestamp
      )
    })
    .returns(createTypeInformation[ListingData])
    .keyBy("listingIdSha")

  tableEnv.registerDataStream("listing", listingDataStream)

  val listingTemporalTable = tableEnv.sqlQuery(
    """
      | SELECT * FROM listing l1
      |   WHERE listingDataTimestamp = (
      |     SELECT MAX(listingDataTimestamp) FROM listing l2
      |     WHERE l1.listingIdSha = l2.listingIdSha)
    """.stripMargin
  )

  tableEnv.registerTable("tListing", listingTemporalTable)

  // public method for retrieving listing data stream
  def getTable : Table = listingTemporalTable
}
