package org.ntnu.grepapp.repository

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.ntnu.grepapp.model.Category
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.data.domain.PageRequest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.jdbc.Sql

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = ["/schema.sql"], executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class CategoryRepositoryTest {

    @Autowired
    private lateinit var jdbc: JdbcTemplate
    private lateinit var categoryRepository: CategoryRepository

    private val testCategory = Category(
        name = "PC-deler"
    )

    @BeforeEach
    fun setup() {
        categoryRepository = CategoryRepository(jdbc)
        jdbc.execute("DELETE FROM categories")
    }

    @Test
    fun `should create category successfully`() {
        val result = categoryRepository.create(testCategory)

        assertTrue(result)
        val categories = jdbc.query("SELECT * FROM categories WHERE name = ?",
            { rs, _ -> Category(rs.getString("name")) },
            testCategory.name)

        assertEquals(1, categories.size)
        assertEquals(testCategory, categories[0])
    }

    @Test
    fun `should delete category successfully`() {
        categoryRepository.create(testCategory)

        val result = categoryRepository.delete(testCategory.name)

        assertTrue(result)
        val categories = jdbc.query("SELECT * FROM categories WHERE name = ?",
            { rs, _ -> Category(rs.getString("name")) },
            testCategory.name)

        assertTrue(categories.isEmpty())
    }

    @Test
    fun `should return false when deleting non-existent category`() {
        val result = categoryRepository.delete("NonExistentCategory")

        assertFalse(result)
    }

    @Test
    fun `should update category successfully`() {
        categoryRepository.create(testCategory)

        val updatedCategory = Category("Sofa")
        val result = categoryRepository.update(testCategory.name, updatedCategory)

        assertTrue(result)

        val oldCategories = jdbc.query("SELECT * FROM categories WHERE name = ?",
            { rs, _ -> Category(rs.getString("name")) },
            testCategory.name)
        assertTrue(oldCategories.isEmpty())

        val newCategories = jdbc.query("SELECT * FROM categories WHERE name = ?",
            { rs, _ -> Category(rs.getString("name")) },
            updatedCategory.name)
        assertEquals(1, newCategories.size)
        assertEquals(updatedCategory, newCategories[0])
    }

    @Test
    fun `should return false when updating non-existent category`() {
        val updatedCategory = Category("Bøker")
        val result = categoryRepository.update("NonExistentCategory", updatedCategory)

        assertFalse(result)
    }

    @Test
    fun `should get all categories with pagination`() {
        val categories = listOf(
            Category("PC-deler"),
            Category("Bøker"),
            Category("Maleri"),
            Category("Klær"),
            Category("Platespiller")
        )

        categories.forEach { categoryRepository.create(it) }

        val firstPageRequest = PageRequest.of(0, 2)
        val firstPage = categoryRepository.getAll(firstPageRequest)

        assertEquals(2, firstPage.size)
        assertTrue(categories.contains(firstPage[0]))
        assertTrue(categories.contains(firstPage[1]))

        val secondPageRequest = PageRequest.of(1, 2)
        val secondPage = categoryRepository.getAll(secondPageRequest)

        assertEquals(2, secondPage.size)
        assertTrue(categories.contains(secondPage[0]))
        assertTrue(categories.contains(secondPage[1]))

        val thirdPageRequest = PageRequest.of(2, 2)
        val thirdPage = categoryRepository.getAll(thirdPageRequest)

        assertEquals(1, thirdPage.size)
        assertTrue(categories.contains(thirdPage[0]))
    }

    @Test
    fun `should return empty list when requested page exceeds available items`() {
        categoryRepository.create(testCategory)

        val pageRequest = PageRequest.of(1, 10)
        val result = categoryRepository.getAll(pageRequest)

        assertTrue(result.isEmpty())
    }
}
