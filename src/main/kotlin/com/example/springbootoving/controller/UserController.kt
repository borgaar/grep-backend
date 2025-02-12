package com.example.springbootoving.controller

import com.example.springbootoving.model.User
import com.example.springbootoving.repository.UserRepository
import com.example.springbootoving.security.JwtUtil
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping()
class UserController(
    private val userRepository: UserRepository,
    private val jwtUtil: JwtUtil
) {
    @GetMapping("/profile")
    fun getProfile(@RequestHeader("Authorization") token: String): User {
        // Remove "Bearer " prefix
        val jwt = token.removePrefix("Bearer ")

        // Extract username from JWT
        val username = jwtUtil.extractUsername(jwt)

        // Fetch user from database
        return userRepository.findByUsername(username)
            .orElseThrow { RuntimeException("User not found") }
    }
}
