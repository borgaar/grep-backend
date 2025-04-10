package org.ntnu.grepapp.dto

import io.swagger.v3.oas.annotations.media.Schema
import org.ntnu.grepapp.model.ChatMessageType
import java.time.LocalDateTime

@Schema(description = "Data transfer object for chat messages")
data class ChatMessageDTO(
    @Schema(description = "Unique identifier of the message", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    val id: String,

    @Schema(description = "Phone number of the sender", example = "+4712345678")
    val senderId: String,

    @Schema(description = "Phone number of the recipient", example = "+4787654321")
    val recipientId: String,

    @Schema(description = "Content of the message", example = "Hello, I'm interested in your listing")
    val content: String,

    @Schema(description = "Timestamp when the message was sent")
    val timestamp: LocalDateTime,

    @Schema(description = "Type of the message", example = "TEXT", allowableValues = ["TEXT", "RESERVED", "MARKED_SOLD"])
    val type: ChatMessageType
)