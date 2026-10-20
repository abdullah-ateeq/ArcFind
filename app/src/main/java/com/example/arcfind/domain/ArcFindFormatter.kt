package com.example.arcfind.domain

import com.example.arcfind.model.ArcResult
import com.example.arcfind.model.LengthUnit
import com.example.arcfind.model.SolveType
import java.math.BigDecimal
import java.math.RoundingMode

object ArcFindFormatter {

    fun formatNumber(
        value: BigDecimal,
        decimals: Int,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): String {
        if (decimals == 0) {
            return value.setScale(0, roundingMode).toPlainString()
        }
        val rounded = value.setScale(decimals, roundingMode)
        val stripped = value.stripTrailingZeros()
        // If the value originally has scale < decimals and ends with zeros only from padding (e.g. 10 or 0.125 or 10.000000),
        // we display the stripped value.
        // But if the stripped scale is >= decimals (e.g. 57.2957795... which rounded to 57.295780),
        // or if value already had exact scale == decimals, we preserve the rounded precision.
        if (stripped.scale() < decimals && (value.scale() < decimals || stripped.scale() <= 0)) {
            return if (stripped.scale() < 0) {
                stripped.setScale(0).toPlainString()
            } else {
                stripped.toPlainString()
            }
        }
        return rounded.toPlainString()
    }

    fun removeTrailingZeros(value: BigDecimal): String {
        val stripped = value.stripTrailingZeros()
        return if (stripped.scale() < 0) {
            stripped.setScale(0).toPlainString()
        } else {
            stripped.toPlainString()
        }
    }

    fun formatLength(
        value: BigDecimal,
        unit: LengthUnit,
        decimals: Int,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): String {
        val formatted = formatNumber(value, decimals, roundingMode)
        return "$formatted ${unit.symbol}"
    }

    fun formatAngle(
        value: BigDecimal,
        decimals: Int,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): String {
        val formatted = formatNumber(value, decimals, roundingMode)
        return "$formatted°"
    }

    fun formatPercentage(
        value: BigDecimal,
        decimals: Int,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): String {
        val formatted = formatNumber(value, decimals, roundingMode)
        return "$formatted%"
    }

    fun formatPrimaryResult(
        result: ArcResult,
        resultUnit: LengthUnit,
        decimals: Int,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): String {
        return when (result.solveType) {
            SolveType.FIND_ARC_LENGTH -> {
                val valueInTargetUnit = LengthUnitConverter.fromMeters(result.arcLength, resultUnit)
                formatLength(valueInTargetUnit, resultUnit, decimals, roundingMode)
            }
            SolveType.FIND_RADIUS -> {
                val valueInTargetUnit = LengthUnitConverter.fromMeters(result.radius, resultUnit)
                formatLength(valueInTargetUnit, resultUnit, decimals, roundingMode)
            }
            SolveType.FIND_ANGLE -> {
                formatAngle(result.angleDegrees, decimals, roundingMode)
            }
        }
    }

    fun formatPrimaryResultLabel(solveType: SolveType): String {
        return when (solveType) {
            SolveType.FIND_ARC_LENGTH -> "ARC LENGTH"
            SolveType.FIND_RADIUS -> "RADIUS"
            SolveType.FIND_ANGLE -> "CENTRAL ANGLE"
        }
    }

    fun formatCalculation(
        result: ArcResult,
        radiusUnit: LengthUnit,
        arcLengthUnit: LengthUnit,
        resultUnit: LengthUnit,
        decimals: Int,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): String {
        return buildString {
            appendLine("ArcFind Calculation")
            appendLine("Solve Mode: ${result.solveType.title}")
            val radVal = LengthUnitConverter.fromMeters(result.radius, radiusUnit)
            appendLine("Radius: ${formatLength(radVal, radiusUnit, decimals, roundingMode)}")
            appendLine("Central Angle: ${formatAngle(result.angleDegrees, decimals, roundingMode)}")
            val arcVal = LengthUnitConverter.fromMeters(result.arcLength, arcLengthUnit)
            appendLine("Arc Length: ${formatLength(arcVal, arcLengthUnit, decimals, roundingMode)}")
            appendLine("Arc Type: ${result.arcType.displayName}")
        }
    }

    fun formatShareText(
        result: ArcResult,
        radiusUnit: LengthUnit,
        arcLengthUnit: LengthUnit,
        resultUnit: LengthUnit,
        decimals: Int,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): String {
        val radVal = LengthUnitConverter.fromMeters(result.radius, radiusUnit)
        val arcVal = LengthUnitConverter.fromMeters(result.arcLength, arcLengthUnit)
        val radiusStr = formatLength(radVal, radiusUnit, decimals, roundingMode)
        val arcStr = formatLength(arcVal, arcLengthUnit, decimals, roundingMode)
        val angleStr = formatAngle(result.angleDegrees, decimals, roundingMode)

        return when (result.solveType) {
            SolveType.FIND_ARC_LENGTH -> {
                val resVal = LengthUnitConverter.fromMeters(result.arcLength, resultUnit)
                val resStr = formatLength(resVal, resultUnit, decimals, roundingMode)
                """
                ArcFind

                Radius: $radiusStr
                Central Angle: $angleStr

                Arc Length: $resStr
                Arc Type: ${result.arcType.displayName}
                """.trimIndent()
            }
            SolveType.FIND_RADIUS -> {
                val resVal = LengthUnitConverter.fromMeters(result.radius, resultUnit)
                val resStr = formatLength(resVal, resultUnit, decimals, roundingMode)
                """
                ArcFind

                Arc Length: $arcStr
                Central Angle: $angleStr

                Radius: $resStr
                """.trimIndent()
            }
            SolveType.FIND_ANGLE -> {
                """
                ArcFind

                Arc Length: $arcStr
                Radius: $radiusStr

                Central Angle: $angleStr
                """.trimIndent()
            }
        }
    }
}
