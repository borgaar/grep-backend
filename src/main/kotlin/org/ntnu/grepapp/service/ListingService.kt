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
    fun find(id: UUID, userId: String): Listing? {
        return repository.find(id, userId)
    }

    fun getPaginatedAndFiltered(page: Pageable, filter: ListingFilter, userId: String): List<Listing> {
        return repository.filterPaginate(page, filter, userId)
    }

    fun create(listing: NewListing): Boolean {
        return repository.create(listing)
    }

    fun delete(id: UUID, userId: String, userRole: String): Boolean {
        return repository.delete(id, userId, userRole)
    }

    fun update(id: UUID, new: UpdateListing): Boolean {
        return repository.update(id, new)
    }

    fun getListingsForUserId(userId: String, page: Pageable): List<Listing> {
        return repository.getListingsForUserId(userId, page);
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

    fun setReserved(listingId: UUID, reservedUserID: String?) :Boolean {
        return repository.setReserved(listingId, reservedUserID);
    }

    fun markAsSold(listingId: UUID, soldUserID: String?) :Boolean {
        return repository.markAsSold(listingId, soldUserID);
    }
}
