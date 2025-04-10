package org.ntnu.grepapp.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "Data transfer object for chat contacts")
data class ChatContactDTO(
    @Schema(description = "Phone number of the contact", example = "+4712345678")
    val phone: String,

    @Schema(description = "First name of the contact", example = "John")
    val firstName: String,

    @Schema(description = "Last name of the contact", example = "Doe")
    val lastName: String,

    @Schema(description = "Content of the last message exchanged", example = "Thanks, I'll be there at 5PM")
    val lastMessageContent: String,

    @Schema(description = "Timestamp of the last message exchanged")
    val lastMessageTimestamp: LocalDateTime,
)