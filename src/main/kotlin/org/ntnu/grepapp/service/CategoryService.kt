package org.ntnu.grepapp.service

import org.ntnu.grepapp.model.Category
import org.ntnu.grepapp.repository.CategoryRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class CategoryService (
    private val categoryRepository: CategoryRepository,
){

    fun create(category: Category): Boolean {
        return categoryRepository.create(category)
    }

    fun getAll(page: Pageable): List<Category> {
        return categoryRepository.getAll(page)
    }

    fun delete(name: String): Boolean {
        return categoryRepository.delete(name)
    }

    fun update(name: String, new: Category): Boolean {
        return categoryRepository.update(name, new)
    }
}