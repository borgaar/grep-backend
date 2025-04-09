package org.ntnu.grepapp.dto.listing

import org.ntnu.grepapp.dto.LocationDTO

data class ListingUpdateRequest(
    val title: String,
    val description: String,
    val price: Int,
    val category: String,
    val location: LocationDTO,
    val bookmarked: Boolean,
    val imageIds: List<String>? = null,
)