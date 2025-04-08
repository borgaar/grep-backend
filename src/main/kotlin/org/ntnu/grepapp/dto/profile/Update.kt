package org.ntnu.grepapp.dto.profile

data class UpdateRequest(
    val phone: String?,
    val firstName: String?,
    val lastName: String?,
)

data class UpdateResponse(
      val phone: String,
      val firstName: String,
      val lastName: String,
      val role: String,
)