package org.ntnu.grepapp.dto

data class UserRegisterRequest(val phone: String, val password: String, val firstName: String, val lastName: String)