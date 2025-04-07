package org.ntnu.grepapp.service

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

    fun generateToken(user: User): String {
        return jwtUtil.generateToken(user.phone)
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
        val hashed = passwordEncoder.encode(user.passwordRaw)
        println(hashed)
        if (userRepository.find(user.phone) != null) {
            return null
        }
        val newUser = User(
            phone = user.phone,
            passwordHash = passwordEncoder.encode(user.passwordRaw),
            firstName = user.firstName,
            lastName = user.lastName,
        )
        userRepository.save(newUser);
        return newUser;
    }
}