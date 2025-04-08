package org.ntnu.grepapp.controller

import org.apache.logging.log4j.LogManager
import org.ntnu.grepapp.dto.chat.*
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
    fun sendMessage(@RequestBody message: SendRequest) {
        logger.info(message.recipientId, message.toString());
        messageService.create(message);
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
        )
    }
}