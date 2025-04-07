package org.ntnu.grepapp.dto

data class ListingGetPaginatedRequest(
    val page: Int,
    val pageSize: Int,
)
