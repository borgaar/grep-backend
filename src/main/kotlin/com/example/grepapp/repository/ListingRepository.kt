package com.example.grepapp.repository

import com.example.grepapp.model.Category
import com.example.grepapp.model.Listing
import com.example.grepapp.model.ListingAuthor
import com.example.grepapp.model.User
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class ListingRepository(
    private val jdbc: JdbcTemplate
) {
    private val rowMapper = RowMapper { rs, _ ->
        Listing(
            id = UUID.fromString(rs.getString("id")),
            title = rs.getString("title"),
            description = rs.getString("description"),
            author = ListingAuthor(
                phone = rs.getString("phone"),
                first_name = rs.getString("first_name"),
                last_name = rs.getString("last_name"),
            ),
            price = rs.getInt("price"),
            lat = rs.getDouble("lat"),
            lon = rs.getDouble("lon"),
            category = Category(
                name = rs.getString("category"),
            )
        )
    }

    fun find(id: UUID): Listing? {
        val sql = """
            SELECT
                l.id, l.title, l.description, l.price, l.lat, l.lon,
                l.category, u.phone, u.first_name, u.last_name
            FROM listings l
                JOIN users u ON l.author = u.phone
            WHERE id = ?;
        """
        return try {
            jdbc.queryForObject(sql, rowMapper, id.toString())
        } catch (e: EmptyResultDataAccessException) {
            null
        }
    }
}
