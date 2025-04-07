package org.ntnu.grepapp.service

import org.ntnu.grepapp.model.Category
import org.ntnu.grepapp.repository.CategoryRepository
import org.ntnu.grepapp.repository.UserRepository
import org.ntnu.grepapp.security.JwtUtil
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