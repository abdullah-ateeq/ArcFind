package com.example.arcfind.domain

import java.math.BigDecimal

object ArcFindParser {

    private const val MAX_INPUT_LENGTH = 500

    fun parseNumber(input: String): Result<BigDecimal> {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return Result.failure(IllegalArgumentException("Input cannot be empty."))
        }
        if (trimmed.length > MAX_INPUT_LENGTH) {
            return Result.failure(IllegalArgumentException("Number is too long. Maximum 500 digits."))
        }

        return try {
            // Normalize leading decimal point (e.g. ".5" -> "0.5", "-.5" -> "-0.5")
            val normalized = when {
                trimmed.startsWith(".") -> "0$trimmed"
                trimmed.startsWith("+.") -> "0${trimmed.substring(1)}"
                trimmed.startsWith("-.") -> "-0${trimmed.substring(2)}"
                trimmed.endsWith(".") -> trimmed.substring(0, trimmed.length - 1)
                else -> trimmed
            }
            val bigDecimal = BigDecimal(normalized)
            Result.success(bigDecimal)
        } catch (e: Exception) {
            Result.failure(IllegalArgumentException("Invalid number format: $input"))
        }
    }
}
