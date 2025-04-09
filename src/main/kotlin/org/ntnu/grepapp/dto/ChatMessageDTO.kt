package org.ntnu.grepapp.dto

import org.ntnu.grepapp.model.ChatMessageType
import java.time.LocalDateTime

data class ChatMessageDTO(
    val id: String,
    val senderId: String,
    val recipientId: String,
    val content: String,
    val timestamp: LocalDateTime,
    val type: ChatMessageType
)