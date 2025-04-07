package org.ntnu.grepapp.dto.category

import org.ntnu.grepapp.dto.CategoryDTO

data class UpdateRequest(
    val oldName: String,
    val new: CategoryDTO,
)