package org.ntnu.grepapp.dto.auth

data class UpdatePassword(
    val oldPassword: String,
    val newPassword: String,
)