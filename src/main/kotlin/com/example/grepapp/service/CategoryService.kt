package com.example.grepapp.service

import com.example.grepapp.model.Category
import com.example.grepapp.repository.CategoryRepository
import com.example.grepapp.repository.UserRepository
import com.example.grepapp.security.JwtUtil
import org.springframework.stereotype.Service

@Service
class CategoryService (
    private val categoryRepository: CategoryRepository,
){

    fun create(category: Category): Boolean {
        return try {
            categoryRepository.create(category);
        } catch (ex: Exception) {
            false;
        }
    }

    fun getAll(page: Int, pageSize: Int): List<Category> {
        return categoryRepository.getAll(page, pageSize)
    }

    fun delete(category: Category): Boolean {
        return try {
            categoryRepository.delete(category);
        } catch (ex: Exception) {
            false;
        }
    }

    fun update(category: Category, newCategory: Category): Boolean {
        return try {
            categoryRepository.update(category, newCategory);
        } catch (ex: Exception) {
            false;
        }
    }
}