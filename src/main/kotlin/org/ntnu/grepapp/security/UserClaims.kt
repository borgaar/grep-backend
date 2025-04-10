package org.ntnu.grepapp.security

data class UserClaims(
    val id: String,
    private val role: String,
) {
    fun isNotAdmin(): Boolean {
        return !isAdmin()
    }
    fun isAdmin(): Boolean {
        return role == "admin"
    }
}
