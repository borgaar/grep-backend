package com.example.grepapp.service

import com.example.grepapp.model.Category
import com.example.grepapp.repository.CategoryRepository
import org.springframework.stereotype.Service

@Service
class CategoryService (
    private val categoryRepository: CategoryRepository,
){

    fun create(category: Category): Boolean {
        return categoryRepository.create(category)
    }

    fun getAll(page: Int, pageSize: Int): List<Category> {
        return categoryRepository.getAll(page, pageSize)
    }

    fun delete(name: String): Boolean {
        return categoryRepository.delete(name)
    }

    fun update(name: String, new: Category): Boolean {
        return categoryRepository.update(name, new)
    }
}