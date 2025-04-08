package org.ntnu.grepapp.model

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val senderId: String,
    val recipientId: String,
    val content: String,
    val timestamp: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC),
)
