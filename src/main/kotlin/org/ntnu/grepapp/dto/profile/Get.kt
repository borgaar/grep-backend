package org.ntnu.grepapp.dto.profile

data class ProfileGetResponse(
    val phone: String,
    val firstName: String,
    val lastName: String,
    val role: String,
)