package org.ntnu.grepapp.dto.profile

data class ProfileUpdateRequest(
    val phone: String?,
    val firstName: String?,
    val lastName: String?,
)

data class ProfileUpdateResponse(
      val phone: String,
      val firstName: String,
      val lastName: String,
      val role: String,
)