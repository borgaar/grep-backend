package org.ntnu.grepapp.controller

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.ntnu.grepapp.dto.CreateChatMessage
import org.ntnu.grepapp.model.ChatMessage
import org.ntnu.grepapp.service.MessageService
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/message")
@CrossOrigin(origins = ["http://localhost:5173", "*"])
class MessageController(
    private val messageService: MessageService
) {
    private val logger = LogManager.getLogger(this::class.java)

    @PostMapping("/send")
    fun sendMessage(@RequestBody message: CreateChatMessage) {
        logger.log(Level.INFO, message.recipientId, message.toString());
        messageService.create(message);
    }

    @GetMapping("/history")
    fun getHistory(
        @RequestParam("page", defaultValue = "0") page: Int,
        @RequestParam("pageSize", defaultValue = "5") pageSize: Int,
        @RequestParam("otherUser") otherUser: String
    ): List<ChatMessage> {
        logger.log(Level.INFO, "Get history");
        logger.log(Level.INFO, page.toString(), pageSize.toString(), otherUser);
        return messageService.getHistory(
            PageRequest.of(page, pageSize), otherUser
        )
    }
}