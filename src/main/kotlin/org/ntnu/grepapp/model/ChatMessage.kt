package org.ntnu.grepapp.model

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

data class CreateChatMessage(
    val senderId: String,
    val recipientId: String,
    val content: String,
    val type: ChatMessageType = ChatMessageType.TEXT,
)

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val senderId: String,
    val recipientId: String,
    val content: String,
    val timestamp: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC),
    val type: ChatMessageType = ChatMessageType.TEXT,
)

enum class ChatMessageType(val value: String) {
    TEXT("text"), RESERVED("reserved"), MARKED_SOLD("marked-sold");

    companion object {
        fun fromValue(value: String): ChatMessageType {
            return entries.firstOrNull { it.value == value } ?: TEXT
        }
    }
}