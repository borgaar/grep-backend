package org.ntnu.grepapp.repository

import org.ntnu.grepapp.model.*
import org.springframework.dao.DataAccessException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.collections.ArrayList

/**
 * Repository class for handling database operations related to listings.
 * Provides methods for creating, retrieving, updating, and managing listings and bookmarks.
 */
@Repository
class ListingRepository(
    private val jdbc: JdbcTemplate
) {

    /**
     * Row mapper for converting database rows to Listing objects.
     */
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
            updatedAt = rs.getTimestamp("updated_at").toLocalDateTime(),
            lat = rs.getDouble("lat"),
            lon = rs.getDouble("lon"),
            category = Category(
                name = rs.getString("category"),
            ),
            imageIds = ArrayList(),
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

    /**
     * Row mapper for converting database rows to BookmarkedListing objects.
     */
    private val bookmarkedMapper = RowMapper { rs, i ->
        BookmarkedListing(
            listing = rowMapper.mapRow(rs, i)!!,
            bookmarkedAt = rs.getTimestamp("bookmarked_at").toLocalDateTime()
        )
    }

    /**
     * Retrieves the image IDs associated with a listing.
     *
     * @param id The UUID of the listing
     * @return A list of UUIDs representing the images associated with the listing
     */
    private fun addImagesForListing(id: UUID): List<UUID> {
        // expensive, but simpler and less error-prone than a proper query
        val sql = """
            SELECT li.image_id
            FROM listing_images li
            WHERE li.listing_id = ?
        """
        val mapper = RowMapper { rs, _ ->
            UUID.fromString(rs.getString("image_id"))
        }
        return jdbc.query(sql, mapper, id.toString())
    }

    /**
     * Finds a specific listing by its ID.
     *
     * @param id The UUID of the listing to find
     * @param userId The ID of the user making the request (for bookmark status)
     * @return The Listing object if found, null otherwise
     */
    fun find(id: UUID, userId: String): Listing? {
        val sql = """
            SELECT
                l.id, l.title, l.description, l.price, l.created_at, l.lat, l.lon, l.updated_at,
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
        val listing = try {
            jdbc.queryForObject(sql, rowMapper, userId, id.toString()) !!
        } catch (e: EmptyResultDataAccessException) {
            return null
        }

        listing.imageIds = addImagesForListing(id)
        return listing
    }

    /**
     * Retrieves a paginated and filtered list of listings.
     *
     * @param page Pagination information including page size and offset
     * @param filter Filter criteria including price range, categories, search query, and sorting
     * @param userId The ID of the user making the request (for bookmark status)
     * @return A PaginatedListings object containing the listings and total count
     */
    fun filterPaginate(page: Pageable, filter: ListingFilter, userId: String): PaginatedListings {
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
            l.id, l.title, l.description, l.price, l.created_at, l.lat, l.lon, l.updated_at,
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

        val countBase = """
        SELECT COUNT(*) 
            FROM listings l
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

        // separate query to get the total number of listings
        val countSql = """
            $countBase
            $where
            $keywordSearchWhere
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

        // copy of parameters for the count query
        val countParameters = ArrayList<Any>(parameters)

        parameters.add(page.pageSize)
        parameters.add(page.offset)

        val listings = jdbc.query(
            sql, rowMapper, *parameters.toTypedArray()
        )

        // total number of listings
        val totalCount = jdbc.queryForObject(
            countSql,
            Int::class.java,
            *countParameters.toTypedArray()
        ) ?: 0

        for (l in listings) {
            l.imageIds = addImagesForListing(l.id)
        }

        return PaginatedListings(
            listings,
            totalListings = totalCount,
        )
    }

    /**
     * Creates a new listing in the database.
     *
     * @param listing The NewListing object containing listing details
     * @return true if the listing was successfully created, false otherwise
     */
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

    /**
     * Deletes a listing by its ID.
     * Only the author of the listing or an admin can delete it.
     * Also removes associated bookmarks.
     *
     * @param id The UUID of the listing to delete
     * @param userId The ID of the user attempting to delete the listing
     * @param isAdmin Whether the user has admin privileges
     * @return true if the listing was successfully deleted, false otherwise
     */
    @Transactional
    fun delete(id: UUID, userId: String, isAdmin: Boolean): Boolean {
        val parameters = ArrayList<String>()
        val bookmarkSql: String;
        val listingSql: String;
        if (isAdmin) {
            parameters.addAll(listOf(id.toString()))
            bookmarkSql = """
                DELETE FROM bookmarks
                    WHERE listing_id = ?;
            """

            listingSql = """
                DELETE FROM listings WHERE id = ?;
            """
        } else {
            parameters.addAll(listOf(id.toString(), userId))
            bookmarkSql = """
                DELETE b FROM bookmarks b
                    JOIN listings l ON b.listing_id = l.id
                    WHERE l.id = ? AND l.author = ?;
            """

            listingSql = """
                DELETE FROM listings 
                    WHERE id = ? AND author = ?;
            """
        }

        jdbc.update(bookmarkSql, *parameters.toTypedArray())
        val affected = jdbc.update(listingSql, *parameters.toTypedArray())
        return affected != 0
    }

    /**
     * Updates an existing listing with new information.
     * Also updates associated images if provided.
     *
     * @param id The UUID of the listing to update
     * @param new The UpdateListing object containing the updated listing details
     * @return true if the listing was successfully updated, false otherwise
     */
    @Transactional
    fun update(id: UUID, new: UpdateListing): Boolean {
        val sql = """
            UPDATE listings
            SET title = ?, description = ?,
                category = ?, price = ?, lat = ?, lon = ?,
                updated_at = NOW()
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

        if (new.imageIds != null) {
            val deleteImage = """
                DELETE FROM listing_images
                WHERE listing_id = ?
            """
            val insertImage = """
                INSERT INTO listing_images (listing_id, image_id)
                VALUES (?, ?)
            """
            jdbc.update(deleteImage, id.toString())
            for (imageId in new.imageIds) {
                jdbc.update(insertImage, id.toString(), imageId.toString())
            }
        }

        return affected != 0
    }

    /**
     * Retrieves all listings created by a specific user.
     *
     * @param userId The ID of the user whose listings to retrieve
     * @param page Pagination information including page size and offset
     * @return A list of Listing objects created by the specified user
     */
    fun getListingsForUserId(userId: String, page: Pageable): List<Listing> {
        val sql = """
            SELECT
                l.id, l.title, l.description, l.price, l.created_at, l.lat, l.lon, l.updated_at,
                l.category, u.phone, u.first_name, u.last_name, b.user_id IS NOT NULL AS is_bookmarked
            FROM listings l
                JOIN users u ON l.author = u.phone
                LEFT JOIN bookmarks b ON b.listing_id = l.id AND b.user_id = ?
            WHERE u.phone = ?
            LIMIT ? OFFSET ?;
        """;

        return jdbc.query(sql, rowMapper, userId, userId, page.pageSize, page.offset)
    }

    /**
     * Retrieves all listings bookmarked by a specific user.
     *
     * @param userId The ID of the user whose bookmarks to retrieve
     * @param pageable Pagination information including page size and offset
     * @return A list of BookmarkedListing objects for the specified user
     */
    fun getBookmarked(userId: String, pageable: Pageable): List<BookmarkedListing> {
        val sql = """
            SELECT 
                l.id, l.title, l.description, l.price, l.created_at, l.lat, l.lon, l.updated_at,
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

    /**
     * Creates a bookmark for a listing.
     *
     * @param listingId The UUID of the listing to bookmark
     * @param userId The ID of the user creating the bookmark
     * @return true if the bookmark was successfully created, false if it already exists
     */
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

    /**
     * Deletes a bookmark for a listing.
     *
     * @param listingId The UUID of the listing to remove from bookmarks
     * @param userId The ID of the user removing the bookmark
     * @return true if the bookmark was successfully deleted, false if it didn't exist
     */
    fun deleteBookmark(listingId: UUID, userId: String): Boolean {
        val sql = """
            DELETE FROM bookmarks WHERE user_id = ? AND listing_id = ?;
        """;
        return jdbc.update(sql, userId, listingId.toString()) != 0;
    }

    /**
     * Marks a listing as reserved for a specific user.
     *
     * @param listingId The UUID of the listing to mark as reserved
     * @param reservedUserID The ID of the user reserving the listing, or null to remove reservation
     * @return true if the reservation status was successfully updated, false otherwise
     */
    fun setReserved(listingId: UUID, reservedUserID: String?): Boolean {
        val sql = """
            UPDATE listings SET reserved_by = ? WHERE id = ?;
        """;
        return jdbc.update(sql, reservedUserID, listingId.toString()) != 0;
    }

    /**
     * Marks a listing as sold to a specific user.
     *
     * @param listingId The UUID of the listing to mark as sold
     * @param soldUserID The ID of the user who purchased the listing, or null to remove sold status
     * @return true if the sold status was successfully updated, false otherwise
     */
    fun markAsSold(listingId: UUID, soldUserID: String?): Boolean {
        val sql = """
            UPDATE listings SET sold_to = ? WHERE id = ?;
        """;
        return jdbc.update(sql, soldUserID, listingId.toString()) != 0;
    }
}
