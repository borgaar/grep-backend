package com.example.springbootoving.service

import com.example.springbootoving.dto.CalculationRequest
import com.example.springbootoving.dto.CalculationResponse
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestBody

@Service
class CalculateService {
    fun calculate(@RequestBody request: CalculationRequest): CalculationResponse {
        try {
            val expression: Expression = ExpressionBuilder(request.expression).build();
            return CalculationResponse(expression.evaluate().toString());
        } catch (e: Exception) {
            return CalculationResponse("Math Error");
        }
    }
}