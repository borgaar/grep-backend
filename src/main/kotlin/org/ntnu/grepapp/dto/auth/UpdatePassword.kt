package org.ntnu.grepapp.dto.auth

data class AuthUpdatePasswordRequest(
    val oldPassword: String,
    val newPassword: String,
)