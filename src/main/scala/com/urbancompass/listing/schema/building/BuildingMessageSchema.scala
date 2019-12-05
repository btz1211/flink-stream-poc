package com.urbancompass.listing.schema.building

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.{DefaultScalaModule, ScalaObjectMapper}
import org.apache.flink.api.common.serialization.DeserializationSchema
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.scala.createTypeInformation

/**
  * Copyright (C) 2019 Urban Compass, Inc.
  */
class BuildingMessageSchema extends DeserializationSchema[BuildingMessage] {
  override def deserialize(message: Array[Byte]): BuildingMessage = {
    val objectMapper = new ObjectMapper() with ScalaObjectMapper
    objectMapper.registerModule(DefaultScalaModule)

    objectMapper
      .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
      .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false)
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    objectMapper.readValue(message, classOf[BuildingMessage])
  }

  override def isEndOfStream(nextElement: BuildingMessage): Boolean = false

  override def getProducedType: TypeInformation[BuildingMessage] =
    createTypeInformation[BuildingMessage]

}

case class BuildingMessage(@JsonProperty("buildingIdSHA") buildingIdSha: String,
                          @JsonProperty("buildingData") buildingData: Building,
                          @JsonProperty("lastUpdatedTimestamp") lastUpdatedTimestamp: Long)

