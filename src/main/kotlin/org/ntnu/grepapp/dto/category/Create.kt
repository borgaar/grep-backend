package org.ntnu.grepapp.dto.category

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Request for creating a new category")
data class CategoryCreateRequest (
    @Schema(description = "Name of the category", example = "Electronics", required = true)
    val name: String,
)