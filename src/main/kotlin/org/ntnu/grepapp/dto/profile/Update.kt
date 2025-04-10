package org.ntnu.grepapp.dto.profile

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Request for updating a user's profile information")
data class ProfileUpdateRequest(
    @Schema(description = "New phone number for the user", example = "+4712345678")
    val phone: String?,

    @Schema(description = "New first name for the user", example = "John")
    val firstName: String?,

    @Schema(description = "New last name for the user", example = "Doe")
    val lastName: String?,
)

@Schema(description = "Response after successfully updating a user's profile")
data class ProfileUpdateResponse(
    @Schema(description = "Updated phone number", example = "+4712345678")
    val phone: String,

    @Schema(description = "Updated first name", example = "John")
    val firstName: String,

    @Schema(description = "Updated last name", example = "Doe")
    val lastName: String,

    @Schema(description = "User's role", example = "user", allowableValues = ["user", "admin"])
    val role: String,
)