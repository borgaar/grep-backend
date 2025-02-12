package com.example.springbootoving.controller

import com.example.springbootoving.dto.CalculationRequest
import com.example.springbootoving.dto.CalculationResponse
import com.example.springbootoving.service.CalculateService
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["/api"])
@CrossOrigin(origins = ["http://localhost:5173"])
class CalculatorController(private val calculationService: CalculateService) {

    val logger = LogManager.getLogger(this::class::java);

    @PostMapping("/calculate")
    fun calculate(@RequestBody request: CalculationRequest): ResponseEntity<CalculationResponse> {
        logger.log(Level.INFO, "CalculationRequest: ${request.expression}");
        return ResponseEntity.ok(calculationService.calculate(request));
    }
}