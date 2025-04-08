package org.ntnu.grepapp.repository

import org.ntnu.grepapp.model.Category
import org.ntnu.grepapp.model.User
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

@Repository
class CategoryRepository (private var jdbc: JdbcTemplate)
{
    private val rowMapper = RowMapper { rs, _ ->
        Category(
            name = rs.getString("name"),
        )
    }

    private val logger = LogManager.getLogger(this::class::java);

    fun create(category: Category): Boolean {
        val sql = "INSERT INTO categories (name) VALUE (?)";
        return jdbc.update(sql, category.name) != 0
    }

    fun delete(name: String): Boolean {
        val sql = "DELETE FROM categories WHERE name = ?";
        return jdbc.update(sql, name) != 0
    }

    fun update(name: String, newCategory: Category): Boolean {
        val sql = "UPDATE categories SET name = ? WHERE name = ?;";
        return jdbc.update(sql, newCategory.name, name) != 0
    }

    fun getAll(page: Pageable): List<Category> {
        val sql = "SELECT * FROM categories ORDER BY name LIMIT ? OFFSET ?;";
        return jdbc.query(sql, rowMapper, page.pageSize, page.offset)
    }
}