package com.kkoehler.calculator

import org.springframework.stereotype.Service
import java.lang.IllegalStateException
import java.util.*

@Service
class CalculatorService {

    private final fun <T> concatenate(vararg lists: List<T>): List<T> {
        return listOf(*lists).flatten()
    }

    private final val pointOperators = listOf('*', '/')

    private final val dashOperators = listOf('+', '-')

    private final val operators = concatenate(pointOperators, dashOperators)

    private final val others = listOf('(', ')', '.')

    private final val eligibleChars = concatenate(listOf(CharRange('0', '9')).flatten(), operators, others).toTypedArray()


    fun calc(query: String): Float {
        val cleanQuery = query.filter { it != ' ' }
        if (!lintCheck(cleanQuery)) {
            throw IllegalStateException("Malformed query")
        }
        return evaluateTerm(cleanQuery)
    }

    private fun evaluateTerm(term: String): Float {

        val subTerm = findSubBracket(term)

        val left: Float
        val right: Float
        val operator: Char

        if (subTerm == null) {
            val termAsList = splitInclusiveSeparator(term)
            left = termAsList[0].toFloat()
            operator = termAsList[1].single()
            right = termAsList[2].toFloat()

        } else {
            val remainingTerm = term.removePrefix("($subTerm)").removeSuffix("($subTerm)")

            val remainingTermAsArray = splitInclusiveSeparator(remainingTerm)

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
        return when (operator) {
            '+' -> (left + right)
            '-' -> (left - right)
            '*' -> (left * right)
            '/' -> (left / right)
            else -> throw IllegalArgumentException("Unknown operator")
        }
    }

    private fun findSubBracket(term: String): String? {
        val openingBracketIndex = term.indexOf("(")
        val closingBracketIndex = term.lastIndexOf(')')

        return if (openingBracketIndex == -1) {
            null
        } else {
            term.substring(openingBracketIndex + 1, closingBracketIndex)
        }
    }

    private fun lintCheck(query: String): Boolean {
        return if (!query.all { it in eligibleChars }) {
            false
        } else query.count { it == '(' } == query.count { it == ')' }
    }


    fun splitInclusiveSeparator(term: String): List<String> {

        val operator = term.findAnyOf(operators.map { it.toString() })!!.second

        val termAsArray = term.split(operator).toMutableList()
        termAsArray.add(1, operator)
        termAsArray.remove("")
        return termAsArray
    }

}