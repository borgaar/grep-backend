package org.ntnu.grepapp.repository

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.ntnu.grepapp.model.NewImage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.jdbc.Sql
import java.util.*

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = ["/schema.sql"], executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ImageRepositoryTest {
    @Autowired
    private lateinit var jdbc: JdbcTemplate

    private lateinit var imageRepository: ImageRepository

    private val testImage = NewImage(
        buffer = "base64bildedata_for_testbilde"
    )

    @BeforeEach
    fun setup() {
        imageRepository = ImageRepository(jdbc)
        jdbc.execute("DELETE FROM images")
    }

    @Test
    fun `should save and load image`() {
        val savedId = imageRepository.save(testImage)
        val loadedImages = imageRepository.load(listOf(savedId))

        assertEquals(1, loadedImages.size)

        val loadedImage = loadedImages.first()
        assertEquals(testImage.buffer, loadedImage.buffer)
    }

    @Test
    fun `should load multiple images`() {
        val testImage1 = NewImage(buffer = "bildedata_for_fjell")
        val testImage2 = NewImage(buffer = "bildedata_for_sjø")
        val testImage3 = NewImage(buffer = "bildedata_for_skog")

        val id1 = imageRepository.save(testImage1)
        val id2 = imageRepository.save(testImage2)
        imageRepository.save(testImage3)

        val loadedImages = imageRepository.load(listOf(id1, id2))

        assertEquals(2, loadedImages.size)

        val imageIds = loadedImages.map { it.id }
        assertTrue(imageIds.contains(id1))
        assertTrue(imageIds.contains(id2))

        val image1 = loadedImages.find { it.id == id1 }
        val image2 = loadedImages.find { it.id == id2 }

        assertEquals(testImage1.buffer, image1?.buffer)
        assertEquals(testImage2.buffer, image2?.buffer)
    }

    @Test
    fun `should return empty list when no matching images found`() {
        val nonExistentId = UUID.randomUUID()

        val loadedImages = imageRepository.load(listOf(nonExistentId))

        assertTrue(loadedImages.isEmpty())
    }

    @Test
    fun `should handle empty list of image IDs`() {
        val loadedImages = imageRepository.load(emptyList())

        assertTrue(loadedImages.isEmpty())
    }
}
