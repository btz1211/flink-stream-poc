package com.urbancompass.listing.schema.building

import com.fasterxml.jackson.annotation.JsonProperty

/**
  * Copyright (C) 2019 Urban Compass, Inc.
  */

case class BuildingDimensions(@JsonProperty("area") area: Double,
                              @JsonProperty("depth") depth: Double,
                              @JsonProperty("width") width: Double)

case class BuildingDetail(@JsonProperty("units") units: Int,
                          @JsonProperty("residentialUnits") residentialUnits: Int,
                          @JsonProperty("floors") floors: Int,
                          @JsonProperty("yearBuilt") yearBuilt: Int,
                          @JsonProperty("buildingArea") buildingArea: Int,
                          @JsonProperty("residentialArea") residentialArea: Int,
                          @JsonProperty("buildingClass") buildingClass: String,
                          @JsonProperty("landUse") landUse: String,
                          @JsonProperty("description") description: String,

                         )

case class Building(@JsonProperty("buildingId") buildingId: String,
                    @JsonProperty("buildingIdSHA") buildingIdSha: String,
                    @JsonProperty("address") address: String,
                    @JsonProperty("street") street: String,
                    @JsonProperty("streetType") streetType: String,
                    @JsonProperty("zipCode") zipCode: String,
                    @JsonProperty("city") city: String,
                    @JsonProperty("county") county: String,
                    @JsonProperty("state") state: String,
                    @JsonProperty("geoId") geoId: String,
                    @JsonProperty("latitude") latitude: Double,
                    @JsonProperty("longitude") longitude: Double,
                    @JsonProperty("neighborhoods") neighborhoods: List[String],
                    @JsonProperty("buildingDimensions") buildingDimensions: BuildingDimensions,
                    @JsonProperty("details") buildingDetails: BuildingDetail)

case class BuildingData(buildingIdSha: String,
                        buildingData: Building,
                        buildingDataTimestamp: Long)
