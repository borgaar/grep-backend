package org.ntnu.grepapp.dto.profile

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Response containing user profile information")
data class ProfileGetResponse(
    @Schema(description = "User's phone number", example = "+4712345678")
    val phone: String,

    @Schema(description = "User's first name", example = "John")
    val firstName: String,

    @Schema(description = "User's last name", example = "Doe")
    val lastName: String,

    @Schema(description = "User's role", example = "user", allowableValues = ["user", "admin"])
    val role: String,
)