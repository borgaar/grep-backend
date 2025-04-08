package org.ntnu.grepapp.controller

import org.apache.logging.log4j.LogManager
import org.ntnu.grepapp.dto.auth.*
import org.ntnu.grepapp.model.RegisterUser
import org.ntnu.grepapp.service.AuthService
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
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<RegisterResponse> {
        val user = RegisterUser(
            phone = request.phone,
            passwordRaw = request.password,
            firstName = request.firstName,
            lastName = request.lastName,
        )
        val maybeUser = authService.register(user)
        val newUser = maybeUser ?: return ResponseEntity(HttpStatus.CONFLICT)
        val body = RegisterResponse(
            authService.generateToken(newUser), newUser.firstName, newUser.lastName, newUser.role
        )
        return ResponseEntity.ok(body)
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {
        val user = authService.login(request.phone, request.password) ?: return ResponseEntity(
            HttpStatus.NOT_FOUND
        )
        val body = LoginResponse(
            token = authService.generateToken(user),
            firstName = user.firstName,
            lastName = user.lastName,
            role = "user",
        )
        return ResponseEntity.ok(body)
    }

    @PutMapping("/password")
    fun updatePassword(
        @RequestBody request: UpdatePassword
    ): ResponseEntity<String> {
        try {
            authService.updatePassword(
                authService.getCurrentUser(), request.oldPassword, request.newPassword
            )
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.badRequest().body(e.message)
        }

        return ResponseEntity.ok("Password was updated")
    }
}
