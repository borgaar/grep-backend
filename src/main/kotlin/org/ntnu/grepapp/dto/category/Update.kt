package org.ntnu.grepapp.dto.category

import org.ntnu.grepapp.dto.CategoryDTO

data class CategoryUpdateRequest(
    val oldName: String,
    val new: CategoryDTO,
)