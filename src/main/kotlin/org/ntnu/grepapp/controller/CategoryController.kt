package org.ntnu.grepapp.controller

import org.ntnu.grepapp.dto.category.*
import org.ntnu.grepapp.model.Category
import org.ntnu.grepapp.service.CategoryService
import org.apache.logging.log4j.LogManager
import org.ntnu.grepapp.dto.CategoryDTO
import org.ntnu.grepapp.security.JwtUtil
import org.ntnu.grepapp.security.JwtUtil.*
import org.ntnu.grepapp.service.AuthService
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/category")
@CrossOrigin(origins = ["http://localhost:5173"])
class CategoryController(
    private val categoryService: CategoryService,
    private val authService: AuthService,
) {
    private val logger = LogManager.getLogger(this::class::java);

    /**
     * Retrieves a paginated list of all categories.
     *
     * @param page The page number to retrieve (zero-based)
     * @param pageSize The number of categories to include per page
     * @return A list of CategoryDTO objects representing the categories on the requested page
     */
    @GetMapping
    fun getAll(
        @RequestParam page: Int,
        @RequestParam pageSize: Int
    ): List<CategoryDTO> {
        val list = categoryService.getAll(PageRequest.of(page, pageSize));
        return list.map { CategoryDTO(it.name) }
    }

    /**
     * Creates a new category in the system.
     * This endpoint is restricted to admin users only.
     *
     * @param request The CategoryCreateRequest containing the name of the new category
     * @return ResponseEntity with HTTP status:
     *         - OK if the category was successfully created
     *         - CONFLICT if a category with the same name already exists
     *         - FORBIDDEN if the current user is not an admin
     */
    @PostMapping("/create")
    fun create(@RequestBody request: CategoryCreateRequest): ResponseEntity<Unit> {
        val user = authService.getCurrentUser()
        if (user.isAdmin()) { return ResponseEntity(HttpStatus.FORBIDDEN) }

        val category = Category(request.name);
        val status = if (categoryService.create(category)) {
            HttpStatus.OK
        } else {
            HttpStatus.CONFLICT
        }
        return ResponseEntity(status)
    }

    /**
     * Updates an existing category with a new name.
     * This endpoint is restricted to admin users only.
     *
     * @param request The CategoryUpdateRequest containing the old category name and the new category details
     * @return ResponseEntity with HTTP status:
     *         - OK if the category was successfully updated
     *         - CONFLICT if a category with the new name already exists or the old category wasn't found
     *         - FORBIDDEN if the current user is not an admin
     */
    @PatchMapping("/update")
    fun update(@RequestBody request: CategoryUpdateRequest): ResponseEntity<Unit> {
        val user = authService.getCurrentUser()
        if (user.isAdmin()) { return ResponseEntity(HttpStatus.FORBIDDEN) }

        val new = Category(request.new.name);
        val status = if (categoryService.update(request.oldName, new)) {
            HttpStatus.OK
        } else {
            HttpStatus.CONFLICT
        }
        return ResponseEntity(status)
    }

    /**
     * Deletes a category from the system by its name.
     * This endpoint is restricted to admin users only.
     *
     * @param name The name of the category to delete
     * @return ResponseEntity with HTTP status:
     *         - OK if the category was successfully deleted
     *         - NOT_FOUND if the category doesn't exist
     *         - FORBIDDEN if the current user is not an admin
     */
    @DeleteMapping("/delete/{name}")
    fun delete(@PathVariable name: String): ResponseEntity<Unit> {
        val user = authService.getCurrentUser()
        if (user.isAdmin()) { return ResponseEntity(HttpStatus.FORBIDDEN) }

        val status = if (categoryService.delete(name)) {
            HttpStatus.OK
        } else {
            HttpStatus.NOT_FOUND
        }
        return ResponseEntity(status)
    }
}
