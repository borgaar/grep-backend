package org.ntnu.grepapp.repository

import org.ntnu.grepapp.model.Image
import org.ntnu.grepapp.model.NewImage
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class ImageRepository(
    private val jdbc: JdbcTemplate
) {
    private val rowMapper = RowMapper { rs, _ ->
        Image(
            id = UUID.fromString(rs.getString("id")),
            buffer = rs.getString("buffer")
        )
    }

    fun load(imageIds: List<UUID>): List<Image> {
        // they are guaranteed to be UUIDs, so this is fine
        var idList = imageIds.joinToString("', '")
        if (idList.isNotEmpty()) {
            idList = "'$idList'"
        }
        val sql = """
            SELECT i.id, i.buffer
            FROM images i
            WHERE i.id IN ($idList)
        """
        return jdbc.query(sql, rowMapper)
    }

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