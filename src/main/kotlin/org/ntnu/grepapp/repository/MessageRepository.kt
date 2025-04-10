package org.ntnu.grepapp.repository

import org.apache.logging.log4j.LogManager
import org.ntnu.grepapp.model.ChatContact
import org.ntnu.grepapp.model.ChatMessage
import org.ntnu.grepapp.model.ChatMessageType
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

/**
 * Repository class for handling database operations related to messages.
 * Provides methods for storing and retrieving messages and contacts.
 */
@Repository
class MessageRepository(
    private var jdbc: JdbcTemplate
) {

    /**
     * Row mapper for converting database rows to ChatMessage objects.
     */
    private val rowMapper = RowMapper { rs, _ ->
        ChatMessage(
            id = rs.getString("id"),
            senderId = rs.getString("sender_id"),
            recipientId = rs.getString("recipient_id"),
            content = rs.getString("content"),
            timestamp = rs.getTimestamp("timestamp").toLocalDateTime(),
            type = ChatMessageType.fromValue(rs.getString("type"))
        )
    }

    /**
     * Row mapper for converting database rows to ChatContact objects.
     */
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

    /**
     * Creates a new message in the database.
     *
     * @param message The ChatMessage object to persist
     */
    fun create(message: ChatMessage) {
        val sql = """
            INSERT INTO messages(id, sender_id, recipient_id, content, timestamp, type)
            VALUES (?, ?, ?, ?, ?, ?)
            """
        jdbc.update(
            sql, message.id, message.senderId, message.recipientId, message.content, message.timestamp, message.type.value
        )
    }

    /**
     * Retrieves messages between two users with pagination.
     *
     * @param pagination Pagination information including page size and offset
     * @param senderId The ID of one user in the conversation
     * @param recipientId The ID of the other user in the conversation
     * @return A list of ChatMessage objects representing the conversation history
     */
    fun getList(pagination: Pageable, senderId: String, recipientId: String): List<ChatMessage> {
        val sql = """
            SELECT id, sender_id, recipient_id, content, timestamp, type
            FROM messages
            WHERE ? IN (sender_id, recipient_id)
                AND ? IN (sender_id, recipient_id)
            ORDER BY timestamp DESC
            LIMIT ? OFFSET ?
        """
        return jdbc.query(
            sql, rowMapper, senderId, recipientId, pagination.pageSize, pagination.offset
        )
    }

    /**
     * Retrieves a list of contacts that a user has exchanged messages with.
     *
     * @param pagination Pagination information including page size and offset
     * @param userId The ID of the user whose contacts to retrieve
     * @return A list of ChatContact objects representing the user's contacts
     */
    fun getContacts(pagination: Pageable, userId: String): List<ChatContact> {
        val sql = """
            WITH contacts (one, other) AS (
                SELECT m.sender_id, m.recipient_id
                FROM messages m
                UNION
                SELECT m.recipient_id, m.sender_id
                FROM messages m
            ), msgs AS (
                SELECT mc.other AS phone, m.*, Max(m.timestamp) OVER (
                    PARTITION BY mc.other
                    ORDER BY m.timestamp DESC
                    ) AS maxval
                FROM messages m
                    JOIN contacts mc ON mc.other IN (m.sender_id, m.recipient_id)
                WHERE mc.one = ?
            )
            SELECT m.phone, u.first_name, u.last_name, m.sender_id AS last_message_sender,
                   m.content AS last_message_content,
                   m.timestamp AS last_message_timestamp
            FROM msgs m
                JOIN users u ON u.phone = m.phone
            WHERE m.maxval = m.timestamp -- technically breaks if there are messages with the same timestamp between the same users
            LIMIT ? OFFSET ?;
        """;

        return jdbc.query(
            sql, contactRowMapper, userId, pagination.pageSize, pagination.offset
        )
    }
}