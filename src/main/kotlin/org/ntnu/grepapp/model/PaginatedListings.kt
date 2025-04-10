package org.ntnu.grepapp.model

data class PaginatedListings(
    val listings: List<Listing>,
    val totalListings: Int,
)
