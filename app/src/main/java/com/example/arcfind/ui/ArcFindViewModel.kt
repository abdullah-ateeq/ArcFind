package com.example.arcfind.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arcfind.domain.ArcCalculator
import com.example.arcfind.domain.ArcFindFormatter
import com.example.arcfind.domain.ArcFindParser
import com.example.arcfind.domain.ArcFindValidator
import com.example.arcfind.domain.LengthUnitConverter
import com.example.arcfind.math.BigDecimalMath
import com.example.arcfind.model.AppSettings
import com.example.arcfind.model.ArcFindError
import com.example.arcfind.model.ArcFindHistory
import com.example.arcfind.model.ArcResult
import com.example.arcfind.model.LengthUnit
import com.example.arcfind.model.SolveType
import com.example.arcfind.repository.HistoryRepository
import com.example.arcfind.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.UUID

class ArcFindViewModel(
    private val settingsRepository: SettingsRepository,
    private val historyRepository: HistoryRepository,
    dispatcher: kotlinx.coroutines.CoroutineDispatcher = kotlinx.coroutines.Dispatchers.Main
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ArcFindUiState(
            solveType = settingsRepository.getLastSolveType(),
            radiusInput = settingsRepository.getLastRadius(),
            angleInput = settingsRepository.getLastAngle(),
            arcLengthInput = settingsRepository.getLastArcLength(),
            radiusUnit = settingsRepository.getLastRadiusUnit(),
            arcLengthUnit = settingsRepository.getLastArcLengthUnit(),
            resultUnit = settingsRepository.getLastResultUnit(),
            settings = settingsRepository.getSettings(),
            history = historyRepository.getHistory()
        )
    )
    val uiState: StateFlow<ArcFindUiState> = _uiState.asStateFlow()

    init {
        // Observe settings changes
        viewModelScope.launch(dispatcher) {
            settingsRepository.settingsFlow.collect { settings ->
                _uiState.update { it.copy(settings = settings) }
                calculate()
            }
        }

        // Observe history changes
        viewModelScope.launch(dispatcher) {
            historyRepository.historyFlow.collect { history ->
                _uiState.update { it.copy(history = history) }
            }
        }

        calculate()
    }

    fun onSolveTypeSelected(solveType: SolveType) {
        if (_uiState.value.solveType == solveType) return
        _uiState.update { current ->
            // Pre-populate sensible defaults if needed when switching mode
            val newRadius = if (solveType != SolveType.FIND_RADIUS && current.radiusInput.isBlank()) "10" else current.radiusInput
            val newAngle = if (solveType != SolveType.FIND_ANGLE && current.angleInput.isBlank()) "60" else current.angleInput
            val newArc = if (solveType != SolveType.FIND_ARC_LENGTH && current.arcLengthInput.isBlank()) "10" else current.arcLengthInput

            current.copy(
                solveType = solveType,
                radiusInput = newRadius,
                angleInput = newAngle,
                arcLengthInput = newArc
            )
        }
        persistState()
        calculate()
    }

    fun onRadiusInputChanged(input: String) {
        _uiState.update { it.copy(radiusInput = input) }
        persistState()
        calculate()
    }

    fun onAngleInputChanged(input: String) {
        _uiState.update { it.copy(angleInput = input) }
        persistState()
        calculate()
    }

    fun onArcLengthInputChanged(input: String) {
        _uiState.update { it.copy(arcLengthInput = input) }
        persistState()
        calculate()
    }

    fun onRadiusUnitChanged(unit: LengthUnit) {
        _uiState.update { it.copy(radiusUnit = unit) }
        persistState()
        calculate()
    }

    fun onArcLengthUnitChanged(unit: LengthUnit) {
        _uiState.update { it.copy(arcLengthUnit = unit) }
        persistState()
        calculate()
    }

    fun onResultUnitChanged(unit: LengthUnit) {
        _uiState.update { it.copy(resultUnit = unit) }
        persistState()
        calculate()
    }

    fun calculate() {
        val currentState = _uiState.value
        val solveType = currentState.solveType
        val settings = currentState.settings

        when (solveType) {
            SolveType.FIND_ARC_LENGTH -> {
                val radiusErr = ArcFindValidator.validateRawInput(currentState.radiusInput)
                if (radiusErr != null) {
                    _uiState.update { it.copy(errorMessage = ArcFindValidator.getErrorMessage(radiusErr), result = null) }
                    return
                }
                val angleErr = ArcFindValidator.validateRawInput(currentState.angleInput)
                if (angleErr != null) {
                    _uiState.update { it.copy(errorMessage = ArcFindValidator.getErrorMessage(angleErr), result = null) }
                    return
                }

                val radiusParse = ArcFindParser.parseNumber(currentState.radiusInput)
                if (radiusParse.isFailure) {
                    _uiState.update { it.copy(errorMessage = ArcFindValidator.getErrorMessage(ArcFindError.InvalidNumber), result = null) }
                    return
                }
                val angleParse = ArcFindParser.parseNumber(currentState.angleInput)
                if (angleParse.isFailure) {
                    _uiState.update { it.copy(errorMessage = ArcFindValidator.getErrorMessage(ArcFindError.InvalidNumber), result = null) }
                    return
                }

                val radiusVal = radiusParse.getOrThrow()
                val angleVal = angleParse.getOrThrow()

                val radiusValErr = ArcFindValidator.validateRadius(radiusVal)
                if (radiusValErr != null) {
                    _uiState.update { it.copy(errorMessage = ArcFindValidator.getErrorMessage(radiusValErr), result = null) }
                    return
                }
                val angleValErr = ArcFindValidator.validateAngle(angleVal)
                if (angleValErr != null) {
                    _uiState.update { it.copy(errorMessage = ArcFindValidator.getErrorMessage(angleValErr), result = null) }
                    return
                }

                // Authoritative calculation in domain layer
                val radiusInMeters = LengthUnitConverter.toMeters(radiusVal, currentState.radiusUnit)
                val arcLengthInMeters = ArcCalculator.calculateArcLength(radiusInMeters, angleVal)
                val diameterInMeters = ArcCalculator.calculateDiameter(radiusInMeters)
                val circumferenceInMeters = ArcCalculator.calculateCircumference(radiusInMeters)
                val arcPercentage = ArcCalculator.calculateArcPercentage(angleVal)
                val arcType = ArcCalculator.determineArcType(angleVal)
                val angleRadians = BigDecimalMath.degreesToRadians(angleVal)

                val steps = ArcCalculator.generateSteps(
                    solveType = solveType,
                    radiusInMeters = radiusInMeters,
                    angleDegrees = angleVal,
                    arcLengthInMeters = arcLengthInMeters,
                    radiusUnit = currentState.radiusUnit,
                    arcLengthUnit = currentState.arcLengthUnit,
                    resultUnit = currentState.resultUnit,
                    decimals = settings.decimalPlaces,
                    roundingMode = settings.roundingMode
                )

                val result = ArcResult(
                    solveType = solveType,
                    radius = radiusInMeters,
                    angleDegrees = angleVal,
                    angleRadians = angleRadians,
                    arcLength = arcLengthInMeters,
                    diameter = diameterInMeters,
                    circumference = circumferenceInMeters,
                    arcPercentage = arcPercentage,
                    arcType = arcType,
                    formula = "s = r × θ",
                    calculationSteps = steps,
                    calculatedAt = System.currentTimeMillis()
                )

                _uiState.update { it.copy(result = result, errorMessage = null) }
            }

            SolveType.FIND_RADIUS -> {
                val arcErr = ArcFindValidator.validateRawInput(currentState.arcLengthInput)
                if (arcErr != null) {
                    _uiState.update { it.copy(errorMessage = ArcFindValidator.getErrorMessage(arcErr), result = null) }
                    return
                }
                val angleErr = ArcFindValidator.validateRawInput(currentState.angleInput)
                if (angleErr != null) {
                    _uiState.update { it.copy(errorMessage = ArcFindValidator.getErrorMessage(angleErr), result = null) }
                    return
                }

                val arcParse = ArcFindParser.parseNumber(currentState.arcLengthInput)
                if (arcParse.isFailure) {
                    _uiState.update { it.copy(errorMessage = ArcFindValidator.getErrorMessage(ArcFindError.InvalidNumber), result = null) }
                    return
                }
                val angleParse = ArcFindParser.parseNumber(currentState.angleInput)
                if (angleParse.isFailure) {
                    _uiState.update { it.copy(errorMessage = ArcFindValidator.getErrorMessage(ArcFindError.InvalidNumber), result = null) }
                    return
                }

                val arcVal = arcParse.getOrThrow()
                val angleVal = angleParse.getOrThrow()

                val arcValErr = ArcFindValidator.validateArcLength(arcVal)
                if (arcValErr != null) {
                    _uiState.update { it.copy(errorMessage = ArcFindValidator.getErrorMessage(arcValErr), result = null) }
                    return
                }
                val angleValErr = ArcFindValidator.validateAngle(angleVal)
                if (angleValErr != null) {
                    _uiState.update { it.copy(errorMessage = ArcFindValidator.getErrorMessage(angleValErr), result = null) }
                    return
                }

                val arcLengthInMeters = LengthUnitConverter.toMeters(arcVal, currentState.arcLengthUnit)
                val radiusInMeters = ArcCalculator.calculateRadius(arcLengthInMeters, angleVal)
                val diameterInMeters = ArcCalculator.calculateDiameter(radiusInMeters)
                val circumferenceInMeters = ArcCalculator.calculateCircumference(radiusInMeters)
                val arcPercentage = ArcCalculator.calculateArcPercentage(angleVal)
                val arcType = ArcCalculator.determineArcType(angleVal)
                val angleRadians = BigDecimalMath.degreesToRadians(angleVal)

                val steps = ArcCalculator.generateSteps(
                    solveType = solveType,
                    radiusInMeters = radiusInMeters,
                    angleDegrees = angleVal,
                    arcLengthInMeters = arcLengthInMeters,
                    radiusUnit = currentState.radiusUnit,
                    arcLengthUnit = currentState.arcLengthUnit,
                    resultUnit = currentState.resultUnit,
                    decimals = settings.decimalPlaces,
                    roundingMode = settings.roundingMode
                )

                val result = ArcResult(
                    solveType = solveType,
                    radius = radiusInMeters,
                    angleDegrees = angleVal,
                    angleRadians = angleRadians,
                    arcLength = arcLengthInMeters,
                    diameter = diameterInMeters,
                    circumference = circumferenceInMeters,
                    arcPercentage = arcPercentage,
                    arcType = arcType,
                    formula = "r = s / θ",
                    calculationSteps = steps,
                    calculatedAt = System.currentTimeMillis()
                )

                _uiState.update { it.copy(result = result, errorMessage = null) }
            }

            SolveType.FIND_ANGLE -> {
                val arcErr = ArcFindValidator.validateRawInput(currentState.arcLengthInput)
                if (arcErr != null) {
                    _uiState.update { it.copy(errorMessage = ArcFindValidator.getErrorMessage(arcErr), result = null) }
                    return
                }
                val radiusErr = ArcFindValidator.validateRawInput(currentState.radiusInput)
                if (radiusErr != null) {
                    _uiState.update { it.copy(errorMessage = ArcFindValidator.getErrorMessage(radiusErr), result = null) }
                    return
                }

                val arcParse = ArcFindParser.parseNumber(currentState.arcLengthInput)
                if (arcParse.isFailure) {
                    _uiState.update { it.copy(errorMessage = ArcFindValidator.getErrorMessage(ArcFindError.InvalidNumber), result = null) }
                    return
                }
                val radiusParse = ArcFindParser.parseNumber(currentState.radiusInput)
                if (radiusParse.isFailure) {
                    _uiState.update { it.copy(errorMessage = ArcFindValidator.getErrorMessage(ArcFindError.InvalidNumber), result = null) }
                    return
                }

                val arcVal = arcParse.getOrThrow()
                val radiusVal = radiusParse.getOrThrow()

                val arcValErr = ArcFindValidator.validateArcLength(arcVal)
                if (arcValErr != null) {
                    _uiState.update { it.copy(errorMessage = ArcFindValidator.getErrorMessage(arcValErr), result = null) }
                    return
                }
                val radiusValErr = ArcFindValidator.validateRadius(radiusVal)
                if (radiusValErr != null) {
                    _uiState.update { it.copy(errorMessage = ArcFindValidator.getErrorMessage(radiusValErr), result = null) }
                    return
                }

                val arcLengthInMeters = LengthUnitConverter.toMeters(arcVal, currentState.arcLengthUnit)
                val radiusInMeters = LengthUnitConverter.toMeters(radiusVal, currentState.radiusUnit)
                val angleVal = ArcCalculator.calculateAngle(arcLengthInMeters, radiusInMeters)

                // Validate calculated angle <= 360°
                val calcAngleErr = ArcFindValidator.validateCalculatedAngle(angleVal)
                if (calcAngleErr != null) {
                    _uiState.update { it.copy(errorMessage = ArcFindValidator.getErrorMessage(calcAngleErr), result = null) }
                    return
                }

                val diameterInMeters = ArcCalculator.calculateDiameter(radiusInMeters)
                val circumferenceInMeters = ArcCalculator.calculateCircumference(radiusInMeters)
                val arcPercentage = ArcCalculator.calculateArcPercentage(angleVal)
                val arcType = ArcCalculator.determineArcType(angleVal)
                val angleRadians = BigDecimalMath.degreesToRadians(angleVal)

                val steps = ArcCalculator.generateSteps(
                    solveType = solveType,
                    radiusInMeters = radiusInMeters,
                    angleDegrees = angleVal,
                    arcLengthInMeters = arcLengthInMeters,
                    radiusUnit = currentState.radiusUnit,
                    arcLengthUnit = currentState.arcLengthUnit,
                    resultUnit = currentState.resultUnit,
                    decimals = settings.decimalPlaces,
                    roundingMode = settings.roundingMode
                )

                val result = ArcResult(
                    solveType = solveType,
                    radius = radiusInMeters,
                    angleDegrees = angleVal,
                    angleRadians = angleRadians,
                    arcLength = arcLengthInMeters,
                    diameter = diameterInMeters,
                    circumference = circumferenceInMeters,
                    arcPercentage = arcPercentage,
                    arcType = arcType,
                    formula = "θ = s / r",
                    calculationSteps = steps,
                    calculatedAt = System.currentTimeMillis()
                )

                _uiState.update { it.copy(result = result, errorMessage = null) }
            }
        }
    }

    fun onSaveCalculation() {
        val state = _uiState.value
        val result = state.result ?: return

        val primaryResultFormatted = ArcFindFormatter.formatPrimaryResult(
            result = result,
            resultUnit = state.resultUnit,
            decimals = state.settings.decimalPlaces,
            roundingMode = state.settings.roundingMode
        )

        val entry = ArcFindHistory(
            id = UUID.randomUUID().toString(),
            solveType = state.solveType,
            radius = state.radiusInput,
            radiusUnit = state.radiusUnit,
            angle = state.angleInput,
            arcLength = state.arcLengthInput,
            arcLengthUnit = state.arcLengthUnit,
            resultUnit = state.resultUnit,
            resultValue = primaryResultFormatted,
            savedAt = System.currentTimeMillis()
        )

        val saved = historyRepository.saveCalculation(entry)
        if (saved) {
            _uiState.update { it.copy(userNotification = "Calculation saved to History") }
        } else {
            _uiState.update { it.copy(userNotification = "Already saved in History") }
        }
    }

    fun onRestoreHistory(item: ArcFindHistory) {
        _uiState.update { current ->
            current.copy(
                solveType = item.solveType,
                radiusInput = item.radius,
                radiusUnit = item.radiusUnit,
                angleInput = item.angle,
                arcLengthInput = item.arcLength,
                arcLengthUnit = item.arcLengthUnit,
                resultUnit = item.resultUnit
            )
        }
        persistState()
        calculate()
    }

    fun onDeleteHistory(id: String) {
        historyRepository.deleteEntry(id)
    }

    fun onClearHistory() {
        historyRepository.clearAll()
        _uiState.update { it.copy(userNotification = "History cleared") }
    }

    fun onResetAll() {
        // Reset to default solve mode (FIND_ARC_LENGTH) and default example (radius = 10, angle = 60), history remains intact!
        _uiState.update { current ->
            current.copy(
                solveType = SolveType.FIND_ARC_LENGTH,
                radiusInput = "10",
                angleInput = "60",
                arcLengthInput = "",
                radiusUnit = LengthUnit.CENTIMETER,
                arcLengthUnit = LengthUnit.CENTIMETER,
                resultUnit = LengthUnit.CENTIMETER
            )
        }
        persistState()
        calculate()
    }

    fun onUpdateSettings(newSettings: AppSettings) {
        settingsRepository.updateSettings(newSettings)
    }

    fun onResetSettings() {
        settingsRepository.resetSettings()
        _uiState.update { it.copy(userNotification = "Settings reset to defaults") }
    }

    fun clearNotification() {
        _uiState.update { it.copy(userNotification = null) }
    }

    fun getShareText(): String {
        val state = _uiState.value
        val result = state.result ?: return ""
        return ArcFindFormatter.formatShareText(
            result = result,
            radiusUnit = state.radiusUnit,
            arcLengthUnit = state.arcLengthUnit,
            resultUnit = state.resultUnit,
            decimals = state.settings.decimalPlaces,
            roundingMode = state.settings.roundingMode
        )
    }

    fun getCopyValueOnly(): String {
        val state = _uiState.value
        val result = state.result ?: return ""
        return ArcFindFormatter.formatPrimaryResult(
            result = result,
            resultUnit = state.resultUnit,
            decimals = state.settings.decimalPlaces,
            roundingMode = state.settings.roundingMode
        )
    }

    private fun persistState() {
        val state = _uiState.value
        settingsRepository.saveLastInputs(
            solveType = state.solveType,
            radius = state.radiusInput,
            angle = state.angleInput,
            arcLength = state.arcLengthInput,
            radiusUnit = state.radiusUnit,
            arcLengthUnit = state.arcLengthUnit,
            resultUnit = state.resultUnit
        )
    }
}
