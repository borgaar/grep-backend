package org.ntnu.grepapp.controller

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.ntnu.grepapp.dto.image.UploadImageResponse
import org.ntnu.grepapp.service.ImageService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.mock.web.MockMultipartFile
import java.util.*

class ImageControllerTest {

    private lateinit var imageService: ImageService
    private lateinit var imageController: ImageController

    @BeforeEach
    fun setup() {
        imageService = mock(ImageService::class.java)
        imageController = ImageController(imageService)
    }

    @Test
    fun `should upload image and return id`() {
        val mockFile = MockMultipartFile("file", "bilde.jpg", "image/jpeg", byteArrayOf(1, 2, 3))
        val expectedId = UUID.randomUUID()
        `when`(imageService.save(mockFile.bytes)).thenReturn(expectedId)

        val response: ResponseEntity<UploadImageResponse> = imageController.upload(mockFile)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expectedId.toString(), response.body?.id)
    }

    @Test
    fun `should download images and return map`() {
        val imageId = UUID.randomUUID()
        val imageData = byteArrayOf(4, 5, 6)
        val imageMock = org.ntnu.grepapp.model.ImageFile(imageId, imageData)
        `when`(imageService.load(listOf(imageId))).thenReturn(listOf(imageMock))

        val response: ResponseEntity<Map<String, ByteArray>> = imageController.download(listOf(imageId.toString()))

        assertEquals(HttpStatus.OK, response.statusCode)
        assertTrue(response.body!!.containsKey(imageId.toString()))
        assertArrayEquals(imageData, response.body!![imageId.toString()])
    }
}
