package org.ntnu.grepapp.dto.profile

data class GetResponse(
    val phone: String,
    val firstName: String,
    val lastName: String,
    val role: String,
)