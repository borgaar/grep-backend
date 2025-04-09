package org.ntnu.grepapp.model

import java.time.LocalDateTime
import java.util.*

data class Listing(
    val id: UUID,
    val title: String,
    val description: String,
    val author: ListingAuthor,
    val price: Int,
    val timestamp: LocalDateTime,
    val updatedAt: LocalDateTime,
    val lat: Double,
    val lon: Double,
    val category: Category,
    var imageIds: List<UUID>,
    // user-specific
    val isBookmarked: Boolean,
    val reservedBy: User?,
    val soldTo: User?
)
