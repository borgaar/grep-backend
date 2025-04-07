package org.ntnu.grepapp.service

import org.ntnu.grepapp.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val repository: UserRepository,
) {

}