package com.kkoehler.calculator

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class CalculatorTest {

    val service = CalculatorService()

    @Test
    fun `calc basic query`() {
        val result = service.calc("4+2")
        assertEquals(6F, result)
    }

    @Test
    fun `calc advanced query`() {
        val result = service.calc("((4+(2*3))*3)/(4-2)")
        assertEquals(15F, result)
    }

    @Test
    fun `query with more than one operator per subterm`() {
        assertThrows(IllegalArgumentException::class.java) {
            service.calc("4*(10+2+4)")
        }
    }

    @Test
    fun `division by zero`() {
        assertThrows(IllegalArgumentException::class.java) {
            service.calc("10/0")
        }
    }

    @Test
    fun `query with letters`() {
        assertThrows(IllegalArgumentException::class.java) {
            service.calc("10+five")
        }
    }

    @Test
    fun `malformed query`() {
        assertThrows(IllegalArgumentException::class.java) {
            service.calc("7+(12-4")
        }
    }
}
