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

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = ["http://localhost:5173"])
class UserController (
    private val userService: UserService,
    private val authService: AuthService,
) {
    private val logger = LogManager.getLogger(this::class::java);

    @GetMapping("/profile")
    fun getProfile(): ResponseEntity<ProfileGetResponse> {
        val user = userService.getProfile(authService.getCurrentUser()) ?: return ResponseEntity(
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

    @PatchMapping("/profile")
    fun updateProfile(@RequestBody body: ProfileUpdateRequest): ResponseEntity<ProfileUpdateResponse> {
        val updated = userService.updateProfile(
            authService.getCurrentUser(), body.phone, body.firstName, body.lastName
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