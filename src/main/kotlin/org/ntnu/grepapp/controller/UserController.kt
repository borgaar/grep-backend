package org.ntnu.grepapp.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.apache.logging.log4j.LogManager
import org.ntnu.grepapp.dto.profile.ProfileGetResponse
import org.ntnu.grepapp.dto.profile.ProfileUpdateRequest
import org.ntnu.grepapp.dto.profile.ProfileUpdateResponse
import org.ntnu.grepapp.service.AuthService
import org.ntnu.grepapp.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for user-related operations.
 * Provides endpoints for retrieving and updating user profile information.
 */
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = ["http://localhost:5173"])
@Tag(name = "Users", description = "API for managing user profiles")
class UserController (
    private val userService: UserService,
    private val authService: AuthService,
) {
    private val logger = LogManager.getLogger(this::class::java);

    /**
     * Retrieves the profile information of the currently authenticated user.
     *
     * @return ResponseEntity containing:
     *         - A ProfileGetResponse with the user's information if found
     *         - HTTP 404 NOT_FOUND status if the user profile doesn't exist
     */
    @Operation(
        summary = "Get user profile",
        description = "Retrieves the profile information of the currently authenticated user"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved user profile",
            content = [Content(mediaType = "application/json",
                schema = Schema(implementation = ProfileGetResponse::class))]
        ),
        ApiResponse(
            responseCode = "404",
            description = "User profile not found"
        )
    ])
    @GetMapping("/profile")
    fun getProfile(): ResponseEntity<ProfileGetResponse> {
        val user = userService.getProfile(authService.getCurrentUser().id) ?: return ResponseEntity(
            HttpStatus.NOT_FOUND
        );
        val body = ProfileGetResponse(
            user.phone,
            user.firstName,
            user.lastName,
            user.role
        )
        return ResponseEntity.ok(body);
    }

    /**
     * Updates the profile information of the currently authenticated user.
     *
     * @param body A ProfileUpdateRequest containing the new profile information
     * @return ResponseEntity containing:
     *         - A ProfileUpdateResponse with the updated user information if successful
     *         - HTTP 404 NOT_FOUND status if the user profile doesn't exist
     */
    @Operation(
        summary = "Update user profile",
        description = "Updates the profile information of the currently authenticated user"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "User profile successfully updated",
            content = [Content(mediaType = "application/json",
                schema = Schema(implementation = ProfileUpdateResponse::class))]
        ),
        ApiResponse(
            responseCode = "404",
            description = "User profile not found"
        )
    ])
    @PatchMapping("/profile")
    fun updateProfile(@RequestBody body: ProfileUpdateRequest): ResponseEntity<ProfileUpdateResponse> {
        val updated = userService.updateProfile(
            authService.getCurrentUser().id, body.phone, body.firstName, body.lastName
        )

        if (updated == null) {
            return ResponseEntity(HttpStatus.NOT_FOUND);
        }
        val out = ProfileUpdateResponse(
            updated.phone,
            updated.firstName,
            updated.lastName,
            updated.role
        )
        return ResponseEntity.ok(out);
    }
}