package org.ntnu.grepapp.controller

import org.apache.logging.log4j.LogManager
import org.hibernate.sql.Update
import org.ntnu.grepapp.dto.profile.GetResponse
import org.ntnu.grepapp.dto.profile.UpdateRequest
import org.ntnu.grepapp.dto.profile.UpdateResponse
import org.ntnu.grepapp.mapping.toUserDTO
import org.ntnu.grepapp.model.User
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
    fun getProfile(): ResponseEntity<GetResponse> {
        val user = userService.getProfile(authService.getCurrentUser()) ?: return ResponseEntity(
            HttpStatus.NOT_FOUND
        );
        val body = GetResponse(
            user.phone,
            user.firstName,
            user.lastName,
            user.role
        )
        return ResponseEntity.ok(body);
    }

    @PatchMapping("/profile")
    fun updateProfile(@RequestBody body: UpdateRequest): ResponseEntity<UpdateResponse> {
        val updated = userService.updateProfile(
            authService.getCurrentUser(), body.phone, body.firstName, body.lastName
        )

        if (updated == null) {
            return ResponseEntity(HttpStatus.NOT_FOUND);
        }
        val out = UpdateResponse(
            updated.phone,
            updated.firstName,
            updated.lastName,
            updated.role
        )
        return ResponseEntity.ok(out);
    }
}