package com.example.grepapp.service

import com.example.grepapp.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val repository: UserRepository,
) {

}