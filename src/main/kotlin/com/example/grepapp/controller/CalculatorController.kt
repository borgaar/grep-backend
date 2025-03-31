package com.example.springbootoving.controller

import com.example.springbootoving.dto.CalculationRequest
import com.example.springbootoving.dto.CalculationResponse
import com.example.springbootoving.model.Calculation
import com.example.springbootoving.service.CalculateService
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
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

    @PostMapping("/calculate/save")
    fun saveCalculation(@RequestBody request: CalculationRequest): ResponseEntity<Calculation> {
        logger.log(Level.INFO, "Save with CalculationRequest: ${request.expression}");
        val result = calculationService.calculate(request);
        return ResponseEntity.ok(calculationService.save(request, result));
    }

    @GetMapping("/calculate/history")
    fun getCalculations(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "5") size: Int
    ): ResponseEntity<Page<Calculation>> {
        logger.log(Level.INFO, "Getting all calculations");
        return ResponseEntity.ok(calculationService.getHistory(PageRequest.of(page, size)));
    }
}