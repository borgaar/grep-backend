package org.ntnu.grepapp.repository

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.ntnu.grepapp.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.jdbc.Sql

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = ["/schema.sql"], executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserRepositoryTest {

    @Autowired
    private lateinit var jdbc: JdbcTemplate
    private lateinit var userRepository: UserRepository

    private val testUser = User(
        phone = "11111111",
        passwordHash = "hashedpassword123",
        firstName = "John",
        lastName = "Doe",
        role = "user"
    )

    @BeforeEach
    fun setup() {
        userRepository = UserRepository(jdbc)
        jdbc.execute("DELETE FROM users")
    }

    @Test
    fun `should save and find user`() {
        userRepository.save(testUser)
        val foundUser = userRepository.find(testUser.phone)

        assertNotNull(foundUser)
        assertEquals(testUser, foundUser)
    }

    @Test
    fun `should return null when user not found`() {
        val foundUser = userRepository.find("nonexistent")

        assertNull(foundUser)
    }

    @Test
    fun `should overwrite existing user`() {
        val originalUser = testUser

        val updatedUser = originalUser
            .copy(
                passwordHash = "newhash456",
                firstName = "Jane",
            )

        userRepository.save(originalUser)
        userRepository.overwrite(originalUser.phone, updatedUser)
        val foundUser = userRepository.find(originalUser.phone)

        assertNotNull(foundUser)
        assertEquals(updatedUser, foundUser)
        assertNotEquals(originalUser, foundUser)
    }
}