package org.ntnu.grepapp.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "Detailed information about a marketplace listing")
data class ListingDTO(
    @Schema(description = "Unique identifier of the listing", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    val id: String,

    @Schema(description = "Title of the listing", example = "iPhone 13 Pro Max")
    val title: String,

    @Schema(description = "Detailed description of the listing", example = "Like new, only used for 3 months. Comes with original box and accessories.")
    val description: String,

    @Schema(description = "Geographic location of the listing")
    val location: LocationDTO,

    @Schema(description = "Price in NOK", example = "8000")
    val price: Int,

    @Schema(description = "Date and time when the listing was created")
    val createdAt: LocalDateTime,

    @Schema(description = "Date and time when the listing was last updated")
    val updatedAt: LocalDateTime,

    @Schema(description = "Category of the listing")
    val category: CategoryDTO,

    @Schema(description = "Information about the listing's author")
    val author: AuthorDTO,

    @Schema(description = "Whether the listing is currently reserved by a user")
    val isReserved: Boolean,

    @Schema(description = "Whether the listing has been sold")
    val isSold: Boolean,

    @Schema(description = "List of image IDs associated with this listing")
    val imageIds: List<String>,

    @Schema(description = "Whether the current user has bookmarked this listing")
    val isBookmarked: Boolean,

    @Schema(description = "Information about the user who reserved the listing (only visible to the author)")
    val reservedBy: AuthorDTO?,

    @Schema(description = "Information about the user who bought the listing (only visible to the author)")
    val soldTo: AuthorDTO?,
) {
    @Schema(description = "Information about a user in the context of a listing")
    data class AuthorDTO(
        @Schema(description = "Unique identifier of the user", example = "user123")
        val id: String,

        @Schema(description = "Phone number of the user", example = "+4712345678")
        val phone: String,

        @Schema(description = "First name of the user", example = "John")
        val firstName: String,

        @Schema(description = "Last name of the user", example = "Doe")
        val lastName: String,
    )
}