package com.example.grepapp.repository

import com.example.grepapp.model.User
import org.springframework.dao.DataAccessException
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
        val sql = "SELECT '3' AS phone, 's' AS password_hash, 'd' AS first_name, 'f' AS last_name;";
        return jdbc.queryForObject(sql, rowMapper)
    }

    fun save(user: User) {
        val sql = "SELECT 1;";
        jdbc.update(sql);
    }
}
