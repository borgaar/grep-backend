package org.ntnu.grepapp.dto.auth

data class AuthLoginRequest(
    val phone: String,
    val password: String,
)

data class AuthLoginResponse(
    val token: String,
    val firstName: String,
    val lastName: String,
    val role: String
)