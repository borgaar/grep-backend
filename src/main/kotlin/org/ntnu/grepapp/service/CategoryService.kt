package org.ntnu.grepapp.service

import org.ntnu.grepapp.model.Category
import org.ntnu.grepapp.repository.CategoryRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

/**
 * Service class that manages categories.
 * Provides methods for creating, retrieving, updating, and deleting categories.
 */
@Service
class CategoryService (
    private val categoryRepository: CategoryRepository,
){

    /**
     * Creates a new category in the database.
     *
     * @param category The Category object to be created
     * @return true if the category was successfully created, false if a category with the same name already exists
     */
    fun create(category: Category): Boolean {
        return categoryRepository.create(category)
    }

    /**
     * Retrieves a paginated list of all categories.
     *
     * @param page Pagination information including page number and size
     * @return A list of Category objects for the requested page
     */
    fun getAll(page: Pageable): List<Category> {
        return categoryRepository.getAll(page)
    }

    /**
     * Deletes a category from the system by its name.
     *
     * @param name The name of the category to delete
     * @return true if the category was successfully deleted, false if the category doesn't exist
     */
    fun delete(name: String): Boolean {
        return categoryRepository.delete(name)
    }

    /**
     * Updates an existing category with new information.
     *
     * @param name The name of the category to update
     * @param new The new Category object containing updated information
     * @return true if the category was successfully updated, false if the category doesn't exist or if there's a conflict
     */
    fun update(name: String, new: Category): Boolean {
        return categoryRepository.update(name, new)
    }
}