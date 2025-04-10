package org.ntnu.grepapp.mapping

import org.ntnu.grepapp.dto.CategoryDTO
import org.ntnu.grepapp.dto.ListingDTO
import org.ntnu.grepapp.dto.LocationDTO
import org.ntnu.grepapp.model.Listing

fun toListingDTO(value: Listing, isAuthor: Boolean = false): ListingDTO {
    return ListingDTO(
        id = value.id.toString(),
        title = value.title,
        description = value.description,
        location = LocationDTO(
            value.lat, value.lon
        ),
        price = value.price,
        createdAt = value.timestamp,
        updatedAt = value.updatedAt,
        category = CategoryDTO(
            name = value.category.name,
        ),
        author = ListingDTO.AuthorDTO(
            id = value.author.phone,
            phone = value.author.phone,
            firstName = value.author.firstName,
            lastName = value.author.lastName,
        ),
        isBookmarked = value.isBookmarked,
        imageIds = value.imageIds.map { it.toString() },

        isReserved = value.reservedBy != null,
        isSold = value.soldTo != null,
        reservedBy = if (isAuthor && value.reservedBy != null) ListingDTO.AuthorDTO(
            id = value.reservedBy.phone,
            phone = value.reservedBy.phone,
            firstName = value.reservedBy.firstName,
            lastName = value.reservedBy.lastName
        ) else null,
        soldTo = if (isAuthor && value.soldTo != null) ListingDTO.AuthorDTO(
            id = value.soldTo.phone,
            phone = value.soldTo.phone,
            firstName = value.soldTo.firstName,
            lastName = value.soldTo.lastName
        ) else null,
    )
}
