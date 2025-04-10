package org.ntnu.grepapp.dto.category

import io.swagger.v3.oas.annotations.media.Schema
import org.ntnu.grepapp.dto.CategoryDTO

@Schema(description = "Request for updating an existing category")
data class CategoryUpdateRequest(
    @Schema(description = "Current name of the category to update", example = "Electronics", required = true)
    val oldName: String,

    @Schema(description = "New category details", required = true)
    val new: CategoryDTO,
)