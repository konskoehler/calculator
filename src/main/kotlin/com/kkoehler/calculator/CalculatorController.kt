package com.kkoehler.calculator

import com.kkoehler.calculator.model.ErrorResponse
import com.kkoehler.calculator.model.Response
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*


@RestController
class CalculatorController(val service: CalculatorService) {

    @GetMapping("/calculus")
    fun calc(@RequestParam query: String): Response {
        return try {
            val origQuery = String(Base64.getDecoder().decode(query))
            service.calc(origQuery)
        } catch (exp: Exception) {
            ErrorResponse(exp.message!!)
        }
    }
}