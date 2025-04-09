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
    private val jwtUtil: JwtUtil,
) {
    private val logger = LogManager.getLogger(this::class::java);

    @GetMapping
    fun getAll(
        @RequestParam page: Int,
        @RequestParam pageSize: Int
    ): List<CategoryDTO> {
        val list = categoryService.getAll(PageRequest.of(page, pageSize));
        return list.map { CategoryDTO(it.name) }
    }

    @PostMapping("/create")
    fun create(@RequestBody request: CategoryCreateRequest): ResponseEntity<Unit> {
        val role = authService.getRole()
        if (role != "admin") { return ResponseEntity(HttpStatus.FORBIDDEN) }

        val category = Category(request.name);
        val status = if (categoryService.create(category)) {
            HttpStatus.OK
        } else {
            HttpStatus.CONFLICT
        }
        return ResponseEntity(status)
    }

    @PatchMapping("/update")
    fun update(@RequestBody request: CategoryUpdateRequest): ResponseEntity<Unit> {
        val role = authService.getRole()
        if (role != "admin") { return ResponseEntity(HttpStatus.FORBIDDEN) }

        val new = Category(request.new.name);
        val status = if (categoryService.update(request.oldName, new)) {
            HttpStatus.OK
        } else {
            HttpStatus.CONFLICT
        }
        return ResponseEntity(status)
    }

    @DeleteMapping("/delete/{name}")
    fun delete(@PathVariable name: String): ResponseEntity<Unit> {
        val role = authService.getRole()
        if (role != "admin") { return ResponseEntity(HttpStatus.FORBIDDEN) }

        val status = if (categoryService.delete(name)) {
            HttpStatus.OK
        } else {
            HttpStatus.NOT_FOUND
        }
        return ResponseEntity(status)
    }
}
