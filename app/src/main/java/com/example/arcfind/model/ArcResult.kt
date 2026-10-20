package com.example.arcfind.model

import java.math.BigDecimal

data class ArcResult(
    val solveType: SolveType,
    val radius: BigDecimal,
    val angleDegrees: BigDecimal,
    val angleRadians: BigDecimal,
    val arcLength: BigDecimal,
    val diameter: BigDecimal,
    val circumference: BigDecimal,
    val arcPercentage: BigDecimal,
    val arcType: ArcType,
    val formula: String,
    val calculationSteps: List<CalculationStep>,
    val calculatedAt: Long
)
