package com.example.grepapp.service

import com.example.grepapp.model.Listing
import com.example.grepapp.repository.ListingRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ListingService(
    private val repository: ListingRepository
) {
    fun find(id: UUID): Listing? {
        return repository.find(id)
    }
}
