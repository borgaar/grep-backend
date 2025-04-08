package org.ntnu.grepapp.dto

import java.time.LocalDateTime

data class ChatContactDTO(
    val phone: String,
    val firstName: String,
    val lastName: String,
    val lastMessageContent: String,
    val lastMessageTimestamp: LocalDateTime,
)
