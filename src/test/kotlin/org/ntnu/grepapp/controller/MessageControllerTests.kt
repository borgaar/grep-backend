package org.ntnu.grepapp.controller

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.ntnu.grepapp.dto.ChatContactDTO
import org.ntnu.grepapp.dto.ChatMessageDTO
import org.ntnu.grepapp.dto.chat.ChatSendRequest
import org.ntnu.grepapp.dto.chat.ChatSendResponse
import org.ntnu.grepapp.model.ChatContact
import org.ntnu.grepapp.model.ChatMessage
import org.ntnu.grepapp.model.CreateChatMessage
import org.ntnu.grepapp.security.UserClaims
import org.ntnu.grepapp.service.AuthService
import org.ntnu.grepapp.service.MessageService
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus

class MessageControllerTest {

    private lateinit var messageService: MessageService
    private lateinit var authService: AuthService
    private lateinit var messageController: MessageController

    private val testUserId = "11111111"
    private val testOtherUserId = "22222222"
    private val testMessage = ChatMessage(
        senderId = testUserId,
        recipientId = testOtherUserId,
        content = "Hei, hvordan går det?",
    )

    @BeforeEach
    fun setup() {
        messageService = mock()
        authService = mock()
        messageController = MessageController(messageService, authService)
        whenever(authService.getCurrentUser()).thenReturn(
            UserClaims(
                id = testUserId,
                role = "user"
            )
        )
    }

    @Test
    fun `should send message`() {
        val request = ChatSendRequest(recipientId = testOtherUserId, content = testMessage.content)
        val createMessage = CreateChatMessage(
            senderId = testUserId,
            recipientId = testOtherUserId,
            content = testMessage.content
        )

        whenever(messageService.create(createMessage)).thenReturn(testMessage)

        val response = messageController.sendMessage(request)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(ChatSendResponse(
            id = testMessage.id,
            senderId = testMessage.senderId,
            recipientId = testMessage.recipientId,
            timestamp = testMessage.timestamp,
            content = testMessage.content
        ), response.body)
    }

    @Test
    fun `should return bad request when message fails to send`() {
        val request = ChatSendRequest(recipientId = testOtherUserId, content = "")
        val createMessage = CreateChatMessage(senderId = testUserId, recipientId = testOtherUserId, content = "")

        whenever(messageService.create(createMessage)).thenReturn(null)

        val response = messageController.sendMessage(request)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `should retrieve message history`() {
        val messages = listOf(testMessage)
        whenever(messageService.getHistory(PageRequest.of(0, 100), testOtherUserId)).thenReturn(messages)

        val response = messageController.getHistory(0, 100, testOtherUserId)

        assertEquals(messages.size, response.size)
        assertEquals(messages.first().id, response.first().id)
    }
}
