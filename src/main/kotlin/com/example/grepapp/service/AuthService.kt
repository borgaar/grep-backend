package com.example.grepapp.service

import com.example.grepapp.model.RegisterUser
import com.example.grepapp.model.User
import com.example.grepapp.repository.UserRepository
import com.example.grepapp.security.JwtUtil
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val jwtUtil: JwtUtil,
) {
    private val passwordEncoder = BCryptPasswordEncoder()

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