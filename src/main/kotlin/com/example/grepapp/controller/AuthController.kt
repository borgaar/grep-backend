package com.example.grepapp.controller

import com.example.grepapp.model.User
import com.example.grepapp.repository.UserRepository
import com.example.grepapp.security.JwtUtil
import org.apache.logging.log4j.LogManager
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*

data class AuthRequest(val username: String, val password: String)
data class AuthResponse(val token: String)

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = ["http://localhost:5173"])
class AuthController(
    private val userRepository: UserRepository,
    private val jwtUtil: JwtUtil
) {
    private val passwordEncoder = BCryptPasswordEncoder()
    private val logger = LogManager.getLogger(this::class::java);

    @PostMapping("/register")
    fun register(@RequestBody request: AuthRequest): AuthResponse {
        println(request)
        if (userRepository.findByUsername(request.username).isPresent) {
            throw RuntimeException("User already exists")
        }
        val user = User(username = request.username, password = passwordEncoder.encode(request.password))
        userRepository.save(user)
        return AuthResponse(jwtUtil.generateToken(user.username))
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
