package com.kkoehler.calculator

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.*


@Controller
class CalculatorController(val service: CalculatorService) {



    @GetMapping("/calculus")
    fun calc(@RequestParam query: String): Float {
        val origQuery = String(Base64.getDecoder().decode(query))
        val res = service.calc(origQuery)
        return res
    }
}