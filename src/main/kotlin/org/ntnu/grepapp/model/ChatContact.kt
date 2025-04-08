package org.ntnu.grepapp.model

import java.time.LocalDateTime

data class ChatContact(
    val phone: String,
    val firstName: String,
    val lastName: String,
    val lastMessageContent: String,
    val lastMessageTimestamp: LocalDateTime,
)
