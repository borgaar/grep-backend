package com.example.grepapp.controller

import com.example.grepapp.dto.GetListingResponse
import com.example.grepapp.service.ListingService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/listing")
@CrossOrigin(origins = ["http://localhost:5173"])
class ListingController(
    val service: ListingService,
) {
    @GetMapping("/{id}")
    fun get(@PathVariable id: UUID): ResponseEntity<GetListingResponse> {
        val listing = service.find(id)
            ?: return ResponseEntity(HttpStatus.NOT_FOUND)

        return ResponseEntity(GetListingResponse(
            id = listing.id.toString(),
            title = listing.title,
            description = listing.description,
            location = GetListingResponse.LocationDTO(
                listing.lat,
                listing.lon
            ),
            price = listing.price,
            category = GetListingResponse.CategoryDTO(
                id = listing.category.name,
                name = listing.category.name,
            ),
            author = GetListingResponse.AuthorDTO(
                id = listing.author.phone,
                phone = listing.author.phone,
                first_name = listing.author.first_name,
                last_name = listing.author.last_name,
            )
        ), HttpStatus.OK)
    }
}