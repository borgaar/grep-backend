package org.ntnu.grepapp.dto

import java.time.LocalDateTime

data class BookmarkedListingDTO(
    val bookmarkedAt: LocalDateTime,
    val listing: ListingDTO
)
