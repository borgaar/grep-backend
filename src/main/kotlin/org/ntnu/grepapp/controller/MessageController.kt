package org.ntnu.grepapp.controller

import org.apache.logging.log4j.LogManager
import org.ntnu.grepapp.dto.ChatContactDTO
import org.ntnu.grepapp.dto.ChatMessageDTO
import org.ntnu.grepapp.dto.chat.*
import org.ntnu.grepapp.mapping.toChatContactDTO
import org.ntnu.grepapp.service.MessageService
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/message")
@CrossOrigin(origins = ["http://localhost:5173", "*"])
class MessageController(
    private val messageService: MessageService
) {
    private val logger = LogManager.getLogger(this::class.java)

    @PostMapping("/send")
    fun sendMessage(@RequestBody message: SendRequest): ResponseEntity<SendResponse> {
        logger.info(message.recipientId, message.toString());
        val createdMessage = messageService.create(message)?: return ResponseEntity(HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok(
            SendResponse(
                id = createdMessage.id,
                senderId = createdMessage.senderId,
                recipientId = createdMessage.recipientId,
                timestamp = createdMessage.timestamp,
                content = createdMessage.content,
            )
        )
    }

    @GetMapping("/history")
    fun getHistory(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "5") pageSize: Int,
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
        ) }
    }

    @GetMapping("/contacts")
    fun getContacts(
        @RequestParam(defaultValue = "0") page: Int, @RequestParam(defaultValue = "5") pageSize: Int
    ): List<ChatContactDTO> {
        logger.info("Get contacts");
        logger.info(page.toString(), pageSize.toString());
        return messageService.getContacts(
            PageRequest.of(page, pageSize)
        ).map { toChatContactDTO(it) }
    }
}