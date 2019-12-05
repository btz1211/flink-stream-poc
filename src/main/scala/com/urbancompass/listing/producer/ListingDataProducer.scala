package com.urbancompass.listing.producer

import com.fasterxml.jackson.databind
import com.fasterxml.jackson.databind.JsonNode
import com.urbancompass.messier.services.{LoadTransactionRequest, MessierServiceGRPC}
import io.grpc.{CallOptions, ManagedChannelBuilder}
import org.joda.time.DateTime

/**
  * Copyright (C) 2019 Urban Compass, Inc.
  */

object ListingDataProducer {
  def main(args: Array[String]): Unit = {

    // GRPC Client
    val channel = ManagedChannelBuilder.forTarget("localhost:9500").usePlaintext.build
    val client = new MessierServiceGRPC.Client(channel,
      CallOptions.DEFAULT.withWaitForReady, java.time.Duration.ofSeconds(10))

    // Kafka Publisher
    val publisher = new KafkaPublisher("listing-data-topic", "localhost:9092")

    // Jackson mapper
    val mapper = new databind.ObjectMapper()

    // retrieve listing
    val listingRevision = client.loadTransaction(
      new LoadTransactionRequest()
        .setMessierTransactionId(4852305568028694737L))
      .toJavaFuture
      .get()
      .getListingRevision

//    println(mapper.convertValue(listingRevision, classOf[JsonNode])
//      .toPrettyString)

    val listing = listingRevision.getListing

    val listingMessage = mapper.createObjectNode()
    listingMessage.put("listingIdSHA", listing.getListingIdSHA)
    listingMessage.put("listingData", mapper.convertValue(listing, classOf[JsonNode]))
    listingMessage.put("lastUpdatedTimestamp", new DateTime().getMillis)

    // publish data to topic
    publisher.publish(listing.getListingIdSHA, mapper.writeValueAsString(listingMessage))
  }
}
