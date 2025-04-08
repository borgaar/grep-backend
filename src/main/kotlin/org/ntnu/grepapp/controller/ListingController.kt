package org.ntnu.grepapp.controller

import org.ntnu.grepapp.dto.BookmarkedListingDTO
import org.ntnu.grepapp.dto.CategoryDTO
import org.ntnu.grepapp.dto.ListingDTO
import org.ntnu.grepapp.dto.LocationDTO
import org.ntnu.grepapp.dto.listing.*
import org.ntnu.grepapp.mapping.toListingDTO
import org.ntnu.grepapp.model.ListingFilter
import org.ntnu.grepapp.model.NewListing
import org.ntnu.grepapp.model.UpdateListing
import org.ntnu.grepapp.repository.ListingRepository
import org.ntnu.grepapp.service.AuthService
import org.ntnu.grepapp.service.ListingService
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import kotlin.collections.ArrayList

@RestController
@RequestMapping("/api/listing")
@CrossOrigin(origins = ["http://localhost:5173"])
class ListingController(
    val service: ListingService,
    val authService: AuthService,
) {
    @GetMapping
    fun getPaginated(
        @RequestParam page: Int,
        @RequestParam size: Int,
        @RequestParam priceLower: Int?,
        @RequestParam priceUpper: Int?,
        @RequestParam categories: List<String>?,
        @RequestParam query: String?,
        @RequestParam sort: String?,
        @RequestParam sortDirection: String?,
    ): ResponseEntity<List<ListingDTO>> {
        val filter = ListingFilter(
            priceLower = priceLower,
            priceUpper = priceUpper,
            categories = categories ?: ArrayList(),
            titleQuery = query,
            sorting = sort,
            sortingDirection = sortDirection,
        )
        val listings = service.getPaginatedAndFiltered(PageRequest.of(page, size), filter)

        val listingsOut = ArrayList<ListingDTO>()
        for (l in listings) {
            val dto = toListingDTO(l)
            listingsOut.add(dto)
        }

        return ResponseEntity.ok(listingsOut)
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: UUID): ResponseEntity<ListingDTO> {
        val listing = service.find(id) ?: return ResponseEntity(HttpStatus.NOT_FOUND)

        val responseListing = toListingDTO(listing)

        return ResponseEntity.ok(responseListing)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody request: CreateRequest): ResponseEntity<Unit> {

        val new = NewListing(
            title = request.title,
            description = request.description,
            price = request.price,
            authorPhone = authService.getCurrentUser(),
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

        return ResponseEntity(status)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Unit> {
        val deleted = service.delete(id)
        val status = if (deleted) {
            HttpStatus.OK
        } else {
            HttpStatus.NOT_FOUND
        }

        return ResponseEntity(status)
    }

    @PatchMapping("/{id}")
    fun update(@PathVariable id: UUID, @RequestBody listing: UpdateRequest): ResponseEntity<Unit> {
        val new = UpdateListing(
            title = listing.title,
            description = listing.description,
            price = listing.price,
            category = listing.category,
            lat = listing.location.lat,
            lon = listing.location.lon
        )

        if (listing.bookmarked) {
            service.createBookmark(id, authService.getCurrentUser())
        } else {
            service.deleteBookmark(id, authService.getCurrentUser())
        }

        val updated = service.update(id, new)
        val status = if (updated) {
            HttpStatus.OK
        } else {
            HttpStatus.NOT_FOUND
        }

        return ResponseEntity(status)
    }

    @PostMapping("/reserve/{id}")
    fun reserve(@PathVariable id: UUID): ResponseEntity<Unit> {
        // TODO
        val status = HttpStatus.OK
        return ResponseEntity(status)
    }

    @GetMapping("/bookmarked")
    fun bookmarked(
        @RequestParam("page", defaultValue = "0") page: Int,
        @RequestParam("pageSize", defaultValue = "5") pageSize: Int,
    ): List<BookmarkedListingDTO> {
        return service.getBookmarked(authService.getCurrentUser(), PageRequest.of(page, pageSize))
            .map {
                BookmarkedListingDTO(
                    bookmarkedAt = it.bookmarkedAt,
                    listing = toListingDTO(it.listing)
                )
            }
    }
}