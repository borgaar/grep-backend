package org.ntnu.grepapp.controller

import org.apache.logging.log4j.LogManager
import org.ntnu.grepapp.model.User
import org.ntnu.grepapp.service.AuthService
import org.ntnu.grepapp.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.HttpClientErrorException.NotFound

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = ["http://localhost:5173"])
class UserController (
    private val userService: UserService,
    private val authService: AuthService,
) {
    private val logger = LogManager.getLogger(this::class::java);

    @GetMapping("/profile")
    fun getProfile(): ResponseEntity<User> {
        val user = userService.getProfile(authService.getCurrentUser());
        user?: return ResponseEntity(HttpStatus.NOT_FOUND);
        return ResponseEntity.ok(user);
    }
}