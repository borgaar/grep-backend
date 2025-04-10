package org.ntnu.grepapp.service

import jakarta.persistence.EntityNotFoundException
import org.ntnu.grepapp.model.RegisterUser
import org.ntnu.grepapp.model.User
import org.ntnu.grepapp.repository.UserRepository
import org.ntnu.grepapp.security.JwtUtil
import org.ntnu.grepapp.security.UserClaims
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

/**
 * Service class that handles authentication and authorization operations.
 * Provides methods for user registration, login, token generation, and security context management.
 */
@Service
class AuthService(
    private val userRepository: UserRepository,
    private val jwtUtil: JwtUtil,
) {
    private val passwordEncoder = BCryptPasswordEncoder()

    /**
     * Retrieves the currently authenticated user from the security context.
     *
     * @return UserClaims object containing the user's phone number and role
     */
    fun getCurrentUser(): UserClaims {
        val authentication = SecurityContextHolder.getContext().authentication
        val phone = authentication.principal as String
        return UserClaims(phone, getRole())
    }

    /**
     * Retrieves the JWT token from the security context.
     *
     * @return The JWT token string
     */
    private fun getJWT(): String {
        val authentication = SecurityContextHolder.getContext().authentication
        val jwt = authentication.credentials as String
        return jwt
    }

    /**
     * Extracts the user role from the current JWT token.
     *
     * @return The user's role as a string
     */
    private fun getRole(): String {
        return jwtUtil.extractRoleFromToken(getJWT())
    }

    /**
     * Generates a JWT token for a user.
     *
     * @param user The User object for which to generate a token
     * @return A JWT token string containing the user's phone number and role
     */
    fun generateToken(user: User): String {
        return jwtUtil.generateToken(user.phone, user.role)
    }

    /**
     * Authenticates a user by phone number and password.
     *
     * @param phone The user's phone number
     * @param passwordRaw The user's raw (unhashed) password
     * @return The User object if authentication is successful, null otherwise
     */
    fun login(phone: String, passwordRaw: String): User? {
        val user = userRepository.find(phone)!!;
        return if (passwordEncoder.matches(passwordRaw, user.passwordHash)) {
            user;
        } else {
            null;
        }
    }

    /**
     * Registers a new user in the system.
     *
     * @param user A RegisterUser object containing registration information
     * @return The newly created User object if registration is successful, null if the phone number is already in use
     */
    fun register(user: RegisterUser): User? {
        val hashed = hashPassword(user.passwordRaw)
        if (userRepository.find(user.phone) != null) {
            return null
        }
        val newUser = User(
            phone = user.phone,
            passwordHash = hashPassword(user.passwordRaw),
            firstName = user.firstName,
            lastName = user.lastName,
        )
        userRepository.save(newUser);
        return newUser;
    }

    /**
     * Updates a user's password after verifying the old password.
     *
     * @param phone The user's phone number
     * @param oldPassword The user's current password for verification
     * @param newPassword The new password to set
     * @throws EntityNotFoundException If the user is not found
     * @throws IllegalArgumentException If the old password verification fails
     */
    fun updatePassword(phone: String, oldPassword: String, newPassword: String) {
        val user = userRepository.find(phone) ?: throw EntityNotFoundException()

        if (passwordEncoder.matches(oldPassword, user.passwordHash)) {
            user.passwordHash = hashPassword(newPassword)
            userRepository.overwrite(phone, user)
        } else {
            throw IllegalArgumentException("Old password is incorrect")
        }
    }

    /**
     * Hashes a raw password using BCrypt encryption.
     *
     * @param password The raw password to hash
     * @return The BCrypt hashed password string
     */
    private fun hashPassword(password: String): String {
        return passwordEncoder.encode(password)
    }
}