package org.ntnu.grepapp.dto.listing

import org.ntnu.grepapp.dto.LocationDTO

data class CreateRequest(
    val title: String,
    val description: String,
    val location: LocationDTO,
    val price: Int,
    val category: String,
)