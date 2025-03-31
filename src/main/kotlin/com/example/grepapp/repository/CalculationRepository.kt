package com.example.grepapp.repository

import com.example.grepapp.model.Calculation
import com.example.grepapp.model.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CalculationRepository : JpaRepository<Calculation, Long> {
    fun findByUser(user: User, pageable: Pageable): Page<Calculation>
}
