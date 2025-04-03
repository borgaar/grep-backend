package com.example.grepapp.repository

import com.example.grepapp.model.Category
import com.example.grepapp.model.User
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

    fun create(category: Category) {
        val sql = "INSERT INTO categories (name) VALUES (?)";
        jdbc.update(sql, category.name)
    }

    fun delete(category: Category) {
        val sql = "DELETE FROM categories WHERE name = ?";
        jdbc.update(sql, category.name)
    }

    fun update(oldCategory: Category, newCategory: Category) {
        val sql = "UPDATE categories SET name = ? WHERE name = ?;";
        jdbc.update(sql, oldCategory.name)
    }

    fun getAll(page: Int, pageSize: Int): List<Category> {
        val sql = "SELECT * FROM categories ORDER BY name ASC LIMIT ? OFFSET ?;";
        return jdbc.query(sql, rowMapper, pageSize, pageSize*page)
    }
}