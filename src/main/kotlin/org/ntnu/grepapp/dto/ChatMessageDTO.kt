package org.ntnu.grepapp.dto

data class CreateChatMessage(
    val recipientId: String,
    val content: String,
)