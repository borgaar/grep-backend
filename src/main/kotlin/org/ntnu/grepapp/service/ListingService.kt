package org.ntnu.grepapp.service

import org.ntnu.grepapp.model.*
import org.ntnu.grepapp.repository.ListingRepository
import org.ntnu.grepapp.security.UserClaims
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

/**
 * Service class that manages marketplace listings.
 * Provides methods for creating, retrieving, updating, and managing listings and bookmarks.
 */
@Service
class ListingService(
    private val repository: ListingRepository,
) {

    /**
     * Finds a specific listing by its ID.
     *
     * @param id The UUID of the listing to find
     * @param userId The ID of the user making the request (for bookmark status)
     * @return The Listing object if found, null otherwise
     */
    fun find(id: UUID, userId: String): Listing? {
        return repository.find(id, userId)
    }

    /**
     * Retrieves a paginated and filtered list of listings.
     *
     * @param page Pagination information including page number and size
     * @param filter Filter criteria including price range, categories, search query, and sorting
     * @param userId The ID of the user making the request (for bookmark status)
     * @return A PaginatedListings object containing the listings and total count
     */
    fun getPaginatedAndFiltered(page: Pageable, filter: ListingFilter, userId: String): PaginatedListings {
        return repository.filterPaginate(page, filter, userId)
    }

    /**
     * Creates a new listing in the marketplace.
     *
     * @param listing The NewListing object containing listing details
     * @return true if the listing was successfully created, false otherwise
     */
    fun create(listing: NewListing): Boolean {
        return repository.create(listing)
    }

    /**
     * Deletes a listing by its ID.
     * Only the author of the listing or an admin can delete it.
     *
     * @param id The UUID of the listing to delete
     * @param user The UserClaims of the user attempting to delete the listing
     * @return true if the listing was successfully deleted, false otherwise
     */
    fun delete(id: UUID, user: UserClaims): Boolean {
        return repository.delete(id, user.id, user.isAdmin())
    }

    /**
     * Updates an existing listing with new information.
     *
     * @param id The UUID of the listing to update
     * @param new The UpdateListing object containing the updated listing details
     * @return true if the listing was successfully updated, false otherwise
     */
    fun update(id: UUID, new: UpdateListing): Boolean {
        return repository.update(id, new)
    }

    /**
     * Retrieves all listings created by a specific user.
     *
     * @param userId The ID of the user whose listings to retrieve
     * @param page Pagination information including page number and size
     * @return A list of Listing objects created by the specified user
     */
    fun getListingsForUserId(userId: String, page: Pageable): List<Listing> {
        return repository.getListingsForUserId(userId, page);
    }

    /**
     * Retrieves all listings bookmarked by a specific user.
     *
     * @param userId The ID of the user whose bookmarks to retrieve
     * @param pageable Pagination information including page number and size
     * @return A list of BookmarkedListing objects for the specified user
     */
    fun getBookmarked(userId: String, pageable: Pageable): List<BookmarkedListing> {
        return repository.getBookmarked(userId, pageable)
    }

    /**
     * Creates a bookmark for a listing.
     *
     * @param listingId The UUID of the listing to bookmark
     * @param userId The ID of the user creating the bookmark
     * @return true if the bookmark was successfully created, false otherwise
     */
    fun createBookmark(listingId: UUID, userId: String): Boolean {
        return repository.createBookmark(listingId, userId);
    }

    /**
     * Deletes a bookmark for a listing.
     *
     * @param listingId The UUID of the listing to remove from bookmarks
     * @param userId The ID of the user removing the bookmark
     * @return true if the bookmark was successfully deleted, false otherwise
     */
    fun deleteBookmark(listingId: UUID, userId: String): Boolean {
        return repository.deleteBookmark(listingId, userId);
    }

    /**
     * Marks a listing as reserved for a specific user.
     *
     * @param listingId The UUID of the listing to mark as reserved
     * @param reservedUserID The ID of the user reserving the listing, or null to remove reservation
     * @return true if the reservation status was successfully updated, false otherwise
     */
    fun setReserved(listingId: UUID, reservedUserID: String?) :Boolean {
        return repository.setReserved(listingId, reservedUserID);
    }

    /**
     * Marks a listing as sold to a specific user.
     *
     * @param listingId The UUID of the listing to mark as sold
     * @param soldUserID The ID of the user who purchased the listing, or null to remove sold status
     * @return true if the sold status was successfully updated, false otherwise
     */
    fun markAsSold(listingId: UUID, soldUserID: String?) :Boolean {
        return repository.markAsSold(listingId, soldUserID);
    }
}
