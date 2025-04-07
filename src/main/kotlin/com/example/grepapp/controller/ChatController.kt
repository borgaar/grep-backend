package com.example.grepapp.controller

import com.example.grepapp.dto.ChatMessage
import com.example.grepapp.dto.CreateChatMessage
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.springframework.http.ResponseEntity
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@CrossOrigin(origins = ["http://localhost:5173", "*"])
class ChatController (
//    private val chatService: ChatService
) {
    private val logger = LogManager.getLogger(this::class.java)

    @MessageMapping("/send")
    fun sendMessage(@RequestBody message: CreateChatMessage, @PathVariable userId: String): ChatMessage {
        logger.log(Level.INFO, userId, message.toString());

        return ChatMessage(senderId = userId, content = message.content)
    }

}