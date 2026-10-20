package com.example.arcfind.domain

import com.example.arcfind.math.BigDecimalMath
import com.example.arcfind.math.MathConstants
import com.example.arcfind.model.ArcType
import com.example.arcfind.model.LengthUnit
import com.example.arcfind.model.SolveType
import java.math.BigDecimal
import java.math.MathContext

object ArcCalculator {

    fun calculateArcLength(
        radius: BigDecimal,
        angleDegrees: BigDecimal,
        mathContext: MathContext = MathConstants.MATH_CONTEXT
    ): BigDecimal {
        val angleRadians = BigDecimalMath.degreesToRadians(angleDegrees, mathContext)
        return radius.multiply(angleRadians, mathContext)
    }

    fun calculateRadius(
        arcLength: BigDecimal,
        angleDegrees: BigDecimal,
        mathContext: MathContext = MathConstants.MATH_CONTEXT
    ): BigDecimal {
        val angleRadians = BigDecimalMath.degreesToRadians(angleDegrees, mathContext)
        return arcLength.divide(angleRadians, mathContext)
    }

    fun calculateAngle(
        arcLength: BigDecimal,
        radius: BigDecimal,
        mathContext: MathContext = MathConstants.MATH_CONTEXT
    ): BigDecimal {
        val angleRadians = arcLength.divide(radius, mathContext)
        return BigDecimalMath.radiansToDegrees(angleRadians, mathContext)
    }

    fun calculateDiameter(
        radius: BigDecimal,
        mathContext: MathContext = MathConstants.MATH_CONTEXT
    ): BigDecimal {
        return radius.multiply(MathConstants.BD_2, mathContext)
    }

    fun calculateCircumference(
        radius: BigDecimal,
        mathContext: MathContext = MathConstants.MATH_CONTEXT
    ): BigDecimal {
        return MathConstants.BD_2
            .multiply(MathConstants.PI, mathContext)
            .multiply(radius, mathContext)
    }

    fun calculateArcPercentage(
        angleDegrees: BigDecimal,
        mathContext: MathContext = MathConstants.MATH_CONTEXT
    ): BigDecimal {
        return angleDegrees
            .divide(MathConstants.BD_360, mathContext)
            .multiply(MathConstants.BD_100, mathContext)
    }

    fun determineArcType(angleDegrees: BigDecimal): ArcType {
        return when {
            angleDegrees.compareTo(MathConstants.BD_360) == 0 -> ArcType.FULL_CIRCLE
            angleDegrees.compareTo(MathConstants.BD_180) > 0 -> ArcType.MAJOR
            angleDegrees.compareTo(MathConstants.BD_180) == 0 -> ArcType.SEMICIRCLE
            else -> ArcType.MINOR
        }
    }

    fun generateSteps(
        solveType: SolveType,
        radiusInMeters: BigDecimal,
        angleDegrees: BigDecimal,
        arcLengthInMeters: BigDecimal,
        radiusUnit: LengthUnit,
        arcLengthUnit: LengthUnit,
        resultUnit: LengthUnit,
        decimals: Int,
        roundingMode: java.math.RoundingMode
    ): List<com.example.arcfind.model.CalculationStep> {
        val steps = mutableListOf<com.example.arcfind.model.CalculationStep>()
        val radUserVal = LengthUnitConverter.fromMeters(radiusInMeters, radiusUnit)
        val arcUserVal = LengthUnitConverter.fromMeters(arcLengthInMeters, arcLengthUnit)
        val radFormatted = ArcFindFormatter.formatLength(radUserVal, radiusUnit, decimals, roundingMode)
        val arcFormatted = ArcFindFormatter.formatLength(arcUserVal, arcLengthUnit, decimals, roundingMode)
        val angleFormatted = ArcFindFormatter.formatAngle(angleDegrees, decimals, roundingMode)
        val radians = BigDecimalMath.degreesToRadians(angleDegrees)
        val radRadiansFormatted = ArcFindFormatter.formatNumber(radians, decimals, roundingMode)

        when (solveType) {
            SolveType.FIND_ARC_LENGTH -> {
                steps.add(
                    com.example.arcfind.model.CalculationStep(
                        label = "Formula",
                        expression = "s = r × θ",
                        result = "Arc length = Radius × Central Angle (radians)"
                    )
                )
                steps.add(
                    com.example.arcfind.model.CalculationStep(
                        label = "Convert Angle to Radians",
                        expression = "θ = $angleFormatted × π / 180°",
                        result = "θ ≈ $radRadiansFormatted rad"
                    )
                )
                val targetArcVal = LengthUnitConverter.fromMeters(arcLengthInMeters, resultUnit)
                val targetArcFormatted = ArcFindFormatter.formatLength(targetArcVal, resultUnit, decimals, roundingMode)
                steps.add(
                    com.example.arcfind.model.CalculationStep(
                        label = "Calculate Arc Length",
                        expression = "s = $radFormatted × $radRadiansFormatted rad",
                        result = "s ≈ $targetArcFormatted"
                    )
                )
            }
            SolveType.FIND_RADIUS -> {
                steps.add(
                    com.example.arcfind.model.CalculationStep(
                        label = "Formula",
                        expression = "r = s / θ",
                        result = "Radius = Arc length / Central Angle (radians)"
                    )
                )
                steps.add(
                    com.example.arcfind.model.CalculationStep(
                        label = "Convert Angle to Radians",
                        expression = "θ = $angleFormatted × π / 180°",
                        result = "θ ≈ $radRadiansFormatted rad"
                    )
                )
                val targetRadVal = LengthUnitConverter.fromMeters(radiusInMeters, resultUnit)
                val targetRadFormatted = ArcFindFormatter.formatLength(targetRadVal, resultUnit, decimals, roundingMode)
                steps.add(
                    com.example.arcfind.model.CalculationStep(
                        label = "Calculate Radius",
                        expression = "r = $arcFormatted / $radRadiansFormatted rad",
                        result = "r ≈ $targetRadFormatted"
                    )
                )
            }
            SolveType.FIND_ANGLE -> {
                steps.add(
                    com.example.arcfind.model.CalculationStep(
                        label = "Formula",
                        expression = "θ = s / r",
                        result = "Angle in radians = Arc length / Radius (both in meters)"
                    )
                )
                steps.add(
                    com.example.arcfind.model.CalculationStep(
                        label = "Calculate Radians",
                        expression = "θ = $arcFormatted / $radFormatted",
                        result = "θ ≈ $radRadiansFormatted rad"
                    )
                )
                steps.add(
                    com.example.arcfind.model.CalculationStep(
                        label = "Convert to Degrees",
                        expression = "angle = $radRadiansFormatted rad × 180° / π",
                        result = "angle ≈ $angleFormatted"
                    )
                )
            }
        }
        return steps
    }
}
