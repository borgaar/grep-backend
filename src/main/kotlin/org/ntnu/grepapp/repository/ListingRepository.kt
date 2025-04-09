package org.ntnu.grepapp.repository

import org.ntnu.grepapp.model.*
import org.springframework.dao.DataAccessException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.SQLException
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
            ),
            isBookmarked = rs.getBoolean("is_bookmarked"),
        )
    }

    private val bookmarkedMapper = RowMapper { rs, i ->
        BookmarkedListing(
            listing = rowMapper.mapRow(rs, i)!!,
            bookmarkedAt = rs.getTimestamp("bookmarked_at").toLocalDateTime()
        )
    }

    fun find(id: UUID, userId: String): Listing? {
        val sql = """
            SELECT
                l.id, l.title, l.description, l.price, l.created_at, l.lat, l.lon,
                l.category, u.phone, u.first_name, u.last_name, b.user_id IS NOT NULL AS is_bookmarked
            FROM listings l
                JOIN users u ON l.author = u.phone
                LEFT JOIN bookmarks b ON b.listing_id = l.id AND b.user_id = ?
            WHERE id = ?;
        """
        return try {
            jdbc.queryForObject(sql, rowMapper, userId, id.toString())
        } catch (e: EmptyResultDataAccessException) {
            null
        }
    }

    fun filterPaginate(page: Pageable, filter: ListingFilter, userId: String): List<Listing> {
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
                l.id, l.title, l.description, l.price, l.created_at, l.lat, l.lon,
                l.category, u.phone, u.first_name, u.last_name, b.user_id IS NOT NULL AS is_bookmarked
            FROM listings l
                JOIN users u ON l.author = u.phone
                LEFT JOIN bookmarks b ON b.listing_id = l.id AND b.user_id = ?
            WHERE ? <= l.price AND l.price <= ?
        """

        val where = if (filter.categories.isNotEmpty()) {
            val params = filter.categories.joinToString(",") { "?" }
            "AND l.category IN ($params)"
        } else {
            "AND TRUE"
        }

        var keywordSearchWhere = " AND ("
        var keywords: List<String> = ArrayList();
        if (!filter.searchQuery.isNullOrEmpty()) {
            keywords = filter.searchQuery.split(" ");
            keywords.forEach({
                keywordSearchWhere += "l.title LIKE ? OR l.description LIKE ? OR "
            });
            keywordSearchWhere += "FALSE)"
        } else {
            keywordSearchWhere += "TRUE)"
        }



        val sql = """
            $base
            $where
            $keywordSearchWhere
            ORDER BY $sorting $sortingDir, l.id
            LIMIT ?
            OFFSET ?
        """

        val parameters = ArrayList<Any>()
        parameters.add(userId.toString())
        parameters.add(filter.priceLower ?: 0)
        parameters.add(filter.priceUpper ?: Int.MAX_VALUE)

        if (filter.categories.isNotEmpty()) {
            parameters.addAll(filter.categories)
        }

        parameters.addAll(keywords.flatMap { keyword ->
            listOf("%$keyword%", "%$keyword%")
        });

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

    fun getListingsForUserId(userId: String, page: Pageable): List<Listing> {
        val sql = """
            SELECT
                l.id, l.title, l.description, l.price, l.created_at, l.lat, l.lon,
                l.category, u.phone, u.first_name, u.last_name, b.user_id IS NOT NULL AS is_bookmarked
            FROM listings l
                JOIN users u ON l.author = u.phone
                LEFT JOIN bookmarks b ON b.listing_id = l.id AND b.user_id = ?
            WHERE u.phone = ?
            LIMIT ? OFFSET ?;
        """;

        return jdbc.query(sql, rowMapper, userId, userId, page.pageSize, page.offset)
    }

    fun getBookmarked(userId: String, pageable: Pageable): List<BookmarkedListing> {
        val sql = """
            SELECT 
                l.id, l.title, l.description, l.price, l.created_at, l.lat, l.lon,
                l.category, u.phone, u.first_name, u.last_name, b.created_at AS bookmarked_at, TRUE AS is_bookmarked
            FROM bookmarks b 
                JOIN listings l ON b.listing_id = l.id
                JOIN users u ON l.author = u.phone
            WHERE b.user_id = ?
            ORDER BY bookmarked_at DESC
            LIMIT ? OFFSET ?
        """;
        return jdbc.query(sql, bookmarkedMapper, userId, pageable.pageSize, pageable.offset);
    }

    fun createBookmark(listingId: UUID, userId: String): Boolean {
        val sql = """
            INSERT INTO bookmarks (user_id, listing_id) VALUES (?, ?)
        """;
        return try { jdbc.update(sql, userId, listingId.toString()); true }
        catch (e: DataAccessException) { false };
    }

    fun deleteBookmark(listingId: UUID, userId: String): Boolean {
        val sql = """
            DELETE FROM bookmarks WHERE user_id = ? AND listing_id = ?;
        """;
        return jdbc.update(sql, userId, listingId.toString()) != 0;
    }
}
