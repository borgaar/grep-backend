package org.ntnu.grepapp.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.ntnu.grepapp.model.Image
import org.ntnu.grepapp.model.NewImage
import org.ntnu.grepapp.repository.ImageRepository
import java.util.*

class ImageServiceTest {

    private lateinit var repository: ImageRepository
    private lateinit var imageService: ImageService

    private val testImageId = UUID.randomUUID()
    private val testBytes = "Testbilde".toByteArray()
    private val testBase64 = Base64.getEncoder().encodeToString(testBytes)

    @BeforeEach
    fun setup() {
        repository = mock(ImageRepository::class.java)
        imageService = ImageService(repository)
    }

    @Test
    fun `should load images`() {
        val image = Image(id = testImageId, buffer = testBase64)
        `when`(repository.load(listOf(testImageId))).thenReturn(listOf(image))

        val loadedImages = imageService.load(listOf(testImageId))

        assertEquals(1, loadedImages.size)
        assertEquals(testImageId, loadedImages.first().id)
        assertArrayEquals(testBytes, loadedImages.first().buffer)
    }

    @Test
    fun `should save image and return UUID`() {
        `when`(repository.save(NewImage(buffer = testBase64))).thenReturn(testImageId)

        val savedId = imageService.save(testBytes)

        assertEquals(testImageId, savedId)
    }
}
