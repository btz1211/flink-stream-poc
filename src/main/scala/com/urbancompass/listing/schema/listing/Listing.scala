package com.urbancompass.listing.schema.listing

import com.fasterxml.jackson.annotation.JsonProperty
import com.urbancompass.building.Building

/**
  * Copyright (C) 2019 Urban Compass, Inc.
  */

case class Listing(@JsonProperty("messierTransactionId") messierTransactionId: String,
                   @JsonProperty("__uc_id_sha__") ucIdSha: String,
                   @JsonProperty("listingIdSHA") listingIdSha: String,
                   @JsonProperty("ucAddressSha") ucAddressSha: String,
                   @JsonProperty("buildingIdSHA") buildingIdSha: String,
                   @JsonProperty("listingType") listingType: String,
                   @JsonProperty("street") street: String,
                   @JsonProperty("streetType") streetType: String,
                   @JsonProperty("address") address: String,
                   @JsonProperty("rawAddress") rawAddress: String,
                   @JsonProperty("unitNumber") unitNumber: String,
                   @JsonProperty("zipCode") zipCode: String,
                   @JsonProperty("city") city: String,
                   @JsonProperty("county") county: String,
                   @JsonProperty("state") state: String,
                   @JsonProperty("geoId") geoId: String,
                   @JsonProperty("bedrooms") bedrooms: Int,
                   @JsonProperty("bathrooms") bathrooms: Int,
                   @JsonProperty("amenities") amenities: List[String],
                   @JsonProperty("description") description: String)

case class ListingData(listingIdSha: String,
                       buildingIdSha: String,
                       listingData: Listing,
                       listingDataTimestamp: Long)
