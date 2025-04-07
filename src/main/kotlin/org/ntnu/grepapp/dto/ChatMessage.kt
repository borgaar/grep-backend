package com.example.grepapp.dto

import org.springframework.data.annotation.Id
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

data class ChatMessage(
    @Id
    val id: String = UUID.randomUUID().toString(),
    val senderId: String,
    val content: String,
    val timestamp: String = DateTimeFormatter
        .ofPattern("yyyy-MM-dd HH:mm:ss")
        .withZone(ZoneOffset.UTC)
        .format(Instant.now())
)

data class CreateChatMessage(
    val content: String,

)