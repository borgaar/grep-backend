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

@Service
class MessageService(
    private val repository: MessageRepository,
    private val authService: AuthService
) {
    private val logger = LogManager.getLogger(this::class::java);

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

    fun getHistory(page: Pageable, recipientId: String): List<ChatMessage> {
        return repository.getList(page, recipientId, authService.getCurrentUser().id);
    }

    fun getContacts(page: Pageable): List<ChatContact> {
        return repository.getContacts(page, authService.getCurrentUser().id);
    }
}