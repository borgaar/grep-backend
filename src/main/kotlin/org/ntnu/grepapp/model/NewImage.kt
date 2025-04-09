package org.ntnu.grepapp.model

import java.util.*

data class NewImage(
    val buffer: String,
) {
    val id: UUID = UUID.randomUUID()
}
