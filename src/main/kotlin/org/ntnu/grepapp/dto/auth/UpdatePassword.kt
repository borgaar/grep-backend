package org.ntnu.grepapp.dto.auth

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Request body for password update")
data class AuthUpdatePasswordRequest(
    @Schema(description = "User's current password", example = "oldPassword123", required = true)
    val oldPassword: String,

    @Schema(description = "User's new password", example = "newPassword456", required = true)
    val newPassword: String,
)