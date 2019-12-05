package com.urbancompass.listing.producer

import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

/**
  * Copyright (C) 2019 Urban Compass, Inc.
  */
class KafkaPublisher(topic: String, servers: String) {
  val SERIALIZER: String = "org.apache.kafka.common.serialization.StringSerializer"

  val properties = new Properties()
  properties.put("bootstrap.servers", servers)
  properties.put("key.serializer", SERIALIZER)
  properties.put("value.serializer", SERIALIZER)


  def publish(key: String, message: String): Unit = {
    val producer = new KafkaProducer[String, String](properties)
    val record = new ProducerRecord[String, String](topic, key, message)
    producer.send(record)
    producer.close()
  }
}
