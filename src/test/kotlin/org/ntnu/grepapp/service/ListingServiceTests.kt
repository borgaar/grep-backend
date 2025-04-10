package org.ntnu.grepapp.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever
import org.ntnu.grepapp.model.*
import org.ntnu.grepapp.repository.ListingRepository
import org.ntnu.grepapp.security.UserClaims
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

class ListingServiceTest {
    private lateinit var listingService: ListingService
    private lateinit var mockRepository: ListingRepository

    private val testId = UUID.randomUUID()
    private val testUserId = "12345678"
    private val testUser = UserClaims(testUserId, "user")
    private val testAdminUser = UserClaims("98765432", "admin")
    private val testPageable: Pageable = PageRequest.of(0, 10)

    private val testListing = Listing(
        id = testId,
        title = "Pent brukt sofa",
        description = "Ikea-sofa i god stand, 3 år gammel",
        price = 15000,
        category = Category("Møbler"),
        timestamp = LocalDateTime.now(),
        updatedAt = LocalDateTime.now(),
        lat = 50.00,
        lon = 50.00,
        imageIds = listOf(UUID.randomUUID()),
        author = ListingAuthor(
            phone = testUserId,
            firstName = "Ola",
            lastName = "Nordmann",
        ),
        isBookmarked = false,
        reservedBy = null,
        soldTo = null
    )

    private val testNewListing = NewListing(
        title = "Brukt sykkel",
        description = "Fin sykkel selges",
        price = 2500,
        category = "Sport og fritid",
        authorPhone = testUserId,
        lat = 70.00,
        lon = 50.00,
    )

    private val testUpdateListing = UpdateListing(
        title = "Pent brukt sofa - PRISREDUKSJON",
        description = "Ikea-sofa i god stand, selges billig",
        price = 200,
        category = "Møbler",
        lat = 70.00,
        lon = 50.00,
        imageIds = emptyList()
    )

    private val testListingFilter = ListingFilter(
        searchQuery = "sofa",
        categories = listOf("Møbler"),
        priceLower = 1000,
        priceUpper = 2000,
        sorting = "price",
        sortingDirection = "desc",
    )

    private val testBookmarkedListing = BookmarkedListing(
        listing = testListing,
        bookmarkedAt = LocalDateTime.now(),
    )

    @BeforeEach
    fun setup() {
        mockRepository = mock(ListingRepository::class.java)
        listingService = ListingService(mockRepository)
    }

    @Test
    fun `should find listing by id and user id`() {
        whenever(mockRepository.find(testId, testUserId)).thenReturn(testListing)

        val result = listingService.find(testId, testUserId)

        assertNotNull(result)
        assertEquals(testListing, result)
        verify(mockRepository).find(testId, testUserId)
    }

    @Test
    fun `should get paginated and filtered listings`() {
        val expectedResult = PaginatedListings(
            listings = listOf(testListing),
            totalListings = 1
        )
        whenever(mockRepository.filterPaginate(testPageable, testListingFilter, testUserId)).thenReturn(expectedResult)

        val result = listingService.getPaginatedAndFiltered(testPageable, testListingFilter, testUserId)

        assertEquals(expectedResult.listings.size, result.listings.size)
        assertEquals(expectedResult.totalListings, result.totalListings)
        verify(mockRepository).filterPaginate(testPageable, testListingFilter, testUserId)
    }

    @Test
    fun `should create new listing`() {
        whenever(mockRepository.create(testNewListing)).thenReturn(true)

        val result = listingService.create(testNewListing)

        assertTrue(result)
        verify(mockRepository).create(testNewListing)
    }

    @Test
    fun `should delete listing when user is owner`() {
        whenever(mockRepository.delete(testId, testUserId, false)).thenReturn(true)

        val result = listingService.delete(testId, testUser)

        assertTrue(result)
        verify(mockRepository).delete(testId, testUserId, false)
    }

    @Test
    fun `should delete any listing when user is admin`() {
        whenever(mockRepository.delete(testId, testAdminUser.id, true)).thenReturn(true)

        val result = listingService.delete(testId, testAdminUser)

        assertTrue(result)
        verify(mockRepository).delete(testId, testAdminUser.id, true)
    }

    @Test
    fun `should update listing`() {
        whenever(mockRepository.update(testId, testUpdateListing)).thenReturn(true)

        val result = listingService.update(testId, testUpdateListing)

        assertTrue(result)
        verify(mockRepository).update(testId, testUpdateListing)
    }

    @Test
    fun `should get listings for user id`() {
        val expectedListings = listOf(testListing)
        whenever(mockRepository.getListingsForUserId(testUserId, testPageable)).thenReturn(expectedListings)

        val result = listingService.getListingsForUserId(testUserId, testPageable)

        assertEquals(expectedListings.size, result.size)
        assertEquals(expectedListings[0], result[0])
        verify(mockRepository).getListingsForUserId(testUserId, testPageable)
    }

    @Test
    fun `should get bookmarked listings`() {
        val expectedBookmarks = listOf(testBookmarkedListing)
        whenever(mockRepository.getBookmarked(testUserId, testPageable)).thenReturn(expectedBookmarks)

        val result = listingService.getBookmarked(testUserId, testPageable)

        assertEquals(expectedBookmarks.size, result.size)
        assertEquals(expectedBookmarks[0], result[0])
        verify(mockRepository).getBookmarked(testUserId, testPageable)
    }

    @Test
    fun `should create bookmark`() {
        whenever(mockRepository.createBookmark(testId, testUserId)).thenReturn(true)

        val result = listingService.createBookmark(testId, testUserId)

        assertTrue(result)
        verify(mockRepository).createBookmark(testId, testUserId)
    }

    @Test
    fun `should delete bookmark`() {
        whenever(mockRepository.deleteBookmark(testId, testUserId)).thenReturn(true)

        val result = listingService.deleteBookmark(testId, testUserId)

        assertTrue(result)
        verify(mockRepository).deleteBookmark(testId, testUserId)
    }

    @Test
    fun `should set listing as reserved`() {
        whenever(mockRepository.setReserved(testId, testUserId)).thenReturn(true)

        val result = listingService.setReserved(testId, testUserId)

        assertTrue(result)
        verify(mockRepository).setReserved(testId, testUserId)
    }

    @Test
    fun `should mark listing as sold`() {
        whenever(mockRepository.markAsSold(testId, testUserId)).thenReturn(true)

        val result = listingService.markAsSold(testId, testUserId)

        assertTrue(result)
        verify(mockRepository).markAsSold(testId, testUserId)
    }
}