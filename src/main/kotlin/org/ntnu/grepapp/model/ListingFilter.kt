package org.ntnu.grepapp.model

data class ListingFilter(
    val priceLower: Int?,
    val priceUpper: Int?,
    val categories: List<String>,
    val searchQuery: String?,
    val sorting: String?,
    val sortingDirection: String?,
)