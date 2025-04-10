package org.ntnu.grepapp.repository

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.ntnu.grepapp.model.ChatContact
import org.ntnu.grepapp.model.ChatMessage
import org.ntnu.grepapp.model.ChatMessageType
import org.ntnu.grepapp.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.data.domain.PageRequest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.jdbc.Sql
import java.time.LocalDateTime
import java.util.*

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = ["/schema.sql"], executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class MessageRepositoryTest {
    @Autowired
    private lateinit var jdbc: JdbcTemplate

    private lateinit var messageRepository: MessageRepository
    private lateinit var userRepository: UserRepository

    private val now = LocalDateTime.now()

    private val testUser1 = User(
        phone = "11111111",
        passwordHash = "passord123",
        firstName = "Ole",
        lastName = "Hansen",
        role = "user"
    )

    private val testUser2 = User(
        phone = "22222222",
        passwordHash = "passord456",
        firstName = "Kari",
        lastName = "Nordmann",
        role = "user"
    )

    private val testMessage = ChatMessage(
        senderId = testUser1.phone,
        recipientId = testUser2.phone,
        content = "Hei, hvordan går det?",
    )

    @BeforeEach
    fun setup() {
        messageRepository = MessageRepository(jdbc)
        userRepository = UserRepository(jdbc)
        jdbc.execute("DELETE FROM messages")
        jdbc.execute("DELETE FROM users")

        userRepository.save(testUser1)
        userRepository.save(testUser2)
    }

    @Test
    fun `should create and retrieve message`() {
        messageRepository.create(testMessage)

        val messages = messageRepository.getList(
            PageRequest.of(0, 10),
            testMessage.senderId,
            testMessage.recipientId
        )
        val retrieved = messages[0]

        assertEquals(1, messages.size)
        assertEquals(testMessage.id, retrieved.id)
        assertEquals(testMessage.senderId, retrieved.senderId)
        assertEquals(testMessage.recipientId, retrieved.recipientId)
    }

    @Test
    fun `should retrieve empty list when no messages exist`() {
        val messages = messageRepository.getList(
            PageRequest.of(0, 10),
            testUser1.phone,
            testUser2.phone,
        )

        assertTrue(messages.isEmpty())
    }

    @Test
    fun `should paginate messages correctly`() {
        val baseMessage = testMessage

        for (i in 1..5) {
            val timestamp = now.plusMinutes(i.toLong())
            val message = baseMessage.copy(
                id = "msg$i",
                content = "Melding nummer $i",
                timestamp = timestamp
            )
            messageRepository.create(message)
        }

        val pageSize = 2
        val firstPage = messageRepository.getList(
            PageRequest.of(0, pageSize),
            testMessage.senderId,
            testMessage.recipientId
        )

        val secondPage = messageRepository.getList(
            PageRequest.of(1, pageSize),
            testMessage.senderId,
            testMessage.recipientId
        )

        assertEquals(pageSize, firstPage.size)
        assertEquals(pageSize, secondPage.size)
        assertTrue(firstPage[0].timestamp.isAfter(firstPage[1].timestamp))
        assertTrue(firstPage[1].timestamp.isAfter(secondPage[0].timestamp))
    }

    @Test
    fun `should get contacts correctly`() {
        val messages = listOf(
            testMessage,
        )

        messages.forEach { messageRepository.create(it) }

        val contacts = messageRepository.getContacts(
            PageRequest.of(0, 10),
            testUser1.phone,
        )

        assertEquals(1, contacts.size)

        val contact = contacts[0]
        assertEquals(testUser2.phone, contact.phone)
        assertEquals(testUser2.firstName, contact.firstName)
        assertEquals(testUser2.lastName, contact.lastName)
        assertEquals(testMessage.content, contact.lastMessageContent)
    }

    @Test
    fun `should return empty list when user has no contacts`() {
        val contacts = messageRepository.getContacts(
            PageRequest.of(0, 10),
            testUser1.phone,
        )

        assertTrue(contacts.isEmpty())
    }
}
