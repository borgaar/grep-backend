package org.ntnu.grepapp.dto.auth

data class RegisterRequest(
    val phone: String,
    val password: String,
    val firstName: String,
    val lastName: String
)

data class RegisterResponse(
    val token: String,
    val firstName: String,
    val lastName: String,
    val role: String
)