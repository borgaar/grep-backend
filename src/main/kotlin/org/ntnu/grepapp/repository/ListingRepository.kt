package org.ntnu.grepapp.repository

import org.ntnu.grepapp.model.*
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
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
                firstName = rs.getString("first_name"),
                lastName = rs.getString("last_name"),
            ),
            price = rs.getInt("price"),
            timestamp = rs.getTimestamp("created_at").toLocalDateTime(),
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

    fun filterPaginate(page: Pageable, filter: ListingFilter): List<Listing> {
        val sorting = when (filter.sorting) {
            "price" -> "l.price"
            else -> "l.id"
        }

        val sortingDir = when (filter.sortingDirection) {
            "desc" -> "DESC"
            else -> "ASC"
        }

        val base = """
            SELECT
                l.id, l.title, l.description, l.price, l.lat, l.lon,
                l.category, u.phone, u.first_name, u.last_name
            FROM listings l
                JOIN users u ON l.author = u.phone
            WHERE ? <= l.price AND l.price <= ?
        """

        val where = if (filter.categories.isNotEmpty()) {
            val params = filter.categories.joinToString(",") { "?" }
            "AND l.category IN ($params)"
        } else {
            "AND TRUE"
        }

        val sql = """
            $base
            $where
                AND Locate(?, l.title) != 0
            ORDER BY $sorting $sortingDir, l.id
            LIMIT ?
            OFFSET ?
        """

        val parameters = ArrayList<Any>()
        parameters.add(filter.priceLower ?: 0)
        parameters.add(filter.priceUpper ?: Int.MAX_VALUE)

        if (filter.categories.isNotEmpty()) {
            parameters.addAll(filter.categories)
        }

        parameters.add(filter.titleQuery ?: "")
        parameters.add(page.pageSize)
        parameters.add(page.offset)

        return jdbc.query(
            sql,
            rowMapper,
            *parameters.toTypedArray()
        )
    }

    fun create(listing: NewListing): Boolean {
        val sql = """
            INSERT INTO listings (id, title, description, category, price, lat, lon, author)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """
        val affected = jdbc.update(
            sql,
            listing.id.toString(),
            listing.title,
            listing.description,
            listing.category,
            listing.price,
            listing.lat,
            listing.lon,
            listing.authorPhone
        )
        return affected != 0
    }

    fun delete(id: UUID): Boolean {
        val sql = """
            DELETE FROM listings WHERE id = ?
        """
        val affected = jdbc.update(sql, id.toString())
        return affected != 0
    }

    fun update(id: UUID, new: UpdateListing): Boolean {
        val sql = """
            UPDATE listings
            SET title = ?, description = ?,
                category = ?, price = ?, lat = ?, lon = ?
            WHERE id = ?
        """
        val affected =
            jdbc.update(sql, new.title, new.description, new.category, new.price, new.lat, new.lon, id.toString())
        return affected != 0
    }


}
