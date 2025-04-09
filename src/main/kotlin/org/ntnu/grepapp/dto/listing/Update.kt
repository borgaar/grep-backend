package org.ntnu.grepapp.dto.listing

import org.ntnu.grepapp.dto.LocationDTO

data class UpdateRequest(
    val title: String,
    val description: String,
    val price: Int,
    val category: String,
    val location: LocationDTO,
    val bookmarked: Boolean
)