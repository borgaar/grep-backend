package com.example.springbootoving.repository

import com.example.springbootoving.model.Calculation
import com.example.springbootoving.model.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CalculationRepository : JpaRepository<Calculation, Long> {
    fun findByUser(user: User, pageable: Pageable): Page<Calculation>
}
