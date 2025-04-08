package org.ntnu.grepapp.mapping

import org.ntnu.grepapp.dto.profile.UserDTO
import org.ntnu.grepapp.model.User

fun toUserDTO(user: User): UserDTO {
    return UserDTO(
        phone = user.phone,
        firstName = user.firstName,
        lastName = user.lastName,
        role = user.role,
    )
}