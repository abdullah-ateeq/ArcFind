package com.example.arcfind.math

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

object MathConstants {
    // High-precision PI with at least 50 digits (100 decimal places provided)
    val PI = BigDecimal(
        "3.1415926535897932384626433832795028841971693993751058209749445923078164062862089986280348253421170679"
    )

    val MATH_CONTEXT = MathContext(50, RoundingMode.HALF_UP)

    val BD_0: BigDecimal = BigDecimal.ZERO
    val BD_1: BigDecimal = BigDecimal.ONE
    val BD_2: BigDecimal = BigDecimal("2")
    val BD_100: BigDecimal = BigDecimal("100")
    val BD_180: BigDecimal = BigDecimal("180")
    val BD_360: BigDecimal = BigDecimal("360")
}
