package org.ntnu.grepapp.repository

import org.ntnu.grepapp.model.*
import org.springframework.dao.DataAccessException
import org.springframework.dao.EmptyResultDataAccessException
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
            ),
            isBookmarked = rs.getBoolean("is_bookmarked"),
            reservedBy = if (rs.getNString("ru_phone") != null) User(
                phone = rs.getString("ru_phone"),
                passwordHash = rs.getString("ru_password_hash"),
                firstName = rs.getString("ru_first_name"),
                lastName = rs.getString("ru_last_name"),
                role = rs.getString("ru_role"),
            ) else null,
            soldTo = if (rs.getNString("su_phone") != null) User(
                phone = rs.getString("su_phone"),
                passwordHash = rs.getString("su_password_hash"),
                firstName = rs.getString("su_first_name"),
                lastName = rs.getString("su_last_name"),
                role = rs.getString("su_role"),
            ) else null
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
                l.category, u.phone, u.first_name, u.last_name, b.user_id IS NOT NULL AS is_bookmarked,
                ru.first_name AS ru_first_name, ru.phone AS ru_phone, ru.last_name AS ru_last_name, ru.password_hash AS ru_password_hash, ru.role AS ru_role,
                su.first_name AS su_first_name, su.phone AS su_phone, su.last_name AS su_last_name, su.password_hash AS su_password_hash, su.role AS su_role
            FROM listings l
                JOIN users u ON l.author = u.phone
                LEFT JOIN users ru ON l.reserved_by = ru.phone
                LEFT JOIN users su ON l.sold_to = su.phone
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
                l.category, u.phone, u.first_name, u.last_name, b.user_id IS NOT NULL AS is_bookmarked,
                ru.first_name AS ru_first_name, ru.phone AS ru_phone, ru.last_name AS ru_last_name, ru.password_hash AS ru_password_hash, ru.role AS ru_role,
                su.first_name AS su_first_name, su.phone AS su_phone, su.last_name AS su_last_name, su.password_hash AS su_password_hash, su.role AS su_role
            FROM listings l
                JOIN users u ON l.author = u.phone
                LEFT JOIN users ru ON l.reserved_by = ru.phone
                LEFT JOIN users su ON l.reserved_by = su.phone
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
            sql, rowMapper, *parameters.toTypedArray()
        )
    }

    fun create(listing: NewListing): Boolean {
        val sql = """
            INSERT INTO listings (id, title, description, category, price, lat, lon, author, reserved_by)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, NULL)
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

    fun delete(id: UUID, userId: String, userRole: String): Boolean {
        val parameters = ArrayList<String>()
        val sql = if (userRole == "admin") {
            parameters.add(id.toString())
            """
                DELETE FROM listings WHERE id = ?
            """
        } else {
            parameters.addAll(listOf(id.toString(), userId))
            """
                DELETE FROM listings WHERE id = ? AND author = ?
            """
        }
        val affected = jdbc.update(sql, *parameters.toTypedArray())
        return affected != 0
    }

    fun update(id: UUID, new: UpdateListing): Boolean {
        val sql = """
            UPDATE listings
            SET title = ?, description = ?,
                category = ?, price = ?, lat = ?, lon = ?
            WHERE id = ?
        """
        val affected = jdbc.update(
            sql,
            new.title,
            new.description,
            new.category,
            new.price,
            new.lat,
            new.lon,
            id.toString()
        )
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
        return try {
            jdbc.update(sql, userId, listingId.toString()); true
        } catch (e: DataAccessException) {
            false
        };
    }

    fun deleteBookmark(listingId: UUID, userId: String): Boolean {
        val sql = """
            DELETE FROM bookmarks WHERE user_id = ? AND listing_id = ?;
        """;
        return jdbc.update(sql, userId, listingId.toString()) != 0;
    }

    fun setReserved(listingId: UUID, reservedUserID: String?): Boolean {
        val sql = """
            UPDATE listings SET reserved_by = ? WHERE id = ?;
        """;
        return jdbc.update(sql, reservedUserID, listingId.toString()) != 0;
    }

    fun markAsSold(listingId: UUID, soldUserID: String?): Boolean {
        val sql = """
            UPDATE listings SET sold_to = ? WHERE id = ?;
        """;
        return jdbc.update(sql, soldUserID, listingId.toString()) != 0;
    }
}
