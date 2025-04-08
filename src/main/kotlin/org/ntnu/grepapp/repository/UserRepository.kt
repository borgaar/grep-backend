package org.ntnu.grepapp.repository

import org.ntnu.grepapp.model.User
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

@Repository
class UserRepository(private var jdbc: JdbcTemplate) {

    private val rowMapper = RowMapper { rs, _ ->
        User(
            phone = rs.getString("phone"),
            passwordHash = rs.getString("password_hash"),
            firstName = rs.getString("first_name"),
            lastName = rs.getString("last_name"),
            role = rs.getString("role"),
        )
    }

    fun find(phone: String): User? {
        val sql = "SELECT phone, password_hash, first_name, last_name, role FROM users WHERE phone = ?;";
        return try {
            jdbc.queryForObject(sql, rowMapper, phone)
        } catch (e: EmptyResultDataAccessException) {
            null
        }
    }

    fun save(user: User) {
        val sql = "INSERT INTO users (phone, password_hash, first_name, last_name) VALUES (?, ?, ?, ?);";
        jdbc.update(sql, user.phone, user.passwordHash, user.firstName, user.lastName);
    }

    fun overwrite(phone: String, user: User) {
        val sql = "UPDATE users SET phone = ?, password_hash = ?, first_name = ?, last_name = ? WHERE phone = ?;";
        jdbc.update(sql, user.phone, user.passwordHash, user.firstName, user.lastName, phone);
    }
}
