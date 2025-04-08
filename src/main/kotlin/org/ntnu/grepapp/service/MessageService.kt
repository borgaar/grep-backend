package org.ntnu.grepapp.service

import org.ntnu.grepapp.dto.chat.CreateChatMessage
import org.ntnu.grepapp.model.ChatMessage
import org.ntnu.grepapp.repository.MessageRepository
import org.apache.logging.log4j.LogManager
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class MessageService (
    private val repository: MessageRepository,
    private val authService: AuthService
) {
    private val logger = LogManager.getLogger(this::class::java);

    fun create(message: CreateChatMessage) {
        repository.create(
            ChatMessage(
                senderId = authService.getCurrentUser(),
                recipientId = message.recipientId,
                content = message.content,
            )
        );
    }

    fun getHistory(page: Pageable, recipientId: String): List<ChatMessage> {
        return repository.getList(page, recipientId, authService.getCurrentUser());
    }
}