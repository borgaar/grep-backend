package org.ntnu.grepapp.model

import java.util.*

data class ImageFile(
    val id: UUID,
    val buffer: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageFile

        if (id != other.id) return false
        if (!buffer.contentEquals(other.buffer)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + buffer.contentHashCode()
        return result
    }
}
