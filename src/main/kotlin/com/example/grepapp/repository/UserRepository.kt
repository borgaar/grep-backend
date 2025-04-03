package com.example.grepapp.repository

import com.example.grepapp.model.User
import org.springframework.dao.DataAccessException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.queryForObject
import org.springframework.stereotype.Repository

@Repository
class UserRepository(private var jdbc: JdbcTemplate) {

    private val rowMapper = RowMapper { rs, _ ->
        User(
            phone = rs.getString("phone"),
            passwordHash = rs.getString("password_hash"),
            firstName = rs.getString("first_name"),
            lastName = rs.getString("last_name")
        )
    }

    fun find(phone: String): User? {
        val sql = "SELECT phone, password_hash, first_name, last_name FROM users WHERE phone = ?;";
        return try { jdbc.queryForObject(sql, rowMapper, phone) }
        catch (e: EmptyResultDataAccessException) { null }
    }

    fun save(user: User) {
        val sql = "INSERT INTO users (phone, password_hash, first_name, last_name) VALUES (?, ?, ?, ?);";
        jdbc.update(sql, user.phone, user.passwordHash, user.firstName, user.lastName);
    }
}
