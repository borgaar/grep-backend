package org.ntnu.grepapp.dto.auth

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Request body for user registration")
data class AuthRegisterRequest(
    @Schema(description = "User's phone number", example = "+4712345678", required = true)
    val phone: String,

    @Schema(description = "User's password", example = "password123", required = true)
    val password: String,

    @Schema(description = "User's first name", example = "John", required = true)
    val firstName: String,

    @Schema(description = "User's last name", example = "Doe", required = true)
    val lastName: String
)

@Schema(description = "Response body for successful registration")
data class AuthRegisterResponse(
    @Schema(description = "JWT authentication token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    val token: String,

    @Schema(description = "User's first name", example = "John")
    val firstName: String,

    @Schema(description = "User's last name", example = "Doe")
    val lastName: String,

    @Schema(description = "User's role", example = "user", allowableValues = ["user", "admin"])
    val role: String
)