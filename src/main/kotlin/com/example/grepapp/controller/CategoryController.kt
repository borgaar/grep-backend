package com.example.grepapp.controller

import com.example.grepapp.dto.*
import com.example.grepapp.model.Category
import com.example.grepapp.service.CategoryService
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/category")
@CrossOrigin(origins = ["http://localhost:5173"])
class CategoryController(
    private val categoryService: CategoryService,
) {
    private val logger = LogManager.getLogger(this::class::java);

    @GetMapping()
    fun getAll(
            @RequestParam page: Int,
            @RequestParam pageSize: Int
    ): ResponseEntity<List<CategoryResponse>> {
        val list = categoryService.getAll(page, pageSize);
        return ResponseEntity.ok(list.map { CategoryResponse(it.name) });
    }

    @PostMapping("/create")
    fun create(@RequestBody request: CreateCategoryRequest): ResponseEntity<CategoryResponse> {
        val category = Category(request.name);
        logger.log(Level.INFO, request.name);
        return if (categoryService.create(category)) {
            ResponseEntity.ok(CategoryResponse(category.name));
        } else {
            ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/update")
    fun update(@RequestBody request: UpdateCategoryRequest): ResponseEntity<CategoryResponse> {
        val oldCategory = Category(request.oldName);
        val newCategory = Category(request.newName);
        return if (categoryService.update(oldCategory, newCategory)) {
            ResponseEntity.ok(CategoryResponse(newCategory.name));
        } else {
            ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/delete/{name}")
    fun delete(@PathVariable name: String): ResponseEntity<CategoryResponse> {
        val category = Category(name);
        return if (categoryService.delete(category)) {
            ResponseEntity.ok(CategoryResponse(category.name));
        } else {
            ResponseEntity.internalServerError().build();
        }
    }
}
