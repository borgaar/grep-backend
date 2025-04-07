package org.ntnu.grepapp.dto

data class ListingDTO(
    val id: String,
    val title: String,
    val description: String,
    val location: LocationDTO,
    val price: Int,
    val category: CategoryDTO,
    val author: AuthorDTO
) {

    data class CategoryDTO(
        val id: String,
        val name: String
    )

    data class AuthorDTO(
        val id: String,
        val phone: String,
        val firstName: String,
        val lastName: String,
    )
}