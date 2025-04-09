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

@Service
class ImageService (
    private val repository: ImageRepository,
){
    private val encoder = Base64.getEncoder()

    fun load(imageIds: List<UUID>): List<Image> {
        return repository.load(imageIds)
    }

    fun save(bytes: ByteArray): UUID {
        val base64 = encoder.encodeToString(bytes)
        val image = NewImage(
            buffer = base64
        )
        return repository.save(image)
    }
}