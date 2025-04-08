package org.ntnu.grepapp.model

import org.springframework.data.annotation.Id
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

data class ChatMessage(
    val senderId: String,
    val recipientId: String,
    val content: String,
) {
    val id: String = UUID.randomUUID().toString()
}
