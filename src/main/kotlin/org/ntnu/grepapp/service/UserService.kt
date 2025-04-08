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

    fun updateProfile(
        previousPhone: String, newPhone: String?, newFirstName: String?, newLastName: String?
    ): User? {
        val user = repository.find(previousPhone) ?: return null;

        if (newPhone != null) {
            user.phone = newPhone
        }
        if (newFirstName != null) {
            user.firstName = newFirstName
        }
        if (newLastName != null) {
            user.lastName = newLastName
        }

        repository.overwrite(previousPhone, user)
        return user
    }
}