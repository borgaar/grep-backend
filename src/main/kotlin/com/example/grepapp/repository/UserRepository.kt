package com.example.grepapp.repository

import com.example.grepapp.model.User
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.queryForObject
import org.springframework.stereotype.Repository

@Repository
class UserRepository(private var jdbc: JdbcTemplate) {

    fun find(phone: String): User? {
        val sql = "TODO";
        return jdbc.queryForObject(sql);
    }

    fun save(user: User) {
        val sql = "TODO";
        jdbc.update(sql);
    }
}
