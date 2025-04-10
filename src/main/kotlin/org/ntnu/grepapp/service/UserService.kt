package org.ntnu.grepapp.service

import org.ntnu.grepapp.model.User
import org.ntnu.grepapp.repository.UserRepository
import org.springframework.stereotype.Service

/**
 * Service class that manages user profile operations.
 * Provides methods for retrieving and updating user profiles.
 */
@Service
class UserService(
    private val repository: UserRepository,
) {

    /**
     * Retrieves a user's profile information by phone number.
     *
     * @param phone The phone number of the user to retrieve
     * @return The User object if found, null otherwise
     */
    fun getProfile(phone: String): User? {
        return repository.find(phone);
    }

    /**
     * Updates a user's profile information.
     * Only the non-null parameters will be updated.
     *
     * @param previousPhone The current phone number of the user to update
     * @param newPhone The new phone number to set, or null to keep the current value
     * @param newFirstName The new first name to set, or null to keep the current value
     * @param newLastName The new last name to set, or null to keep the current value
     * @return The updated User object if found and updated, null if user not found
     */
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