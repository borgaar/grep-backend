package org.ntnu.grepapp.model

import java.util.UUID

data class Image(
    val id: UUID,
    val buffer: String,
)