package org.ntnu.grepapp.service

import org.ntnu.grepapp.model.User
import org.ntnu.grepapp.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val repository: UserRepository,
) {

    fun getProfile(phone: String): User? {
        return repository.find(phone);
    }
}