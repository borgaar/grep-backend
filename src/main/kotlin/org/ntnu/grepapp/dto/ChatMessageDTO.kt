package org.ntnu.grepapp.dto

import java.time.LocalDateTime

data class ChatMessageDTO(
    val id: String,
    val senderId: String,
    val recipientId: String,
    val content: String,
    val timestamp: LocalDateTime,
)