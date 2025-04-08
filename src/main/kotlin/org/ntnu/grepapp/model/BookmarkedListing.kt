package org.ntnu.grepapp.model

import java.time.LocalDateTime

data class BookmarkedListing(
    val listing: Listing,
    val bookmarkedAt: LocalDateTime,
)
