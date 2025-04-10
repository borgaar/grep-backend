package org.ntnu.grepapp.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.ntnu.grepapp.model.Category
import org.ntnu.grepapp.model.Image
import org.ntnu.grepapp.model.NewImage
import org.ntnu.grepapp.repository.CategoryRepository
import org.ntnu.grepapp.repository.ImageRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

/**
 * Service class that manages image operations.
 * Provides methods for storing and retrieving images.
 */
@Service
class ImageService (
    private val repository: ImageRepository,
){
    private val encoder = Base64.getEncoder()

    /**
     * Retrieves multiple images by their IDs.
     *
     * @param imageIds A list of UUIDs identifying the images to load
     * @return A list of Image objects for the requested IDs
     */
    fun load(imageIds: List<UUID>): List<Image> {
        return repository.load(imageIds)
    }

    /**
     * Saves a new image to the system.
     * Converts the binary image data to a Base64 encoded string before saving.
     *
     * @param bytes The binary data of the image to save
     * @return The UUID assigned to the newly saved image
     */
    fun save(bytes: ByteArray): UUID {
        val base64 = encoder.encodeToString(bytes)
        val image = NewImage(
            buffer = base64
        )
        return repository.save(image)
    }
}