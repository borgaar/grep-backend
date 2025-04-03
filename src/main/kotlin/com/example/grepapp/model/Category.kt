package com.example.grepapp.model

import org.springframework.data.annotation.Id

data class Category (
    @Id
    val name: String
)