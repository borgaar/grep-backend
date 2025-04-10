package org.ntnu.grepapp.controller

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.ntnu.grepapp.dto.profile.ProfileGetResponse
import org.ntnu.grepapp.dto.profile.ProfileUpdateRequest
import org.ntnu.grepapp.dto.profile.ProfileUpdateResponse
import org.ntnu.grepapp.model.Listing
import org.ntnu.grepapp.model.User
import org.ntnu.grepapp.security.UserClaims
import org.ntnu.grepapp.service.AuthService
import org.ntnu.grepapp.service.UserService
import org.springframework.http.HttpStatus

class UserControllerTest {

    private lateinit var userService: UserService
    private lateinit var authService: AuthService
    private lateinit var userController: UserController

    private val testClaims = UserClaims(
        id = "11111111",
        role = "user"
    )

    private val testUser = User(
        phone = testClaims.id,
        passwordHash = "hashedpassword123",
        firstName = "Jonas",
        lastName = "Gahr",
        role = "user",
    )

    @BeforeEach
    fun setup() {
        userService = mock(UserService::class.java)
        authService = mock(AuthService::class.java)
        userController = UserController(userService, authService)
    }

    @Test
    fun `should return profile when user exists`() {
        `when`(authService.getCurrentUser()).thenReturn(testClaims)
        `when`(userService.getProfile(testClaims.id)).thenReturn(testUser)

        val response = userController.getProfile()

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(ProfileGetResponse(testUser.phone, testUser.firstName, testUser.lastName, testUser.role), response.body)
    }

    @Test
    fun `should return 404 when profile not found`() {
        `when`(authService.getCurrentUser()).thenReturn(testClaims)
        `when`(userService.getProfile(testClaims.id)).thenReturn(null)

        val response = userController.getProfile()

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
    }

    @Test
    fun `should update profile when user exists`() {
        val request = ProfileUpdateRequest("88888888", "Kari", "Nordmann")
        val updatedUser = testUser.copy(phone = request.phone!!, firstName = request.firstName!!, lastName = request.lastName!!)

        `when`(authService.getCurrentUser()).thenReturn(testClaims)
        `when`(userService.updateProfile(testClaims.id, request.phone, request.firstName, request.lastName)).thenReturn(updatedUser)

        val response = userController.updateProfile(request)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(ProfileUpdateResponse(updatedUser.phone, updatedUser.firstName, updatedUser.lastName, updatedUser.role), response.body)
    }

    @Test
    fun `should return 404 when updating non-existing user`() {
        val request = ProfileUpdateRequest("77777777", "Per", "Hansen")

        `when`(authService.getCurrentUser()).thenReturn(testClaims)
        `when`(userService.updateProfile(testClaims.id, request.phone, request.firstName, request.lastName)).thenReturn(null)

        val response = userController.updateProfile(request)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
    }
}
