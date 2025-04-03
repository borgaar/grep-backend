package com.example.grepapp.dto

data class GetListingResponse(
    val id: String,
    val title: String,
    val description: String,
    val location: LocationDTO,
    val price: Int,
    val category: CategoryDTO,
    val author: AuthorDTO
) {
    data class LocationDTO(
        val lat: Double,
        val lng: Double
    )
    data class CategoryDTO(
        val id: String,
        val name: String
    )
    data class AuthorDTO(
        val id: String,
        val phone: String,
        val first_name: String,
        val last_name: String,
    )
}