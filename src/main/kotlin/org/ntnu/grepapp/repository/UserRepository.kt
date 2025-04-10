package org.ntnu.grepapp.repository

import org.ntnu.grepapp.model.User
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

/**
 * Repository class for handling database operations related to users.
 * Provides methods for finding, saving, and updating user data in the database.
 */
@Repository
class UserRepository(private var jdbc: JdbcTemplate) {

    /**
     * Row mapper for converting database rows to User objects.
     */
    private val rowMapper = RowMapper { rs, _ ->
        User(
            phone = rs.getString("phone"),
            passwordHash = rs.getString("password_hash"),
            firstName = rs.getString("first_name"),
            lastName = rs.getString("last_name"),
            role = rs.getString("role"),
        )
    }

    /**
     * Finds a user in the database by phone number.
     *
     * @param phone The phone number of the user to find
     * @return The User object if found, null if no user exists with the given phone number
     */
    fun find(phone: String): User? {
        val sql = "SELECT phone, password_hash, first_name, last_name, role FROM users WHERE phone = ?;";
        return try {
            jdbc.queryForObject(sql, rowMapper, phone)
        } catch (e: EmptyResultDataAccessException) {
            null
        }
    }

    /**
     * Saves a new user to the database.
     *
     * @param user The User object to save
     */
    fun save(user: User) {
        val sql = "INSERT INTO users (phone, password_hash, first_name, last_name) VALUES (?, ?, ?, ?);";
        jdbc.update(sql, user.phone, user.passwordHash, user.firstName, user.lastName);
    }

    /**
     * Updates an existing user in the database with new information.
     *
     * @param phone The current phone number of the user to update (used as primary key)
     * @param user The User object containing the updated information
     */
    fun overwrite(phone: String, user: User) {
        val sql = "UPDATE users SET phone = ?, password_hash = ?, first_name = ?, last_name = ? WHERE phone = ?;";
        jdbc.update(sql, user.phone, user.passwordHash, user.firstName, user.lastName, phone);
    }
}
