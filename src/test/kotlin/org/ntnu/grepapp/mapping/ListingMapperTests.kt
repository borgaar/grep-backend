package org.ntnu.grepapp.mapping

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.ntnu.grepapp.dto.CategoryDTO
import org.ntnu.grepapp.dto.ListingDTO
import org.ntnu.grepapp.dto.LocationDTO
import org.ntnu.grepapp.model.Category
import org.ntnu.grepapp.model.Listing
import org.ntnu.grepapp.model.ListingAuthor
import org.ntnu.grepapp.model.User
import java.time.Instant
import java.time.LocalDateTime
import java.util.UUID

class ListingMappingTest {

    private val testAuthor = User(
        phone = "99999999",
        passwordHash = "hashpass321",
        firstName = "Ola",
        lastName = "Nordmann",
        role = "bruker"
    )

    private val testCategory = Category(name = "Elektronikk")

    private val testListing = Listing(
        id = UUID.randomUUID(),
        title = "Brukt mobiltelefon",
        description = "Lite brukt, god stand",
        lat = 63.4305,
        lon = 10.3951,
        price = 2500,
        timestamp = LocalDateTime.now(),
        updatedAt = LocalDateTime.now(),
        category = testCategory,
        author = ListingAuthor(
            phone = testAuthor.phone,
            firstName = testAuthor.firstName,
            lastName = testAuthor.lastName,
        ),
        isBookmarked = false,
        imageIds = listOf(UUID.randomUUID(), UUID.randomUUID()),
        reservedBy = null,
        soldTo = null
    )

    @Test
    fun `should map Listing to ListingDTO correctly`() {
        val dto = toListingDTO(testListing)

        assertEquals(testListing.id.toString(), dto.id)
        assertEquals(testListing.title, dto.title)
        assertEquals(testListing.description, dto.description)
        assertEquals(LocationDTO(testListing.lat, testListing.lon), dto.location)
        assertEquals(testListing.price, dto.price)
        assertEquals(testListing.timestamp, dto.createdAt)
        assertEquals(testListing.updatedAt, dto.updatedAt)
        assertEquals(CategoryDTO(testCategory.name), dto.category)
        assertEquals(ListingDTO.AuthorDTO(testAuthor.phone, testAuthor.phone, testAuthor.firstName, testAuthor.lastName), dto.author)
        assertFalse(dto.isBookmarked)
        assertEquals(testListing.imageIds.map { it.toString() }, dto.imageIds)
        assertFalse(dto.isReserved)
        assertFalse(dto.isSold)
        assertNull(dto.reservedBy)
        assertNull(dto.soldTo)
    }
}
