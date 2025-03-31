package com.example.springbootoving.controller

import com.example.springbootoving.model.User
import com.example.springbootoving.repository.UserRepository
import com.example.springbootoving.security.JwtUtil
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = ["http://localhost:5173"])
class UserController(
    private val userRepository: UserRepository,
    private val jwtUtil: JwtUtil
) {
    @CrossOrigin
    @GetMapping("/profile")
    fun getProfile(@AuthenticationPrincipal username: String): User {
        // Fetch user from database
        return userRepository.findByUsername(username)
            .orElseThrow { RuntimeException("User not found") }
    }
}
