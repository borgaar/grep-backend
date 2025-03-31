package com.example.springbootoving.model

import jakarta.persistence.*

@Entity
@Table(name = "calculations")
data class Calculation (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0,
    val expression: String,
    val result: String,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // Foreign key
    val user: User
)