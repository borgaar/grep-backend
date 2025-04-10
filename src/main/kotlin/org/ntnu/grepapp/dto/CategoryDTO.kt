package org.ntnu.grepapp.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Data transfer object for category information")
data class CategoryDTO(
    @Schema(description = "Name of the category", example = "Electronics", required = true)
    val name: String,
)