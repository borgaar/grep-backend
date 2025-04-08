package org.ntnu.grepapp.model

data class User(
    val phone: String,
    val passwordHash: String,
    val firstName: String,
    val lastName: String,
    val role: String = "user",
)
