package org.ntnu.grepapp.model

import java.util.*

data class UpdateListing(
    val title: String,
    val description: String,
    val price: Int,
    val category: String,
    val lat: Double,
    val lon: Double,
    val imageIds: List<UUID>?,
)