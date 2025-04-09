package org.ntnu.grepapp.model

import java.util.UUID

data class Bookmark(
    val userId: String,
    val listingId: UUID,
)