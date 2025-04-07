package com.example.grepapp.controller

import com.example.grepapp.dto.*
import com.example.grepapp.mapping.toListingDTO
import com.example.grepapp.model.NewListing
import com.example.grepapp.model.UpdateListing
import com.example.grepapp.service.AuthService
import com.example.grepapp.service.ListingService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/listing")
@CrossOrigin(origins = ["http://localhost:5173"])
class ListingController(
    val service: ListingService,
    val authService: AuthService,
) {
    @GetMapping
    fun getPaginated(@RequestBody request: ListingGetPaginatedRequest): ResponseEntity<List<ListingDTO>> {
        val listings = service.getPaginated(request.page, request.pageSize)

        val listingsOut = ArrayList<ListingDTO>()
        for (l in listings) {
            val dto = toListingDTO(l)
            listingsOut.add(dto)
        }

        return ResponseEntity(listingsOut, HttpStatus.OK)
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: UUID): ResponseEntity<ListingDTO> {
        val listing = service.find(id) ?: return ResponseEntity(HttpStatus.NOT_FOUND)

        val responseListing = toListingDTO(listing)

        return ResponseEntity(
            responseListing,
            HttpStatus.OK
        )
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody request: ListingCreateRequest): HttpStatus {

        val new = NewListing(
            title = request.title,
            description = request.description,
            price = request.price,
            authorPhone = authService.getCurrentUser(),
            category = request.category,
            lat = request.location.lat,
            lon = request.location.lon
        )

        val created = service.create(
            new
        )

        return if (created) {
            HttpStatus.OK
        } else {
            HttpStatus.CONFLICT
        }
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): HttpStatus {
        val deleted = service.delete(id)
        return if (deleted) {
            HttpStatus.OK
        } else {
            HttpStatus.NOT_FOUND
        }
    }

    @PatchMapping("/{id}")
    fun update(@PathVariable id: UUID, @RequestBody listing: ListingUpdateRequest): HttpStatus {
        val new = UpdateListing(
            title = listing.title,
            description = listing.description,
            price = listing.price,
            category = listing.category,
            lat = listing.location.lat,
            lon = listing.location.lon
        )

        val updated = service.update(id, new)
        return if (updated) {
            HttpStatus.OK
        } else {
            HttpStatus.NOT_FOUND
        }
    }

    @PostMapping("/reserve/{id}")
    fun reserve(@PathVariable id: UUID): HttpStatus {
        // TODO
        return HttpStatus.OK
    }

    @PostMapping("/bookmarked")
    fun bookmarked(): List<BookmarkedListingDTO> {
        // TODO
        val listings = ArrayList<BookmarkedListingDTO>()
        return listings
    }
}