package com.example.grepapp.model

import java.util.UUID

data class User(
    val phone: String,
    val passwordHash: String,
    val firstName: String,
    val lastName: String,
)
