package org.ntnu.grepapp.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.ntnu.grepapp.model.User
import org.ntnu.grepapp.repository.UserRepository

class UserServiceTest {
    private lateinit var userRepository: UserRepository
    private lateinit var userService: UserService

    private val testUser = User(
        phone = "98765432",
        passwordHash = "passord123hashkode",
        firstName = "Ola",
        lastName = "Nordmann",
        role = "bruker"
    )

    @BeforeEach
    fun setup() {
        userRepository = mock(UserRepository::class.java)
        userService = UserService(userRepository)
    }

    @Test
    fun `getProfile should return user when found`() {
        val phone = testUser.phone

        `when`(userRepository.find(phone)).thenReturn(testUser)

        val result = userService.getProfile(phone)

        assertNotNull(result)
        assertEquals(testUser, result)
        verify(userRepository).find(phone)
    }

    @Test
    fun `updateProfile should update user fields and return updated user`() {
        val previousPhone = testUser.phone
        val newPhone = "87654321"
        val newFirstName = "Kari"
        val newLastName = "Hansen"

        val expectedUser = testUser.copy(
            phone = newPhone,
            firstName = newFirstName,
            lastName = newLastName
        )

        `when`(userRepository.find(previousPhone)).thenReturn(testUser)

        val result = userService.updateProfile(
            previousPhone,
            newPhone,
            newFirstName,
            newLastName
        )

        assertNotNull(result)
        assertEquals(newPhone, result?.phone)
        assertEquals(newFirstName, result?.firstName)
        assertEquals(newLastName, result?.lastName)
        verify(userRepository).find(previousPhone)
        verify(userRepository).overwrite(previousPhone, expectedUser)
    }
}