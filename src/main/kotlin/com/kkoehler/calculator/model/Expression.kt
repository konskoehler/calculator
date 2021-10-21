package com.kkoehler.calculator.model

class Expression (val left: Expression, val operator: Operator, val right: Expression){
}

enum class Operator(val symbol: Char) {
    ADD('+'),
    SUBSTRACT('-'),
    MULTIPLY('*'),
    DIVIDE('/')
}