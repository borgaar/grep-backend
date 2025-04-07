package org.ntnu.grepapp.dto

class ListingCreateRequest(
    val title: String,
    val description: String,
    val location: LocationDTO,
    val price: Int,
    val category: String,
)