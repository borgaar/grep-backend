package com.example.grepapp.dto

data class DeleteCategoryRequest(
    val name: String,
)

data class CreateCategoryRequest (
    val name: String,
)

data class UpdateCategoryRequest(
    val oldName: String,
    val newName: String,
)
