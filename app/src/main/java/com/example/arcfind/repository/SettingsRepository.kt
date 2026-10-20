package com.example.arcfind.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.arcfind.model.AppSettings
import com.example.arcfind.model.AppTheme
import com.example.arcfind.model.LengthUnit
import com.example.arcfind.model.SolveType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.RoundingMode

class SettingsRepository(private val prefs: SharedPreferences) {

    constructor(context: Context) : this(
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    )

    private val _settingsFlow = MutableStateFlow(loadSettings())
    val settingsFlow: StateFlow<AppSettings> = _settingsFlow.asStateFlow()

    fun getSettings(): AppSettings = _settingsFlow.value

    fun updateSettings(newSettings: AppSettings) {
        prefs.edit()
            .putString(KEY_THEME, newSettings.theme.name)
            .putBoolean(KEY_DYNAMIC_COLOR, newSettings.dynamicColor)
            .putBoolean(KEY_HAPTIC, newSettings.hapticFeedback)
            .putBoolean(KEY_ANIMATIONS, newSettings.animations)
            .putInt(KEY_DECIMALS, newSettings.decimalPlaces)
            .putString(KEY_ROUNDING_MODE, newSettings.roundingMode.name)
            .putBoolean(KEY_SHOW_FORMULA, newSettings.showFormula)
            .putBoolean(KEY_SHOW_STEPS, newSettings.showCalculationSteps)
            .putBoolean(KEY_SHOW_DIAGRAM, newSettings.showCircleDiagram)
            .putBoolean(KEY_SHOW_DETAILS, newSettings.showMoreDetails)
            .putBoolean(KEY_CONFIRM_DELETE, newSettings.confirmDelete)
            .apply()

        _settingsFlow.value = newSettings
    }

    fun resetSettings() {
        val defaultSettings = AppSettings()
        updateSettings(defaultSettings)
    }

    fun saveLastInputs(
        solveType: SolveType,
        radius: String,
        angle: String,
        arcLength: String,
        radiusUnit: LengthUnit,
        arcLengthUnit: LengthUnit,
        resultUnit: LengthUnit
    ) {
        prefs.edit()
            .putString(KEY_LAST_SOLVE_TYPE, solveType.name)
            .putString(KEY_LAST_RADIUS, radius)
            .putString(KEY_LAST_ANGLE, angle)
            .putString(KEY_LAST_ARC_LENGTH, arcLength)
            .putString(KEY_LAST_RADIUS_UNIT, radiusUnit.name)
            .putString(KEY_LAST_ARC_LENGTH_UNIT, arcLengthUnit.name)
            .putString(KEY_LAST_RESULT_UNIT, resultUnit.name)
            .apply()
    }

    fun getLastSolveType(): SolveType {
        val name = prefs.getString(KEY_LAST_SOLVE_TYPE, SolveType.FIND_ARC_LENGTH.name)
        return try {
            SolveType.valueOf(name ?: SolveType.FIND_ARC_LENGTH.name)
        } catch (e: Exception) {
            SolveType.FIND_ARC_LENGTH
        }
    }

    fun getLastRadius(): String = prefs.getString(KEY_LAST_RADIUS, "10") ?: "10"
    fun getLastAngle(): String = prefs.getString(KEY_LAST_ANGLE, "60") ?: "60"
    fun getLastArcLength(): String = prefs.getString(KEY_LAST_ARC_LENGTH, "") ?: ""

    fun getLastRadiusUnit(): LengthUnit {
        val name = prefs.getString(KEY_LAST_RADIUS_UNIT, LengthUnit.CENTIMETER.name)
        return try {
            LengthUnit.valueOf(name ?: LengthUnit.CENTIMETER.name)
        } catch (e: Exception) {
            LengthUnit.CENTIMETER
        }
    }

    fun getLastArcLengthUnit(): LengthUnit {
        val name = prefs.getString(KEY_LAST_ARC_LENGTH_UNIT, LengthUnit.CENTIMETER.name)
        return try {
            LengthUnit.valueOf(name ?: LengthUnit.CENTIMETER.name)
        } catch (e: Exception) {
            LengthUnit.CENTIMETER
        }
    }

    fun getLastResultUnit(): LengthUnit {
        val name = prefs.getString(KEY_LAST_RESULT_UNIT, LengthUnit.CENTIMETER.name)
        return try {
            LengthUnit.valueOf(name ?: LengthUnit.CENTIMETER.name)
        } catch (e: Exception) {
            LengthUnit.CENTIMETER
        }
    }

    private fun loadSettings(): AppSettings {
        val themeName = prefs.getString(KEY_THEME, AppTheme.SYSTEM.name)
        val theme = try {
            AppTheme.valueOf(themeName ?: AppTheme.SYSTEM.name)
        } catch (e: Exception) {
            AppTheme.SYSTEM
        }

        val roundingName = prefs.getString(KEY_ROUNDING_MODE, RoundingMode.HALF_UP.name)
        val roundingMode = try {
            RoundingMode.valueOf(roundingName ?: RoundingMode.HALF_UP.name)
        } catch (e: Exception) {
            RoundingMode.HALF_UP
        }

        return AppSettings(
            theme = theme,
            dynamicColor = prefs.getBoolean(KEY_DYNAMIC_COLOR, true),
            hapticFeedback = prefs.getBoolean(KEY_HAPTIC, true),
            animations = prefs.getBoolean(KEY_ANIMATIONS, true),
            decimalPlaces = prefs.getInt(KEY_DECIMALS, 6),
            roundingMode = roundingMode,
            showFormula = prefs.getBoolean(KEY_SHOW_FORMULA, true),
            showCalculationSteps = prefs.getBoolean(KEY_SHOW_STEPS, true),
            showCircleDiagram = prefs.getBoolean(KEY_SHOW_DIAGRAM, true),
            showMoreDetails = prefs.getBoolean(KEY_SHOW_DETAILS, false),
            confirmDelete = prefs.getBoolean(KEY_CONFIRM_DELETE, true)
        )
    }

    companion object {
        private const val PREFS_NAME = "arcfind_settings"

        private const val KEY_THEME = "key_theme"
        private const val KEY_DYNAMIC_COLOR = "key_dynamic_color"
        private const val KEY_HAPTIC = "key_haptic"
        private const val KEY_ANIMATIONS = "key_animations"
        private const val KEY_DECIMALS = "key_decimals"
        private const val KEY_ROUNDING_MODE = "key_rounding_mode"
        private const val KEY_SHOW_FORMULA = "key_show_formula"
        private const val KEY_SHOW_STEPS = "key_show_steps"
        private const val KEY_SHOW_DIAGRAM = "key_show_diagram"
        private const val KEY_SHOW_DETAILS = "key_show_details"
        private const val KEY_CONFIRM_DELETE = "key_confirm_delete"

        private const val KEY_LAST_SOLVE_TYPE = "key_last_solve_type"
        private const val KEY_LAST_RADIUS = "key_last_radius"
        private const val KEY_LAST_ANGLE = "key_last_angle"
        private const val KEY_LAST_ARC_LENGTH = "key_last_arc_length"
        private const val KEY_LAST_RADIUS_UNIT = "key_last_radius_unit"
        private const val KEY_LAST_ARC_LENGTH_UNIT = "key_last_arc_length_unit"
        private const val KEY_LAST_RESULT_UNIT = "key_last_result_unit"
    }
}
