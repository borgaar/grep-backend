package org.ntnu.grepapp.dto.listing

import io.swagger.v3.oas.annotations.media.Schema
import org.ntnu.grepapp.dto.ListingDTO

@Schema(description = "Response containing paginated listings and total count")
data class GetPaginatedResponse(
    @Schema(description = "List of listings for the current page")
    val listings: List<ListingDTO>,

    @Schema(description = "Total number of listings matching the filter criteria", example = "42")
    val totalListings: Int,
)