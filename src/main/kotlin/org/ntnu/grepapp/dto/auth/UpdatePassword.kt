package org.ntnu.grepapp.dto.auth

data class UpdatePasswordRequest(
    val oldPassword: String,
    val newPassword: String,
)