package org.ntnu.grepapp.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Geographic coordinates of a location")
data class LocationDTO(
    @Schema(description = "Latitude coordinate", example = "63.4305")
    val lat: Double,

    @Schema(description = "Longitude coordinate", example = "10.3951")
    val lon: Double
)