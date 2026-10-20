package com.example.arcfind.domain

import com.example.arcfind.math.MathConstants
import com.example.arcfind.model.LengthUnit
import java.math.BigDecimal
import java.math.MathContext

object LengthUnitConverter {

    fun toMeters(
        value: BigDecimal,
        fromUnit: LengthUnit,
        mathContext: MathContext = MathConstants.MATH_CONTEXT
    ): BigDecimal {
        if (fromUnit == LengthUnit.METER) return value
        return value.multiply(fromUnit.metersPerUnit, mathContext)
    }

    fun fromMeters(
        valueInMeters: BigDecimal,
        toUnit: LengthUnit,
        mathContext: MathContext = MathConstants.MATH_CONTEXT
    ): BigDecimal {
        if (toUnit == LengthUnit.METER) return valueInMeters
        return valueInMeters.divide(toUnit.metersPerUnit, mathContext)
    }

    fun convert(
        value: BigDecimal,
        fromUnit: LengthUnit,
        toUnit: LengthUnit,
        mathContext: MathContext = MathConstants.MATH_CONTEXT
    ): BigDecimal {
        if (fromUnit == toUnit) return value
        val meters = toMeters(value, fromUnit, mathContext)
        return fromMeters(meters, toUnit, mathContext)
    }
}
