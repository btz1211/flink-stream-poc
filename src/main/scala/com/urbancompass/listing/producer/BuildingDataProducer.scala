package com.urbancompass.listing.producer

import com.fasterxml.jackson.databind
import com.fasterxml.jackson.databind.JsonNode
import com.urbancompass.messier.services.{LoadBuildingRequest, MessierServiceGRPC}
import io.grpc.{CallOptions, ManagedChannelBuilder}
import org.joda.time.DateTime

/**
  * Copyright (C) 2019 Urban Compass, Inc.
  */
object BuildingDataProducer {
  def main(args: Array[String]): Unit = {

    // GRPC Client
    val channel = ManagedChannelBuilder.forTarget("localhost:9500").usePlaintext.build
    val client = new MessierServiceGRPC.Client(channel,
      CallOptions.DEFAULT.withWaitForReady, java.time.Duration.ofSeconds(10))

    // Kafka Publisher
    val publisher = new KafkaPublisher("building-data-topic", "localhost:9092")

    // Jackson mapper
    val mapper = new databind.ObjectMapper()

    // retrieve building
    val building = client.loadBuilding(
      new LoadBuildingRequest().setUcAddressSha("8b4aa7d2ee91675032c598b2e3cef5be83938f65"))
      .toJavaFuture
      .get()
      .getBuilding

    val buildingMessage = mapper.createObjectNode()
    buildingMessage.put("buildingIdSHA", building.getBuildingIdSHA)
    buildingMessage.put("buildingData", mapper.convertValue(building, classOf[JsonNode]))
    buildingMessage.put("lastUpdatedTimestamp", new DateTime().getMillis)

    // publish data to topic
    publisher.publish(building.getBuildingIdSHA, mapper.writeValueAsString(buildingMessage))
  }
}
