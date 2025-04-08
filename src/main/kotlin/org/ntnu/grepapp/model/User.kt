package org.ntnu.grepapp.model

data class User(
    var phone: String,
    var passwordHash: String,
    var firstName: String,
    var lastName: String,
    val role: String = "user"
)
