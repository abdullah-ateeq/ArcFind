package com.example.arcfind.domain

import com.example.arcfind.math.MathConstants
import com.example.arcfind.model.ArcFindError
import java.math.BigDecimal

object ArcFindValidator {

    private const val MAX_INPUT_LENGTH = 500

    fun validateRawInput(input: String): ArcFindError? {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return ArcFindError.EmptyInput
        }
        if (trimmed.length > MAX_INPUT_LENGTH) {
            return ArcFindError.InputTooLong
        }
        return null
    }

    fun validateRadius(radius: BigDecimal): ArcFindError? {
        if (radius <= BigDecimal.ZERO) {
            return ArcFindError.NonPositiveRadius
        }
        return null
    }

    fun validateArcLength(arcLength: BigDecimal): ArcFindError? {
        if (arcLength <= BigDecimal.ZERO) {
            return ArcFindError.NonPositiveArcLength
        }
        return null
    }

    fun validateAngle(angleDegrees: BigDecimal): ArcFindError? {
        if (angleDegrees <= BigDecimal.ZERO || angleDegrees > MathConstants.BD_360) {
            return ArcFindError.InvalidAngle
        }
        return null
    }

    fun validateCalculatedAngle(calculatedAngleDegrees: BigDecimal): ArcFindError? {
        if (calculatedAngleDegrees > MathConstants.BD_360) {
            return ArcFindError.AngleExceedsFullCircle
        }
        if (calculatedAngleDegrees <= BigDecimal.ZERO) {
            return ArcFindError.InvalidAngle
        }
        return null
    }

    fun getErrorMessage(error: ArcFindError): String {
        return when (error) {
            is ArcFindError.EmptyInput -> "Please enter all required values."
            is ArcFindError.InputTooLong -> "Number is too long. Maximum 500 digits."
            is ArcFindError.InvalidNumber -> "Invalid number format."
            is ArcFindError.NonPositiveRadius -> "Radius must be greater than zero."
            is ArcFindError.NonPositiveArcLength -> "Arc length must be greater than zero."
            is ArcFindError.InvalidAngle -> "Angle must be greater than 0° and no greater than 360°."
            is ArcFindError.AngleExceedsFullCircle -> "Arc length is too large for a single 360° arc with this radius."
        }
    }
}
