package com.example.grepapp.service

import com.example.grepapp.dto.CalculationRequest
import com.example.grepapp.dto.CalculationResponse
import com.example.grepapp.model.Calculation
import com.example.grepapp.model.User
import com.example.grepapp.repository.CalculationRepository
import com.example.grepapp.repository.UserRepository
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestBody

@Service
class CalculateService(private val calculationRepository: CalculationRepository, private val userRepository: UserRepository) {
    fun calculate(@RequestBody request: CalculationRequest): CalculationResponse {
        try {
            val expression: Expression = ExpressionBuilder(request.expression).build();
            return CalculationResponse(expression.evaluate().toString());
        } catch (e: Exception) {
            return CalculationResponse("Math Error");
        }
    }

    fun save(request: CalculationRequest, response: CalculationResponse): Calculation {
        val user = getUser();
        val calculation = Calculation(user = user, expression = request.expression, result = response.result);
        calculationRepository.save(calculation);
        return calculation;
    }

    fun getHistory(pageable: Pageable): Page<Calculation> {
        val user = getUser();
        return calculationRepository.findByUser(user, PageRequest.of(pageable.pageNumber, pageable.pageSize));
    }

    fun getUser(): User {
        val authentication = SecurityContextHolder.getContext().authentication;
        val username = authentication?.name ?: throw Exception("User not found");
        val user = userRepository.findByUsername(username).get();
        return user;
    }
}