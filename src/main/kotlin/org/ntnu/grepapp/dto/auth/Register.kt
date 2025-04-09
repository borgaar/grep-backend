package org.ntnu.grepapp.dto.auth

data class AuthRegisterRequest(
    val phone: String,
    val password: String,
    val firstName: String,
    val lastName: String
)

data class AuthRegisterResponse(
    val token: String,
    val firstName: String,
    val lastName: String,
    val role: String
)