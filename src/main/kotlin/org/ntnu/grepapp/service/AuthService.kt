package org.ntnu.grepapp.service

import jakarta.persistence.EntityNotFoundException
import org.ntnu.grepapp.model.RegisterUser
import org.ntnu.grepapp.model.User
import org.ntnu.grepapp.repository.UserRepository
import org.ntnu.grepapp.security.JwtUtil
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val jwtUtil: JwtUtil,
) {
    private val passwordEncoder = BCryptPasswordEncoder()

    fun getCurrentUser(): String {
        val authentication = SecurityContextHolder.getContext().authentication
        val phone = authentication.principal as String
        return phone
    }

    private fun getJWT(): String {
        val authentication = SecurityContextHolder.getContext().authentication
        val jwt = authentication.credentials as String
        return jwt
    }

    fun getRole(): String {
        return jwtUtil.extractRoleFromToken(getJWT())
    }

    fun generateToken(user: User): String {
        return jwtUtil.generateToken(user.phone, user.role)
    }

    fun login(phone: String, passwordRaw: String): User? {
        val user = userRepository.find(phone)!!;
        return if (passwordEncoder.matches(passwordRaw, user.passwordHash)) {
            user;
        } else {
            null;
        }
    }

    fun register(user: RegisterUser): User? {
        val hashed = hashPassword(user.passwordRaw)
        println(hashed)
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

    fun updatePassword(phone: String, oldPassword: String, newPassword: String) {
        val user = userRepository.find(phone) ?: throw EntityNotFoundException()

        if (passwordEncoder.matches(oldPassword, user.passwordHash)) {
            user.passwordHash = hashPassword(newPassword)
            userRepository.overwrite(phone, user)
        } else {
            throw IllegalArgumentException("Old password is incorrect")
        }
    }

    private fun hashPassword(password: String): String {
        return passwordEncoder.encode(password)
    }
}