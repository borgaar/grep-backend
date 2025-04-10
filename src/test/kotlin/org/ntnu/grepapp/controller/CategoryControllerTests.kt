package org.ntnu.grepapp.controller

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.ntnu.grepapp.dto.category.CategoryCreateRequest
import org.ntnu.grepapp.dto.category.CategoryUpdateRequest
import org.ntnu.grepapp.dto.CategoryDTO
import org.ntnu.grepapp.model.Category
import org.ntnu.grepapp.model.User
import org.ntnu.grepapp.security.UserClaims
import org.ntnu.grepapp.service.AuthService
import org.ntnu.grepapp.service.CategoryService
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class CategoryControllerTest {

    private lateinit var categoryService: CategoryService
    private lateinit var authService: AuthService
    private lateinit var categoryController: CategoryController

    private val testCategory = Category("Elektronikk")
    private val testCategoryDTO = CategoryDTO(testCategory.name)
    private val mockUser = mock(UserClaims::class.java)

    @BeforeEach
    fun setup() {
        categoryService = mock(CategoryService::class.java)
        authService = mock(AuthService::class.java)
        categoryController = CategoryController(categoryService, authService)
    }

    @Test
    fun `should return forbidden when creating category as non-admin`() {
        `when`(authService.getCurrentUser()).thenReturn(mockUser)
        `when`(mockUser.isNotAdmin()).thenReturn(true)

        val result = categoryController.create(CategoryCreateRequest("Hage"))
        assertEquals(HttpStatus.FORBIDDEN, result.statusCode)
    }

    @Test
    fun `should delete category when user is admin`() {
        `when`(authService.getCurrentUser()).thenReturn(mockUser)
        `when`(mockUser.isNotAdmin()).thenReturn(false)
        `when`(categoryService.delete(anyString())).thenReturn(true)

        val result = categoryController.delete("Sport")
        assertEquals(HttpStatus.OK, result.statusCode)
    }
}
