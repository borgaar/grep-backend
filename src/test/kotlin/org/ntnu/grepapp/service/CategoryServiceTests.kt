package org.ntnu.grepapp.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.ntnu.grepapp.model.Category
import org.ntnu.grepapp.repository.CategoryRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable

class CategoryServiceTest {

    private lateinit var categoryService: CategoryService
    private lateinit var categoryRepository: CategoryRepository

    private val testCategory = Category(
        name = "Sykkel",
    )

    @BeforeEach
    fun setup() {
        categoryRepository = mock(CategoryRepository::class.java)
        categoryService = CategoryService(categoryRepository)
    }

    @Test
    fun `should create category`() {
        `when`(categoryRepository.create(testCategory)).thenReturn(true)

        val result = categoryService.create(testCategory)

        assertTrue(result)
        verify(categoryRepository).create(testCategory)
    }

    @Test
    fun `should get all categories`() {
        val page = PageRequest.of(0, 10)
        val expectedCategories = listOf(
            testCategory,
            Category(name = "En kategori"),
            Category(name = "Bil")
        )

        `when`(categoryRepository.getAll(page)).thenReturn(expectedCategories)

        val result = categoryService.getAll(page)

        assertEquals(expectedCategories.size, result.size)
        assertEquals(expectedCategories, result)
        verify(categoryRepository).getAll(page)
    }

    @Test
    fun `should delete category`() {
        val categoryName = testCategory.name
        `when`(categoryRepository.delete(categoryName)).thenReturn(true)

        val result = categoryService.delete(categoryName)

        assertTrue(result)
        verify(categoryRepository).delete(categoryName)
    }

    @Test
    fun `should update category`() {
        val originalName = testCategory.name
        val updatedCategory = Category(
            name = "Oppdatert kategori",
        )

        `when`(categoryRepository.update(originalName, updatedCategory)).thenReturn(true)

        val result = categoryService.update(originalName, updatedCategory)

        assertTrue(result)
        verify(categoryRepository).update(originalName, updatedCategory)
    }
}