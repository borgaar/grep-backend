package com.example.grepapp.mapping

import com.example.grepapp.dto.ListingDTO
import com.example.grepapp.dto.LocationDTO
import com.example.grepapp.model.Listing

fun toListingDTO(value: Listing): ListingDTO {
    return ListingDTO(
        id = value.id.toString(),
        title = value.title,
        description = value.description,
        location = LocationDTO(
            value.lat, value.lon
        ),
        price = value.price,
        category = ListingDTO.CategoryDTO(
            id = value.category.name,
            name = value.category.name,
        ),
        author = ListingDTO.AuthorDTO(
            id = value.author.phone,
            phone = value.author.phone,
            firstName = value.author.firstName,
            lastName = value.author.lastName,
        )
    )
}
