package com.example.grepapp.controller

import com.example.grepapp.dto.AuthResponse
import com.example.grepapp.dto.UserRegisterRequest
import com.example.grepapp.model.RegisterUser
import com.example.grepapp.service.AuthService
import org.apache.logging.log4j.LogManager
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = ["http://localhost:5173"])
class AuthController(
    private val authService: AuthService,
) {
    private val logger = LogManager.getLogger(this::class::java);

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@RequestBody request: UserRegisterRequest): ResponseEntity<AuthResponse> {
        val user = RegisterUser(
            phone = request.phone,
            passwordRaw = request.password,
            firstName = request.firstName,
            lastName = request.lastName,
        )
        val maybeUser = authService.register(user)
        val newUser = maybeUser ?: return ResponseEntity(HttpStatus.CONFLICT)
        return ResponseEntity(AuthResponse(authService.generateToken(newUser)), HttpStatus.CREATED)
    }

    @PostMapping("/login")
    fun login(@RequestBody request: UserRegisterRequest): ResponseEntity<AuthResponse> {
        val user = authService.login(request.phone, request.password) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        val body = AuthResponse(authService.generateToken(user))
        return ResponseEntity.ok(body)
    }
}
