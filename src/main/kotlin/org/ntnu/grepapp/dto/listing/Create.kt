package org.ntnu.grepapp.dto.listing

import io.swagger.v3.oas.annotations.media.Schema
import org.ntnu.grepapp.dto.LocationDTO

@Schema(description = "Request for creating a new listing")
data class ListingCreateRequest(
    @Schema(description = "Title of the listing", example = "iPhone 13 Pro Max", required = true)
    val title: String,

    @Schema(description = "Detailed description of the listing", example = "Like new, only used for 3 months. Comes with original box and accessories.", required = true)
    val description: String,

    @Schema(description = "Geographic location of the listing", required = true)
    val location: LocationDTO,

    @Schema(description = "Price in NOK", example = "8000", required = true)
    val price: Int,

    @Schema(description = "Category name of the listing", example = "Electronics", required = true)
    val category: String,
)