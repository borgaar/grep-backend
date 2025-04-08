package org.ntnu.grepapp.dto.chat

data class SendRequest(
    val recipientId: String,
    val content: String,
)