package org.ntnu.grepapp.repository

import org.apache.logging.log4j.LogManager
import org.ntnu.grepapp.model.ChatContact
import org.ntnu.grepapp.model.ChatMessage
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Repository
class MessageRepository(
    private var jdbc: JdbcTemplate
) {
    private val rowMapper = RowMapper { rs, _ ->
        ChatMessage(
            id = rs.getString("id"),
            senderId = rs.getString("sender_id"),
            recipientId = rs.getString("recipient_id"),
            content = rs.getString("content"),
            timestamp = LocalDateTime.parse(rs.getString("timestamp"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        )
    }

    private val contactRowMapper = RowMapper { rs, _ ->
        ChatContact(
            phone = rs.getString("phone"),
            firstName = rs.getString("first_name"),
            lastName = rs.getString("last_name"),
            lastMessageContent = rs.getString("last_message_content"),
            lastMessageTimestamp = rs.getTimestamp("last_message_timestamp").toLocalDateTime()
        )
    }

    private val logger = LogManager.getLogger(this::class::java);

    fun create(message: ChatMessage) {
        val sql = """
            INSERT INTO messages(id, sender_id, recipient_id, content, timestamp)
            VALUES (?, ?, ?, ?, ?)
            """
        jdbc.update(
            sql, uuid, message.senderId, message.recipientId, message.content, message.timestamp
        )
    }

    fun getList(pagination: Pageable, senderId: String, recipientId: String): List<ChatMessage> {
        val sql =
            "SELECT id, sender_id, recipient_id, content, timestamp FROM messages " + "WHERE ? IN (sender_id, recipient_id) AND ? IN (sender_id, recipient_id) " + "ORDER BY timestamp DESC LIMIT ? " + "OFFSET ?";
        return jdbc.query(
            sql, rowMapper, senderId, recipientId, pagination.pageSize, pagination.offset
        )
    }

    fun getContacts(pagination: Pageable, userId: String): List<ChatContact> {
        val sql = """
            SELECT 
                contact.phone AS phone,
                contact.first_name AS first_name,
                contact.last_name AS last_name,
                last_message.content AS last_message_content,
                last_message.timestamp AS last_message_timestamp
            FROM 
                users AS contact
            JOIN 
                (
                    SELECT 
                        IF(sender_id = ?, recipient_id, sender_id) AS contact_id,
                        MAX(timestamp) AS max_timestamp
                    FROM 
                        messages
                    WHERE 
                        sender_id = ? OR recipient_id = ?
                    GROUP BY 
                        contact_id
                ) AS latest_contacts ON contact.phone = latest_contacts.contact_id
            JOIN 
                messages AS last_message ON (
                    (last_message.sender_id = ? AND last_message.recipient_id = contact.phone) OR
                    (last_message.sender_id = contact.phone AND last_message.recipient_id = ?)
                ) AND last_message.timestamp = latest_contacts.max_timestamp
            ORDER BY 
                last_message.timestamp DESC
            LIMIT ? OFFSET ?;
          
        """;

        return jdbc.query(
            sql, contactRowMapper, userId, userId, pagination.pageSize, pagination.offset
        )
    }
}