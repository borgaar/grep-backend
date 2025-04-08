package org.ntnu.grepapp.model

import org.springframework.data.annotation.Id
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

data class ChatMessage(
    @Id
    val id: String = UUID.randomUUID().toString(),
    val senderId: String,
    val recipientId: String,
    val content: String,
    val timestamp: String = DateTimeFormatter
        .ofPattern("yyyy-MM-dd HH:mm:ss")
        .withZone(ZoneId.of("Europe/Oslo"))
        .format(Instant.now())
)
