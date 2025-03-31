package com.example.grepapp.service

import com.example.grepapp.model.User
import com.example.grepapp.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {
    fun getUser(): User {
        val authentication = SecurityContextHolder.getContext().authentication;
        val username = authentication?.name ?: throw Exception("User not found");
        val user = userRepository.findByUsername(username).get();
        return user;
    }
}