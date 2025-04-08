package org.ntnu.grepapp.model

data class ListingFilter(
    val priceLower: Int?,
    val priceUpper: Int?,
    val category: String?,
    val titleQuery: String?,
    val sorting: String?,
    val sortingDirection: String?,
)