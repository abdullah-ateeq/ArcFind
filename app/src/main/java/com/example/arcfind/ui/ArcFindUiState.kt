package com.example.arcfind.ui

import com.example.arcfind.model.AppSettings
import com.example.arcfind.model.ArcFindHistory
import com.example.arcfind.model.ArcResult
import com.example.arcfind.model.LengthUnit
import com.example.arcfind.model.SolveType

data class ArcFindUiState(
    val solveType: SolveType = SolveType.FIND_ARC_LENGTH,
    val radiusInput: String = "10",
    val angleInput: String = "60",
    val arcLengthInput: String = "",
    val radiusUnit: LengthUnit = LengthUnit.CENTIMETER,
    val arcLengthUnit: LengthUnit = LengthUnit.CENTIMETER,
    val resultUnit: LengthUnit = LengthUnit.CENTIMETER,
    val result: ArcResult? = null,
    val history: List<ArcFindHistory> = emptyList(),
    val errorMessage: String? = null,
    val isCalculating: Boolean = false,
    val settings: AppSettings = AppSettings(),
    val userNotification: String? = null
)
