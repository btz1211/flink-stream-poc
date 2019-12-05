package com.urbancompass.listing.aggregator

import com.urbancompass.listing.schema.building.BuildingMessage
import com.urbancompass.listing.schema.listing.ListingMessage
import org.apache.flink.streaming.api.datastream.DataStream

/**
  * Copyright (C) 2019 Urban Compass, Inc.
  */
class ListingAggregatorV1 (val listingMessageStream: DataStream[ListingMessage],
                           val buildingMessageStream: DataStream[BuildingMessage]) {


}
