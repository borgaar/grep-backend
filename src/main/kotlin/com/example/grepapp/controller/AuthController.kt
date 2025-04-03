package com.example.grepapp.controller

import com.example.grepapp.dto.AuthResponse
import com.example.grepapp.dto.UserRegisterRequest
import com.example.grepapp.model.RegisterUser
import com.example.grepapp.service.AuthService
import org.apache.logging.log4j.LogManager
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = ["http://localhost:5173"])
class AuthController(
    private val authService: AuthService,
) {
    private val logger = LogManager.getLogger(this::class::java);

    @PostMapping("/register")
    fun register(@RequestBody request: UserRegisterRequest): AuthResponse {
        val user = RegisterUser(
            phone = request.phone,
            passwordRaw = request.password,
            firstName = request.firstName,
            lastName = request.lastName,
        )
        val newUser = authService.register(user)
        return AuthResponse(authService.generateToken(newUser!!))
    }

    @PostMapping("/login")
    fun login(@RequestBody request: UserRegisterRequest): AuthResponse {
        val user = authService.login(request.phone, request.password)
        return AuthResponse(authService.generateToken(user!!))
    }
}
