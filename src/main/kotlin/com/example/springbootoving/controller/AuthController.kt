package com.example.springbootoving.controller

import com.example.springbootoving.model.User
import com.example.springbootoving.repository.UserRepository
import com.example.springbootoving.security.JwtUtil
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*

data class AuthRequest(val username: String, val password: String)
data class AuthResponse(val token: String)

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val userRepository: UserRepository,
    private val jwtUtil: JwtUtil
) {
    private val passwordEncoder = BCryptPasswordEncoder()

    @PostMapping("/register")
    fun register(@RequestBody request: AuthRequest): String {
        if (userRepository.findByUsername(request.username).isPresent) {
            return "User already exists"
        }
        val user = User(username = request.username, password = passwordEncoder.encode(request.password))
        userRepository.save(user)
        return "User registered successfully"
    }

    @PostMapping("/login")
    fun login(@RequestBody request: AuthRequest): AuthResponse {
        val user = userRepository.findByUsername(request.username)
            .orElseThrow { RuntimeException("User not found") }

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw RuntimeException("Invalid password")
        }

        return AuthResponse(jwtUtil.generateToken(user.username))
    }
}
