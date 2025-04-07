package com.example.grepapp.controller

import com.example.grepapp.dto.*
import com.example.grepapp.model.Category
import com.example.grepapp.service.CategoryService
import org.apache.logging.log4j.LogManager
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/category")
@CrossOrigin(origins = ["http://localhost:5173"])
class CategoryController(
    private val categoryService: CategoryService,
) {
    private val logger = LogManager.getLogger(this::class::java);

    @GetMapping("/get")
    fun getAll(@RequestBody request: PaginationDetail): List<CategoryResponse> {
        val list = categoryService.getAll(request.page, request.pageSize);
        return list.map { CategoryResponse(it.name) }
    }

    @PostMapping("/create")
    fun create(@RequestBody request: CreateCategoryRequest): ResponseEntity<Unit> {
        val category = Category(request.name);
        val status = if (categoryService.create(category)) {
            HttpStatus.OK
        } else {
            HttpStatus.CONFLICT
        }
        return ResponseEntity(status)
    }

    @PatchMapping("/update")
    fun update(@RequestBody request: UpdateCategoryRequest): ResponseEntity<Unit> {
        val new = Category(request.newName);
        val status = if (categoryService.update(request.oldName, new)) {
            HttpStatus.OK
        } else {
            HttpStatus.CONFLICT
        }
        return ResponseEntity(status)
    }

    @DeleteMapping("/delete/{name}")
    fun delete(@PathVariable name: String): ResponseEntity<Unit> {
        val status = if (categoryService.delete(name)) {
            HttpStatus.OK
        } else {
            HttpStatus.NOT_FOUND
        }
        return ResponseEntity(status)
    }
}
