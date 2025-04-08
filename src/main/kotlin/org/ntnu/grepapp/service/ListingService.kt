package org.ntnu.grepapp.service

import org.ntnu.grepapp.model.*
import org.ntnu.grepapp.repository.ListingRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class ListingService(
    private val repository: ListingRepository,
) {
    fun find(id: UUID): Listing? {
        return repository.find(id)
    }

    fun getPaginatedAndFiltered(page: Pageable, filter: ListingFilter): List<Listing> {
        return repository.filterPaginate(page, filter)
    }

    fun create(listing: NewListing): Boolean {
        return repository.create(listing)
    }

    fun delete(id: UUID): Boolean {
        return repository.delete(id)
    }

    fun update(id: UUID, new: UpdateListing): Boolean {
        return repository.update(id, new)
    }

    fun getBookmarked(userId: String, pageable: Pageable): List<BookmarkedListing> {
        return repository.getBookmarked(userId, pageable)
    }

    fun createBookmark(listingId: UUID, userId: String): Boolean {
        return repository.createBookmark(listingId, userId);
    }

    fun deleteBookmark(listingId: UUID, userId: String): Boolean {
        return repository.deleteBookmark(listingId, userId);
    }
}
