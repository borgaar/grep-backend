package org.ntnu.grepapp.repository

import org.ntnu.grepapp.model.Image
import org.ntnu.grepapp.model.NewImage
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Repository class for handling database operations related to images.
 * Provides methods for storing and retrieving image data.
 */
@Repository
class ImageRepository(
    private val jdbc: JdbcTemplate
) {

    /**
     * Row mapper for converting database rows to Image objects.
     */
    private val rowMapper = RowMapper { rs, _ ->
        Image(
            id = UUID.fromString(rs.getString("id")),
            buffer = rs.getString("buffer")
        )
    }

    /**
     * Loads multiple images from the database by their IDs.
     *
     * @param imageIds A list of UUIDs identifying the images to load
     * @return A list of Image objects containing the requested images' data
     */
    fun load(imageIds: List<UUID>): List<Image> {
        // they are guaranteed to be UUIDs, so this is fine
        var idList = imageIds.joinToString("', '")
        if (idList.isNotEmpty()) {
            idList = "'$idList'"
        } else {
            return emptyList()
        }

        val sql = """
            SELECT i.id, i.buffer
            FROM images i
            WHERE i.id IN ($idList)
        """
        return jdbc.query(sql, rowMapper)
    }

    /**
     * Saves a new image to the database.
     *
     * @param image The NewImage object containing the image data to save
     * @return The UUID of the saved image
     */
    fun save(image: NewImage): UUID {
        // save
        val sql = """
            INSERT INTO images (id, buffer)
            VALUES (?, ?)
        """
        jdbc.update(sql, image.id.toString(), image.buffer)
        return image.id
    }
}