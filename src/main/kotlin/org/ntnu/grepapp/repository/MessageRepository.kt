package org.ntnu.grepapp.repository

import org.ntnu.grepapp.model.ChatMessage
import org.apache.logging.log4j.LogManager
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class MessageRepository (
    private var jdbc: JdbcTemplate
) {
    private val rowMapper = RowMapper { rs, _ ->
        ChatMessage(
            id = rs.getString("id"),
            senderId = rs.getString("sender_id"),
            recipientId = rs.getString("recipient_id"),
            content = rs.getString("content"),
            timestamp = rs.getString("timestamp")
        )
    }

    private val logger = LogManager.getLogger(this::class::java);

    fun create(message: ChatMessage): ChatMessage {
        val uuid = UUID.randomUUID().toString();
        val sql = """
            INSERT INTO messages(id, sender_id, recipient_id, content, timestamp)
            VALUES (?, ?, ?, ?, ?)
            """
        jdbc.update(sql, uuid, message.senderId, message.recipientId, message.content, message.timestamp)
    }

    fun getList(pagination: Pageable, senderId: String, recipientId: String): List<ChatMessage> {
        val sql = "SELECT id, sender_id, recipient_id, content, timestamp FROM messages " +
                "WHERE ? IN (sender_id, recipient_id) AND ? IN (sender_id, recipient_id) " +
                "ORDER BY timestamp DESC LIMIT ? " +
                "OFFSET ?";
        return jdbc.query(sql, rowMapper, senderId,  recipientId, pagination.pageSize, pagination.offset)
    }
}