package org.ntnu.grepapp.controller


import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.apache.logging.log4j.LogManager
import org.ntnu.grepapp.dto.auth.*
import org.ntnu.grepapp.model.RegisterUser
import org.ntnu.grepapp.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for authentication-related operations.
 * Provides endpoints for user registration, login, and password management.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = ["http://localhost:5173"])
@Tag(name = "Authentication", description = "Authentication API for user registration, login, and password management")
class AuthController(
    private val authService: AuthService,
) {
    private val logger = LogManager.getLogger(this::class::java);

    /**
     * Registers a new user in the system.
     *
     * @param request An AuthRegisterRequest containing registration information (phone, password, firstName, lastName)
     * @return ResponseEntity containing:
     *         - An AuthRegisterResponse with the generated token and user details if registration is successful
     *         - HTTP 409 CONFLICT status if a user with the same phone number already exists
     */
    @Operation(
        summary = "Register a new user",
        description = "Creates a new user account with the provided information"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "User successfully registered",
            content = [Content(mediaType = "application/json", schema = Schema(implementation = AuthRegisterResponse::class))]
        ),
        ApiResponse(
            responseCode = "409",
            description = "User with this phone number already exists",
            content = [Content(mediaType = "application/json")]
        )
    ])
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@RequestBody request: AuthRegisterRequest): ResponseEntity<AuthRegisterResponse> {
        val user = RegisterUser(
            phone = request.phone,
            passwordRaw = request.password,
            firstName = request.firstName,
            lastName = request.lastName,
        )
        val maybeUser = authService.register(user)
        val newUser = maybeUser ?: return ResponseEntity(HttpStatus.CONFLICT)
        val body = AuthRegisterResponse(
            authService.generateToken(newUser),
            newUser.firstName,
            newUser.lastName,
            newUser.role
        )
        return ResponseEntity.ok(body)
    }

    /**
     * Authenticates a user and generates a JWT token.
     *
     * @param request An AuthLoginRequest containing login credentials (phone, password)
     * @return ResponseEntity containing:
     *         - An AuthLoginResponse with the generated token and user details if authentication is successful
     *         - HTTP 404 NOT_FOUND status if the user doesn't exist or credentials are invalid
     */
    @Operation(
        summary = "Login user",
        description = "Authenticates a user with phone number and password, and returns a JWT token"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "User successfully authenticated",
            content = [Content(mediaType = "application/json", schema = Schema(implementation = AuthLoginResponse::class))]
        ),
        ApiResponse(
            responseCode = "404",
            description = "User not found or invalid credentials",
            content = [Content(mediaType = "application/json")]
        )
    ])
    @PostMapping("/login")
    fun login(@RequestBody request: AuthLoginRequest): ResponseEntity<AuthLoginResponse> {
        val user = authService.login(request.phone, request.password) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        val body = AuthLoginResponse(
            token = authService.generateToken(user),
            firstName = user.firstName,
            lastName = user.lastName,
            role = user.role,
        )
        return ResponseEntity.ok(body)
    }

    /**
     * Updates the password for the currently authenticated user.
     *
     * @param request An AuthUpdatePasswordRequest containing the old and new passwords
     * @return ResponseEntity with HTTP status:
     *         - OK if the password was successfully updated
     *         - BAD_REQUEST if the old password is incorrect
     */
    @Operation(
        summary = "Update password",
        description = "Updates the password of the currently authenticated user"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Password successfully updated",
            content = [Content(mediaType = "application/json")]
        ),
        ApiResponse(
            responseCode = "400",
            description = "Old password is incorrect",
            content = [Content(mediaType = "application/json")]
        ),
        ApiResponse(
            responseCode = "401",
            description = "User is not authenticated",
            content = [Content(mediaType = "application/json")]
        )
    ])
    @PutMapping("/password")
    fun updatePassword(
        @RequestBody request: AuthUpdatePasswordRequest
    ): ResponseEntity<Unit> {
        try {
            authService.updatePassword(
                authService.getCurrentUser().id, request.oldPassword, request.newPassword
            )
        } catch (e: IllegalArgumentException) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }

        return ResponseEntity(HttpStatus.OK)
    }
}
