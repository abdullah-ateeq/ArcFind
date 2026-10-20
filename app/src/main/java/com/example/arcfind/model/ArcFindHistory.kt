package com.example.arcfind.model

data class ArcFindHistory(
    val id: String,
    val solveType: SolveType,
    val radius: String,
    val radiusUnit: LengthUnit,
    val angle: String,
    val arcLength: String,
    val arcLengthUnit: LengthUnit,
    val resultUnit: LengthUnit,
    val resultValue: String,
    val savedAt: Long
)
