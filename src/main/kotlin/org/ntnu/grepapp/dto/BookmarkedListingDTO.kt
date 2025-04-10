package org.ntnu.grepapp.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "Information about a bookmarked listing")
data class BookmarkedListingDTO(
    @Schema(description = "When the listing was bookmarked")
    val bookmarkedAt: LocalDateTime,

    @Schema(description = "Details of the bookmarked listing")
    val listing: ListingDTO
)