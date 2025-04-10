package org.ntnu.grepapp.service

import org.apache.logging.log4j.LogManager
import org.ntnu.grepapp.model.ChatContact
import org.ntnu.grepapp.model.ChatMessage
import org.ntnu.grepapp.model.CreateChatMessage
import org.ntnu.grepapp.repository.MessageRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

/**
 * Service class that manages messaging functionality.
 * Provides methods for sending messages and retrieving message history and contacts.
 */
@Service
class MessageService(
    private val repository: MessageRepository,
    private val authService: AuthService
) {
    private val logger = LogManager.getLogger(this::class::java);

    /**
     * Creates a new chat message between users.
     *
     * @param message The CreateChatMessage object containing message details
     * @return The created ChatMessage if successful, null if creation failed
     */
    fun create(message: CreateChatMessage): ChatMessage? {
        val chatMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderId = message.senderId,
            recipientId = message.recipientId,
            content = message.content,
            timestamp = LocalDateTime.now(),
            type = message.type
        )

        try {
            repository.create(chatMessage);
            return chatMessage
        } catch (e: Exception) {
            logger.error(e.message, e);
            return null
        }

    }

    /**
     * Retrieves message history between the current user and another user.
     *
     * @param page Pagination information including page number and size
     * @param recipientId The ID of the other user in the conversation
     * @return A list of ChatMessage objects representing the conversation history
     */
    fun getHistory(page: Pageable, recipientId: String): List<ChatMessage> {
        return repository.getList(page, recipientId, authService.getCurrentUser().id);
    }

    /**
     * Retrieves a list of contacts that the current user has exchanged messages with.
     *
     * @param page Pagination information including page number and size
     * @return A list of ChatContact objects representing the user's contacts
     */
    fun getContacts(page: Pageable): List<ChatContact> {
        return repository.getContacts(page, authService.getCurrentUser().id);
    }
}