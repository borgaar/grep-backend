package org.ntnu.grepapp.dto

import java.time.LocalDateTime

data class ListingDTO(
    val id: String,
    val title: String,
    val description: String,
    val location: LocationDTO,
    val price: Int,
    val createdAt: LocalDateTime,
    val category: CategoryDTO,
    val author: AuthorDTO,
    // user-specific
    val isBookmarked: Boolean
) {
    data class AuthorDTO(
        val id: String,
        val phone: String,
        val firstName: String,
        val lastName: String,
    )
}