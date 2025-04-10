package org.ntnu.grepapp.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.apache.logging.log4j.LogManager
import org.ntnu.grepapp.dto.ChatContactDTO
import org.ntnu.grepapp.dto.ChatMessageDTO
import org.ntnu.grepapp.dto.chat.*
import org.ntnu.grepapp.mapping.toChatContactDTO
import org.ntnu.grepapp.model.CreateChatMessage
import org.ntnu.grepapp.service.AuthService
import org.ntnu.grepapp.service.MessageService
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for message-related operations.
 * Provides endpoints for sending messages, retrieving message history, and managing contacts.
 */
@RestController
@RequestMapping("/api/message")
@CrossOrigin(origins = ["http://localhost:5173", "*"])
@Tag(name = "Messages", description = "API for sending messages and managing conversations")
class MessageController(
    private val messageService: MessageService,
    private val authService: AuthService
) {
    private val logger = LogManager.getLogger(this::class.java)

    /**
     * Sends a new message to a recipient.
     *
     * @param message A ChatSendRequest containing the recipient ID and message content
     * @return ResponseEntity containing:
     *         - A ChatSendResponse with the created message details if successful
     *         - HTTP 400 BAD_REQUEST status if the message couldn't be created
     */
    @Operation(
        summary = "Send a message",
        description = "Sends a new message to a recipient"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Message successfully sent",
            content = [Content(mediaType = "application/json",
                schema = Schema(implementation = ChatSendResponse::class))]
        ),
        ApiResponse(
            responseCode = "400",
            description = "Message couldn't be created"
        )
    ])
    @PostMapping("/send")
    fun sendMessage(@RequestBody message: ChatSendRequest): ResponseEntity<ChatSendResponse> {
        logger.info(message.recipientId, message.toString());
        val newMessage = CreateChatMessage(
            senderId = authService.getCurrentUser().id,
            recipientId = message.recipientId,
            content = message.content,
        )

        val createdMessage = messageService.create(newMessage)?: return ResponseEntity(HttpStatus.BAD_REQUEST);

        return ResponseEntity.ok(
            ChatSendResponse(
                id = createdMessage.id,
                senderId = createdMessage.senderId,
                recipientId = createdMessage.recipientId,
                timestamp = createdMessage.timestamp,
                content = createdMessage.content,
            )
        )
    }

    /**
     * Retrieves message history between the current user and another user.
     *
     * @param page The page number to retrieve (zero-based), defaults to 0
     * @param pageSize The number of messages per page, defaults to 100
     * @param otherUser The phone number of the other user in the conversation
     * @return A list of ChatMessageDTO objects representing the message history
     */
    @Operation(
        summary = "Get message history",
        description = "Retrieves message history between the current user and another user"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved message history",
            content = [Content(mediaType = "application/json",
                schema = Schema(implementation = ChatMessageDTO::class, type = "array"))]
        )
    ])
    @GetMapping("/history")
    fun getHistory(
        @Parameter(description = "Page number (zero-based)", example = "0")
        @RequestParam(defaultValue = "0") page: Int,

        @Parameter(description = "Number of messages per page", example = "100")
        @RequestParam(defaultValue = "100") pageSize: Int,

        @Parameter(description = "Phone number of the other user", example = "+4712345678", required = true)
        @RequestParam otherUser: String
    ): List<ChatMessageDTO> {
        logger.info("Get history");
        logger.info(page.toString(), pageSize.toString(), otherUser);

        return messageService.getHistory(
            PageRequest.of(page, pageSize), otherUser
        ).map { ChatMessageDTO(
            id = it.id,
            senderId = it.senderId,
            recipientId = it.recipientId,
            timestamp = it.timestamp,
            content = it.content,
            type = it.type
        ) }
    }

    /**
     * Retrieves a list of contacts that the current user has exchanged messages with.
     *
     * @param page The page number to retrieve (zero-based), defaults to 0
     * @param pageSize The number of contacts per page, defaults to 100
     * @return A list of ChatContactDTO objects representing the user's contacts
     */
    @Operation(
        summary = "Get contacts",
        description = "Retrieves a list of contacts that the current user has exchanged messages with"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved contacts",
            content = [Content(mediaType = "application/json",
                schema = Schema(implementation = ChatContactDTO::class, type = "array"))]
        )
    ])
    @GetMapping("/contacts")
    fun getContacts(
        @Parameter(description = "Page number (zero-based)", example = "0")
        @RequestParam(defaultValue = "0") page: Int,

        @Parameter(description = "Number of contacts per page", example = "100")
        @RequestParam(defaultValue = "100") pageSize: Int
    ): List<ChatContactDTO> {
        logger.info("Get contacts");
        logger.info(page.toString(), pageSize.toString());
        return messageService.getContacts(
            PageRequest.of(page, pageSize)
        ).map { toChatContactDTO(it) }
    }
}