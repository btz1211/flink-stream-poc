package com.urbancompass.listing.schema.listing

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.{DefaultScalaModule, ScalaObjectMapper}
import org.apache.flink.api.common.serialization.DeserializationSchema
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.scala._

/**
  * Copyright (C) 2019 Urban Compass, Inc.
  */

class ListingMessageSchema extends DeserializationSchema[ListingMessage] {
  override def deserialize(message: Array[Byte]): ListingMessage = {
    val objectMapper = new ObjectMapper() with ScalaObjectMapper
    objectMapper.registerModule(DefaultScalaModule)

    objectMapper
      .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
      .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false)
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    objectMapper.readValue(message, classOf[ListingMessage])
  }

  override def isEndOfStream(nextElement: ListingMessage): Boolean = false

  override def getProducedType: TypeInformation[ListingMessage] =
    createTypeInformation[ListingMessage]

}

case class ListingMessage(@JsonProperty("listingIdSHA") listingIdSha: String,
                          @JsonProperty("listingData") listingData: Listing,
                          @JsonProperty("lastUpdatedTimestamp") lastUpdatedTimestamp: Long)
