package org.ntnu.grepapp.dto.listing

import io.swagger.v3.oas.annotations.media.Schema
import org.ntnu.grepapp.dto.LocationDTO

@Schema(description = "Request for updating an existing listing")
data class ListingUpdateRequest(
    @Schema(description = "New title of the listing", example = "iPhone 13 Pro Max - PRICE REDUCED", required = true)
    val title: String,

    @Schema(description = "New description of the listing", example = "Like new, only used for 3 months. Comes with original box and accessories. PRICE REDUCED!", required = true)
    val description: String,

    @Schema(description = "New price in NOK", example = "7500", required = true)
    val price: Int,

    @Schema(description = "New category name of the listing", example = "Electronics", required = true)
    val category: String,

    @Schema(description = "New geographic location of the listing", required = true)
    val location: LocationDTO,

    @Schema(description = "Whether the current user wants to bookmark this listing", required = true)
    val bookmarked: Boolean,

    @Schema(description = "New list of image IDs to associate with this listing")
    val imageIds: List<String>? = null,
)