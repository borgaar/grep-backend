package com.example.grepapp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GrepApplication

fun main(args: Array<String>) {
    runApplication<GrepApplication>(*args)
}
