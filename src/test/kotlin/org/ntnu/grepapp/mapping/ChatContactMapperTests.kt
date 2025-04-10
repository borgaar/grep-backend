package org.ntnu.grepapp.mapping

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.ntnu.grepapp.model.ChatContact
import java.time.LocalDateTime

class ChatContactMappingTest {

    private val testChatContact = ChatContact(
        phone = "99887766",
        firstName = "Ola",
        lastName = "Nordmann",
        lastMessageContent = "Hei, hvordan går det?",
        lastMessageTimestamp = LocalDateTime.of(2024, 4, 10, 12, 30)
    )

    @Test
    fun `should map ChatContact to ChatContactDTO`() {
        val dto = toChatContactDTO(testChatContact)

        assertNotNull(dto)
        assertEquals(testChatContact.phone, dto.phone)
        assertEquals(testChatContact.firstName, dto.firstName)
        assertEquals(testChatContact.lastName, dto.lastName)
        assertEquals(testChatContact.lastMessageContent, dto.lastMessageContent)
        assertEquals(testChatContact.lastMessageTimestamp, dto.lastMessageTimestamp)
    }
}
