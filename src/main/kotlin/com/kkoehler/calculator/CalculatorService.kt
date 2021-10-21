package com.kkoehler.calculator

import com.kkoehler.calculator.model.Response
import com.kkoehler.calculator.model.ResultResponse
import org.springframework.stereotype.Service

@Service
class CalculatorService {

    private final fun <T> concatenate(vararg lists: List<T>): List<T> {
        return listOf(*lists).flatten()
    }

    private final val pointOperators = listOf('*', '/')

    private final val dashOperators = listOf('+', '-')

    private final val operators = concatenate(pointOperators, dashOperators)

    private final val others = listOf('.', '(', ')')

    private final val eligibleChars = concatenate(listOf(CharRange('0', '9')).flatten(), operators, others).toTypedArray()


    fun calc(query: String): Response {
        val cleanQuery = query.filter { it != ' ' }
        basicLintCheck(cleanQuery)
        val numericResult = evaluateTerm(cleanQuery)
        return ResultResponse(numericResult)
    }

    private fun evaluateTerm(term: String): Float {

        val subTerms = findSubTerms(term)

        lintCheckTerm(term, subTerms)

        val left: Float
        val right: Float
        val operator: Char

        if (subTerms.isEmpty()) {  // root of recursive tree
            val termAsList = splitInclusiveDeliminator(term)
            left = termAsList[0].toFloat()
            operator = termAsList[1].single()
            right = termAsList[2].toFloat()
        } else {
            when (subTerms.size) {
                2 -> {
                    left = evaluateTerm(subTerms[0])
                    operator = term.replace("(${subTerms[0]})", "").replace("(${subTerms[1]})", "").single()
                    right = evaluateTerm(subTerms[1])
                }
                1 -> {
                    val subTerm = subTerms[0]
                    val remainingTerm = term.replace("($subTerm)", "").replace("($subTerm)", "")

                    val remainingTermAsArray = splitInclusiveDeliminator(remainingTerm)

                    if (remainingTerm.last() in operators) {
                        left = remainingTermAsArray[0].toFloat()
                        right = evaluateTerm(subTerm)
                        operator = remainingTermAsArray[1].single()
                    } else {
                        left = evaluateTerm(subTerm)
                        right = remainingTermAsArray[1].toFloat()
                        operator = remainingTermAsArray[0].single()
                    }
                }
                else -> throw IllegalArgumentException("API can only handle one operation (from $operators) per bracket.")
            }

        }
        return when (operator) {
            '+' -> (left + right)
            '-' -> (left - right)
            '*' -> (left * right)
            '/' -> (left / right)
            else -> throw IllegalArgumentException("Unknown operator")
        }
    }

    // Finds subTerms (up to 2) from term. E.g.:
    // term = 4+((6-3)*(1+3))
    // findSubTerms(term) = [(6-3)*(1+3)]

    //term = (6-3)*(1+3)
    //findSubTerms(term) = [6-3, 1+3]
    private fun findSubTerms(term: String): List<String> {

        var inTerm = false
        var openingSubTermIndex = -1
        var closingSubTermIndex = -1

        val subTerms = mutableListOf<String>()

        var bracketCount = 0
        for ((i, v) in term.withIndex()) {
            if (v == '(') {
                bracketCount++
                if (!inTerm) {
                    inTerm = true
                    openingSubTermIndex = i
                }
            }
            if (v == ')') {
                bracketCount--
                if (bracketCount == 0) {
                    inTerm = false
                    closingSubTermIndex = i
                    subTerms.add(term.substring(openingSubTermIndex + 1, closingSubTermIndex))
                }
            }
        }
        return subTerms
    }

    private fun basicLintCheck(query: String) {
        if (!query.all { it in eligibleChars }) {
            throw IllegalArgumentException("Queries must not contain other literals than ${eligibleChars.joinToString("")}")
        } else if (query.count { it == '(' } != query.count { it == ')' }) {
            throw IllegalArgumentException("Queries must contain equal amounts of '(' and ')'")
        }
        if (query.contains("/0")) { // Todo: Problematic. E.g. refuses to accept 16/02. To be improved.
            throw IllegalArgumentException("Cannot divide by 0.")
        }
    }

    private fun lintCheckTerm(term: String, subTerms: List<String>) {
        var termWithoutSubTerms = term
        for (subTerm in subTerms) {
            termWithoutSubTerms = termWithoutSubTerms.replace("($subTerm)", "")
        }
        if (termWithoutSubTerms.count { it in operators } > 1) {
            throw IllegalArgumentException("API can only handle one operation (from $operators) per bracket.")
        }
    }

    // Splits string by operator while keeping the operator/deliminator in result array.
    fun splitInclusiveDeliminator(term: String): List<String> {
        val operator = term.findAnyOf(operators.map { it.toString() })!!.second

        val termAsArray = term.split(operator).toMutableList()
        termAsArray.add(1, operator)
        termAsArray.remove("")
        return termAsArray
    }
}
