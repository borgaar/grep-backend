package org.ntnu.grepapp.dto.chat

import java.time.LocalDateTime

data class HistoryResponse(
    val id: String,
    val senderId: String,
    val recipientId: String,
    val content: String,
    val timestamp: LocalDateTime,
)