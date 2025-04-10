package org.ntnu.grepapp.repository

import org.ntnu.grepapp.model.Category
import org.ntnu.grepapp.model.User
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

/**
 * Repository class for handling database operations related to categories in the MySQL database.
 * Provides CRUD operations for the categories table.
 */
@Repository
class CategoryRepository (private var jdbc: JdbcTemplate)
{

    /**
     * Row mapper for converting database rows to Category objects.
     */
    private val rowMapper = RowMapper { rs, _ ->
        Category(
            name = rs.getString("name"),
        )
    }

    private val logger = LogManager.getLogger(this::class::java);

    /**
     * Creates a new category in the database.
     *
     * @param category The Category object to be persisted
     * @return true if the category was successfully created (row count > 0), false otherwise
     */
    fun create(category: Category): Boolean {
        val sql = "INSERT INTO categories (name) VALUE (?)";
        return jdbc.update(sql, category.name) != 0
    }

    /**
     * Deletes a category from the database by its name.
     *
     * @param name The name of the category to delete
     * @return true if the category was successfully deleted (row count > 0), false if no category with the given name exists
     */
    fun delete(name: String): Boolean {
        val sql = "DELETE FROM categories WHERE name = ?";
        return jdbc.update(sql, name) != 0
    }

    /**
     * Updates an existing category with a new name.
     *
     * @param name The current name of the category to update
     * @param newCategory The Category object containing the new name
     * @return true if the category was successfully updated (row count > 0), false if no category with the given name exists
     */
    fun update(name: String, newCategory: Category): Boolean {
        val sql = "UPDATE categories SET name = ? WHERE name = ?;";
        return jdbc.update(sql, newCategory.name, name) != 0
    }

    /**
     * Retrieves a paginated list of all categories from the database.
     *
     * @param page Pagination information including page size and offset
     * @return A list of Category objects for the requested page, ordered by name
     */
    fun getAll(page: Pageable): List<Category> {
        val sql = "SELECT * FROM categories ORDER BY name LIMIT ? OFFSET ?;";
        return jdbc.query(sql, rowMapper, page.pageSize, page.offset)
    }
}