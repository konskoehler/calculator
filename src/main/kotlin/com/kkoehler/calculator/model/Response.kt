package com.kkoehler.calculator.model

sealed class Response(val error: Boolean)

class ResultResponse(val result: Float) : Response(false)
class ErrorResponse(val message: String) : Response(true)

