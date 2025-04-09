package org.ntnu.grepapp.dto.chat

import java.time.LocalDateTime

data class ChatSendRequest(
    val recipientId: String,
    val content: String,
)

data class ChatSendResponse(
    val id: String,
    val senderId: String,
    val recipientId: String,
    val content: String,
    val timestamp: LocalDateTime,
)