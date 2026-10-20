package com.example.arcfind.math

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

object BigDecimalMath {

    fun degreesToRadians(
        degrees: BigDecimal,
        mathContext: MathContext = MathConstants.MATH_CONTEXT
    ): BigDecimal {
        // radians = degrees * PI / 180
        return degrees.multiply(MathConstants.PI, mathContext)
            .divide(MathConstants.BD_180, mathContext)
    }

    fun radiansToDegrees(
        radians: BigDecimal,
        mathContext: MathContext = MathConstants.MATH_CONTEXT
    ): BigDecimal {
        // degrees = radians * 180 / PI
        return radians.multiply(MathConstants.BD_180, mathContext)
            .divide(MathConstants.PI, mathContext)
    }

    /**
     * High-precision square root using Newton-Raphson approximation.
     */
    fun sqrt(
        value: BigDecimal,
        mathContext: MathContext = MathConstants.MATH_CONTEXT
    ): BigDecimal {
        val signum = value.signum()
        if (signum < 0) {
            throw IllegalArgumentException("Cannot compute square root of negative number: $value")
        }
        if (signum == 0) {
            return BigDecimal.ZERO
        }

        val two = MathConstants.BD_2
        // Initial estimate using scale
        var x = BigDecimal(Math.sqrt(value.toDouble()).takeIf { !it.isInfinite() && !it.isNaN() } ?: 1.0, mathContext)
        if (x <= BigDecimal.ZERO) {
            x = BigDecimal.ONE
        }

        val precision = mathContext.precision
        val tolerance = BigDecimal.ONE.movePointLeft(precision + 2)
        val maxIterations = 100

        for (i in 0 until maxIterations) {
            // x_next = (x + value / x) / 2
            val division = value.divide(x, mathContext)
            val nextX = x.add(division, mathContext).divide(two, mathContext)
            val diff = nextX.subtract(x, mathContext).abs()
            x = nextX
            if (diff.compareTo(tolerance) <= 0) {
                break
            }
        }
        return x.round(mathContext)
    }

    fun safeDivide(
        numerator: BigDecimal,
        denominator: BigDecimal,
        mathContext: MathContext = MathConstants.MATH_CONTEXT
    ): BigDecimal {
        if (denominator.compareTo(BigDecimal.ZERO) == 0) {
            throw ArithmeticException("Division by zero")
        }
        return numerator.divide(denominator, mathContext)
    }

    fun safeMultiply(
        first: BigDecimal,
        second: BigDecimal,
        mathContext: MathContext = MathConstants.MATH_CONTEXT
    ): BigDecimal {
        return first.multiply(second, mathContext)
    }
}
