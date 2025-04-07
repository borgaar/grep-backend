package org.ntnu.grepapp.dto.auth

data class LoginRequest(
    val phone: String,
    val password: String,
)

data class LoginResponse(
    val token: String,
    val firstName: String,
    val lastName: String,
    val role: String
)