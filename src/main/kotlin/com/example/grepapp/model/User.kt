package com.example.grepapp.model

data class User(
    val phone: String,
    val passwordHash: String,
    val firstName: String,
    val lastName: String,
)
