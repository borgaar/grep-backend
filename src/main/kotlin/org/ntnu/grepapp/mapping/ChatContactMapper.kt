package org.ntnu.grepapp.mapping

import org.ntnu.grepapp.dto.chat.ChatContactDTO
import org.ntnu.grepapp.model.ChatContact

fun toChatContactDTO(value: ChatContact): ChatContactDTO {
    return ChatContactDTO(
        phone = value.phone,
        firstName = value.firstName,
        lastName = value.lastName,
        lastMessageContent = value.lastMessageContent,
        lastMessageTimestamp = value.lastMessageTimestamp,
    )
}