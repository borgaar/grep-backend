package org.ntnu.grepapp.repository

import org.ntnu.grepapp.model.Category
import org.ntnu.grepapp.model.User
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
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
        val sql = "INSERT INTO categories (name) VALUES (?)";
        return jdbc.update(sql, category.name) != 0
    }

    fun delete(category: Category): Boolean {
        val sql = "DELETE FROM categories WHERE name = ?";
        return jdbc.update(sql, category.name) != 0
    }

    fun update(oldCategory: Category, newCategory: Category): Boolean {
        val sql = "UPDATE categories SET name = ? WHERE name = ?;";
        return jdbc.update(sql, oldCategory.name) != 0
    }

    fun getAll(page: Int, pageSize: Int): List<Category> {
        val sql = "SELECT * FROM categories ORDER BY name ASC LIMIT ? OFFSET ?;";
        return jdbc.query(sql, rowMapper, pageSize, pageSize*page)
    }
}