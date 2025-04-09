package org.ntnu.grepapp.dto.listing

import org.ntnu.grepapp.dto.ListingDTO

data class GetPaginatedResponse(
    val listings: List<ListingDTO>,
    val totalListings: Int,
)