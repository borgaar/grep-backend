package org.ntnu.grepapp.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.ntnu.grepapp.dto.BookmarkedListingDTO
import org.ntnu.grepapp.dto.ListingDTO
import org.ntnu.grepapp.dto.listing.*
import org.ntnu.grepapp.mapping.toListingDTO
import org.ntnu.grepapp.model.ListingFilter
import org.ntnu.grepapp.model.NewListing
import org.ntnu.grepapp.model.UpdateListing
import org.ntnu.grepapp.security.JwtUtil
import org.ntnu.grepapp.model.*
import org.ntnu.grepapp.service.AuthService
import org.ntnu.grepapp.service.ListingService
import org.ntnu.grepapp.service.MessageService
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * REST controller for listing-related operations.
 * Provides endpoints for creating, retrieving, updating, and managing listings.
 */
@RestController
@RequestMapping("/api/listing")
@CrossOrigin(origins = ["http://localhost:5173"])
@Tag(name = "Listings", description = "API for managing marketplace listings")
class ListingController(
    val service: ListingService,
    val authService: AuthService,
    private val messageService: MessageService,
) {

    /**
     * Retrieves a paginated list of listings with optional filtering and sorting.
     *
     * @param page The page number to retrieve (zero-based)
     * @param size The number of listings per page
     * @param priceLower Optional minimum price for filtering
     * @param priceUpper Optional maximum price for filtering
     * @param categories Optional list of category names for filtering
     * @param query Optional search query for filtering by title or description
     * @param sort Optional field name to sort by
     * @param sortDirection Optional direction to sort (asc/desc)
     * @return ResponseEntity containing a GetPaginatedResponse with the filtered listings and total count
     */
    @Operation(
        summary = "Get paginated listings",
        description = "Retrieves a paginated list of listings with optional filtering and sorting"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved listings",
            content = [Content(mediaType = "application/json",
                schema = Schema(implementation = GetPaginatedResponse::class))]
        )
    ])
    @GetMapping
    fun getPaginated(
        @Parameter(description = "Page number (zero-based)", example = "0")
        @RequestParam page: Int,

        @Parameter(description = "Number of listings per page", example = "10")
        @RequestParam size: Int,

        @Parameter(description = "Minimum price for filtering (in NOK)", example = "1000")
        @RequestParam priceLower: Int?,

        @Parameter(description = "Maximum price for filtering (in NOK)", example = "10000")
        @RequestParam priceUpper: Int?,

        @Parameter(description = "List of category names to filter by", example = "[\"Electronics\", \"Clothing\"]")
        @RequestParam categories: List<String>?,

        @Parameter(description = "Search query for filtering by title or description", example = "iphone")
        @RequestParam query: String?,

        @Parameter(description = "Field name to sort by", example = "price", schema = Schema(allowableValues = ["price"]))
        @RequestParam sort: String?,

        @Parameter(description = "Sort direction", example = "asc", schema = Schema(allowableValues = ["asc", "desc"]))
        @RequestParam sortDirection: String?,
    ): ResponseEntity<GetPaginatedResponse> {
        val filter = ListingFilter(
            priceLower = priceLower,
            priceUpper = priceUpper,
            categories = categories ?: ArrayList(),
            searchQuery = query,
            sorting = sort,
            sortingDirection = sortDirection,
        )
        val listings = service.getPaginatedAndFiltered(PageRequest.of(page, size), filter, authService.getCurrentUser().id)

        val listingsOut = ArrayList<ListingDTO>()
        for (l in listings.listings) {
            val dto = toListingDTO(l)
            listingsOut.add(dto)
        }

        val response = GetPaginatedResponse(
            listingsOut,
            totalListings = listings.totalListings
        )

        return ResponseEntity.ok(response)
    }

    /**
     * Retrieves a specific listing by its ID.
     *
     * @param id The UUID of the listing to retrieve
     * @return ResponseEntity containing:
     *         - A ListingDTO with the listing details if found
     *         - HTTP 404 NOT_FOUND status if the listing doesn't exist
     */
    @Operation(
        summary = "Get a specific listing",
        description = "Retrieves a specific listing by its ID"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved listing",
            content = [Content(mediaType = "application/json",
                schema = Schema(implementation = ListingDTO::class))]
        ),
        ApiResponse(
            responseCode = "404",
            description = "Listing not found"
        )
    ])
    @GetMapping("/{id}")
    fun get(
        @Parameter(description = "ID of the listing to retrieve", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
        @PathVariable id: UUID
    ): ResponseEntity<ListingDTO> {
        val listing = service.find(id, authService.getCurrentUser().id) ?: return ResponseEntity(HttpStatus.NOT_FOUND)

        val responseListing = toListingDTO(listing)

        return ResponseEntity.ok(responseListing)
    }

    /**
     * Retrieves all listings created by the currently authenticated user.
     *
     * @param page The page number to retrieve (zero-based), defaults to 0
     * @param pageSize The number of listings per page, defaults to 100
     * @return ResponseEntity containing a list of ListingDTOs for the user's listings
     */
    @Operation(
        summary = "Get user's own listings",
        description = "Retrieves all listings created by the currently authenticated user"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved user's listings",
            content = [Content(mediaType = "application/json",
                schema = Schema(implementation = ListingDTO::class, type = "array"))]
        )
    ])
    @GetMapping("/personal")
    fun getOwnListings(
        @Parameter(description = "Page number (zero-based)", example = "0")
        @RequestParam(defaultValue = "0") page: Int,

        @Parameter(description = "Number of listings per page", example = "100")
        @RequestParam(defaultValue = "100") pageSize: Int,
    ): ResponseEntity<List<ListingDTO>> {
        val listings = service.getListingsForUserId(authService.getCurrentUser().id, PageRequest.of(page, pageSize));
        return ResponseEntity.ok(
            listings.map {
                toListingDTO(it, true)
            }
        );
    }

    /**
     * Creates a new listing in the marketplace.
     *
     * @param request A ListingCreateRequest containing the listing details
     * @return ResponseEntity with HTTP status:
     *         - OK if the listing was successfully created
     *         - CONFLICT if there was an issue creating the listing
     */
    @Operation(
        summary = "Create a new listing",
        description = "Creates a new listing in the marketplace"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Listing successfully created"
        ),
        ApiResponse(
            responseCode = "409",
            description = "Conflict: issue creating the listing"
        )
    ])
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody request: ListingCreateRequest): ResponseEntity<ListingCreateResponse> {
        val new = NewListing(
            title = request.title,
            description = request.description,
            price = request.price,
            authorPhone = authService.getCurrentUser().id,
            category = request.category,
            lat = request.location.lat,
            lon = request.location.lon
        )

        val created = service.create(new)

        val status = if (created) {
            HttpStatus.OK
        } else {
            HttpStatus.CONFLICT
        }

        val response = ListingCreateResponse(
            id = new.id.toString(),
        )

        return ResponseEntity(response, status)
    }

    /**
     * Deletes a listing by its ID.
     * Only the author of the listing can delete it.
     *
     * @param id The UUID of the listing to delete
     * @return ResponseEntity with HTTP status:
     *         - OK if the listing was successfully deleted
     *         - NOT_FOUND if the listing doesn't exist or the user is not authorized to delete it
     */
    @Operation(
        summary = "Delete a listing",
        description = "Deletes a listing by its ID. Only the author or an admin can delete it."
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Listing successfully deleted"
        ),
        ApiResponse(
            responseCode = "404",
            description = "Listing not found or user not authorized to delete it"
        )
    ])
    @DeleteMapping("/{id}")
    fun delete(
        @Parameter(description = "ID of the listing to delete", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
        @PathVariable id: UUID
    ): ResponseEntity<Unit> {
        val user = authService.getCurrentUser()
        val deleted = service.delete(id, user)
        val status = if (deleted) {
            HttpStatus.OK
        } else {
            HttpStatus.NOT_FOUND
        }

        return ResponseEntity(status)
    }

    /**
     * Updates an existing listing with new information and manages bookmark status.
     *
     * @param id The UUID of the listing to update
     * @param listing A ListingUpdateRequest containing the updated listing details
     * @return ResponseEntity with HTTP status:
     *         - OK if the listing was successfully updated
     *         - NOT_FOUND if the listing doesn't exist
     */
    @Operation(
        summary = "Update a listing",
        description = "Updates an existing listing with new information and manages bookmark status"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Listing successfully updated"
        ),
        ApiResponse(
            responseCode = "404",
            description = "Listing not found"
        )
    ])
    @PatchMapping("/{id}")
    fun update(
        @Parameter(description = "ID of the listing to update", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
        @PathVariable id: UUID,
        @RequestBody listing: ListingUpdateRequest
    ): ResponseEntity<Unit> {
        val new = UpdateListing(
            title = listing.title,
            description = listing.description,
            price = listing.price,
            category = listing.category,
            lat = listing.location.lat,
            lon = listing.location.lon,
            imageIds = listing.imageIds?.map { UUID.fromString(it) },
        )

        val user = authService.getCurrentUser()
        if (listing.bookmarked) {
            service.createBookmark(id, user.id)
        } else {
            service.deleteBookmark(id, user.id)
        }

        val updated = service.update(id, new)
        val status = if (updated) {
            HttpStatus.OK
        } else {
            HttpStatus.NOT_FOUND
        }

        return ResponseEntity(status)
    }

    /**
     * Marks a listing as reserved for the currently authenticated user.
     *
     * @param id The UUID of the listing to reserve
     * @return ResponseEntity with HTTP status:
     *         - OK if the listing was successfully reserved
     *         - NOT_FOUND if the listing doesn't exist
     *         - CONFLICT if the listing is already reserved
     *         - FORBIDDEN if the user is attempting to reserve their own listing
     */
    @Operation(
        summary = "Reserve a listing",
        description = "Marks a listing as reserved for the currently authenticated user"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Listing successfully reserved"
        ),
        ApiResponse(
            responseCode = "404",
            description = "Listing not found"
        ),
        ApiResponse(
            responseCode = "409",
            description = "Listing is already reserved"
        ),
        ApiResponse(
            responseCode = "403",
            description = "User is attempting to reserve their own listing"
        )
    ])
    @PostMapping("/reserve/{id}")
    fun reserve(
        @Parameter(description = "ID of the listing to reserve", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
        @PathVariable id: UUID
    ): ResponseEntity<Unit> {
        val user = authService.getCurrentUser()
        val listing = service.find(id, user.id) ?: return ResponseEntity(
            HttpStatus.NOT_FOUND
        )

        if (listing.reservedBy != null) {
            return ResponseEntity(HttpStatus.CONFLICT)
        }

        if (listing.author.phone == user.id) {
            return ResponseEntity(HttpStatus.FORBIDDEN)
        }

        service.setReserved(id, user.id)

        messageService.create(CreateChatMessage(
            senderId = user.id,
            recipientId = listing.author.phone,
            content = "", // message generated on frontend based on type
            type = ChatMessageType.RESERVED
        ))

        val status = HttpStatus.OK
        return ResponseEntity(status)
    }

    /**
     * Retrieves all listings bookmarked by the currently authenticated user.
     *
     * @param page The page number to retrieve (zero-based), defaults to 0
     * @param pageSize The number of bookmarks per page, defaults to 100
     * @return A list of BookmarkedListingDTO objects containing bookmark information and listing details
     */
    @Operation(
        summary = "Get bookmarked listings",
        description = "Retrieves all listings bookmarked by the currently authenticated user"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved bookmarked listings",
            content = [Content(mediaType = "application/json",
                schema = Schema(implementation = BookmarkedListingDTO::class, type = "array"))]
        )
    ])
    @GetMapping("/bookmarked")
    fun bookmarked(
        @Parameter(description = "Page number (zero-based)", example = "0")
        @RequestParam("page", defaultValue = "0") page: Int,

        @Parameter(description = "Number of bookmarks per page", example = "100")
        @RequestParam("pageSize", defaultValue = "100") pageSize: Int,
    ): List<BookmarkedListingDTO> {
        return service.getBookmarked(authService.getCurrentUser().id, PageRequest.of(page, pageSize))
            .map {
                BookmarkedListingDTO(
                    bookmarkedAt = it.bookmarkedAt,
                    listing = toListingDTO(it.listing)
                )
            }
    }

    /**
     * Marks a listing as sold to a specific user.
     * Only the author of the listing can mark it as sold.
     *
     * @param id The UUID of the listing to mark as sold
     * @param phone The phone number of the user who purchased the listing
     * @return ResponseEntity with HTTP status:
     *         - OK if the listing was successfully marked as sold
     *         - NOT_FOUND if the listing doesn't exist
     *         - FORBIDDEN if the current user is not the author of the listing
     */
    @Operation(
        summary = "Mark a listing as sold",
        description = "Marks a listing as sold to a specific user. Only the author can mark it as sold."
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Listing successfully marked as sold"
        ),
        ApiResponse(
            responseCode = "404",
            description = "Listing not found"
        ),
        ApiResponse(
            responseCode = "403",
            description = "Current user is not the author of the listing"
        )
    ])
    @PostMapping("/{id}/sell/{phone}")
    fun markAsSold(
        @Parameter(description = "ID of the listing to mark as sold", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
        @PathVariable id: UUID,

        @Parameter(description = "Phone number of the user who purchased the listing", example = "+4712345678")
        @PathVariable phone: String
    ): ResponseEntity<Unit> {
        // Make sure the user is the author
        val user = authService.getCurrentUser()
        val listing = service.find(id, user.id) ?: return ResponseEntity(
            HttpStatus.NOT_FOUND
        )

        if (listing.author.phone != user.id) {
            return ResponseEntity(HttpStatus.FORBIDDEN)
        }

        service.markAsSold(id, phone)

        // Send message to the user that bought the listing
        messageService.create(CreateChatMessage(
            senderId = user.id,
            recipientId = phone,
            content = "", // message generated on frontend based on type
            type = ChatMessageType.MARKED_SOLD
        ))
        return ResponseEntity(HttpStatus.OK)
    }
}