package org.ntnu.grepapp.controller

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