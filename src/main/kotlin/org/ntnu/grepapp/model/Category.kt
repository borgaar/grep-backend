package org.ntnu.grepapp.model

import org.springframework.data.annotation.Id

data class Category (
    @Id
    val name: String
)