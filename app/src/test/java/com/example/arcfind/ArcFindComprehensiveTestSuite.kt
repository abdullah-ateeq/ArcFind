package com.example.arcfind

import com.example.arcfind.domain.ArcCalculator
import com.example.arcfind.domain.ArcFindFormatter
import com.example.arcfind.domain.ArcFindParser
import com.example.arcfind.domain.ArcFindValidator
import com.example.arcfind.domain.LengthUnitConverter
import com.example.arcfind.math.BigDecimalMath
import com.example.arcfind.math.MathConstants
import com.example.arcfind.model.AppSettings
import com.example.arcfind.model.AppTheme
import com.example.arcfind.model.ArcFindError
import com.example.arcfind.model.ArcFindHistory
import com.example.arcfind.model.ArcType
import com.example.arcfind.model.LengthUnit
import com.example.arcfind.model.SolveType
import com.example.arcfind.repository.HistoryRepository
import com.example.arcfind.repository.SettingsRepository
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class ArcFindComprehensiveTestSuite {

    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // 1. Find arc length: Radius = 10 cm, Angle = 60° -> 10.471976 cm
    @Test
    fun test01_findArcLength() {
        val radius = BigDecimal("10")
        val angle = BigDecimal("60")
        val arcLength = ArcCalculator.calculateArcLength(radius, angle)
        val formatted = ArcFindFormatter.formatLength(arcLength, LengthUnit.CENTIMETER, 6, RoundingMode.HALF_UP)
        assertEquals("10.471976 cm", formatted)
    }

    // 2. Find radius: Arc Length = 10 cm, Angle = 60° -> 9.549297 cm
    @Test
    fun test02_findRadius() {
        val arcLength = BigDecimal("10")
        val angle = BigDecimal("60")
        val radius = ArcCalculator.calculateRadius(arcLength, angle)
        val formatted = ArcFindFormatter.formatLength(radius, LengthUnit.CENTIMETER, 6, RoundingMode.HALF_UP)
        assertEquals("9.549297 cm", formatted)
    }

    // 3. Find angle: Arc Length = 10 cm, Radius = 10 cm -> 57.295780°
    @Test
    fun test03_findAngle() {
        val arcLength = BigDecimal("10")
        val radius = BigDecimal("10")
        val angle = ArcCalculator.calculateAngle(arcLength, radius)
        val formatted = ArcFindFormatter.formatAngle(angle, 6, RoundingMode.HALF_UP)
        assertEquals("57.295780°", formatted)
    }

    // 4. 30° case: Radius = 10 cm, Angle = 30° -> 5.235988 cm
    @Test
    fun test04_thirtyDegrees() {
        val radius = BigDecimal("10")
        val angle = BigDecimal("30")
        val arcLength = ArcCalculator.calculateArcLength(radius, angle)
        val formatted = ArcFindFormatter.formatLength(arcLength, LengthUnit.CENTIMETER, 6, RoundingMode.HALF_UP)
        assertEquals("5.235988 cm", formatted)
    }

    // 5. 45° case: Radius = 10 cm, Angle = 45° -> 7.853982 cm
    @Test
    fun test05_fortyFiveDegrees() {
        val radius = BigDecimal("10")
        val angle = BigDecimal("45")
        val arcLength = ArcCalculator.calculateArcLength(radius, angle)
        val formatted = ArcFindFormatter.formatLength(arcLength, LengthUnit.CENTIMETER, 6, RoundingMode.HALF_UP)
        assertEquals("7.853982 cm", formatted)
    }

    // 6. 60° case: Radius = 10 cm, Angle = 60° -> 10.471976 cm
    @Test
    fun test06_sixtyDegrees() {
        val radius = BigDecimal("10")
        val angle = BigDecimal("60")
        val arcLength = ArcCalculator.calculateArcLength(radius, angle)
        val formatted = ArcFindFormatter.formatLength(arcLength, LengthUnit.CENTIMETER, 6, RoundingMode.HALF_UP)
        assertEquals("10.471976 cm", formatted)
    }

    // 7. 90° case: Radius = 5 cm, Angle = 90° -> 7.853982 cm
    @Test
    fun test07_ninetyDegrees() {
        val radius = BigDecimal("5")
        val angle = BigDecimal("90")
        val arcLength = ArcCalculator.calculateArcLength(radius, angle)
        val formatted = ArcFindFormatter.formatLength(arcLength, LengthUnit.CENTIMETER, 6, RoundingMode.HALF_UP)
        assertEquals("7.853982 cm", formatted)
    }

    // 8. 120° case: Radius = 6 cm, Angle = 120° -> 12.566371 cm
    @Test
    fun test08_oneTwentyDegrees() {
        val radius = BigDecimal("6")
        val angle = BigDecimal("120")
        val arcLength = ArcCalculator.calculateArcLength(radius, angle)
        val formatted = ArcFindFormatter.formatLength(arcLength, LengthUnit.CENTIMETER, 6, RoundingMode.HALF_UP)
        assertEquals("12.566371 cm", formatted)
    }

    // 9. 180° case: Radius = 5 cm, Angle = 180° -> 15.707963 cm
    @Test
    fun test09_oneEightyDegrees() {
        val radius = BigDecimal("5")
        val angle = BigDecimal("180")
        val arcLength = ArcCalculator.calculateArcLength(radius, angle)
        val formatted = ArcFindFormatter.formatLength(arcLength, LengthUnit.CENTIMETER, 6, RoundingMode.HALF_UP)
        assertEquals("15.707963 cm", formatted)
    }

    // 10. 270° case: Radius = 10 cm, Angle = 270° -> 47.123890 cm
    @Test
    fun test10_twoSeventyDegrees() {
        val radius = BigDecimal("10")
        val angle = BigDecimal("270")
        val arcLength = ArcCalculator.calculateArcLength(radius, angle)
        val formatted = ArcFindFormatter.formatLength(arcLength, LengthUnit.CENTIMETER, 6, RoundingMode.HALF_UP)
        assertEquals("47.123890 cm", formatted)
    }

    // 11. 360° case: Radius = 5 cm, Angle = 360° -> 31.415927 cm
    @Test
    fun test11_threeSixtyDegrees() {
        val radius = BigDecimal("5")
        val angle = BigDecimal("360")
        val arcLength = ArcCalculator.calculateArcLength(radius, angle)
        val formatted = ArcFindFormatter.formatLength(arcLength, LengthUnit.CENTIMETER, 6, RoundingMode.HALF_UP)
        assertEquals("31.415927 cm", formatted)
    }

    // 12. Minor arc: angle < 180° -> MINOR
    @Test
    fun test12_minorArc() {
        assertEquals(ArcType.MINOR, ArcCalculator.determineArcType(BigDecimal("45")))
        assertEquals(ArcType.MINOR, ArcCalculator.determineArcType(BigDecimal("90")))
        assertEquals(ArcType.MINOR, ArcCalculator.determineArcType(BigDecimal("179.999")))
    }

    // 13. Semicircle: angle = 180° -> SEMICIRCLE
    @Test
    fun test13_semicircle() {
        assertEquals(ArcType.SEMICIRCLE, ArcCalculator.determineArcType(BigDecimal("180")))
    }

    // 14. Major arc: 180° < angle < 360° -> MAJOR
    @Test
    fun test14_majorArc() {
        assertEquals(ArcType.MAJOR, ArcCalculator.determineArcType(BigDecimal("180.001")))
        assertEquals(ArcType.MAJOR, ArcCalculator.determineArcType(BigDecimal("270")))
        assertEquals(ArcType.MAJOR, ArcCalculator.determineArcType(BigDecimal("359.999")))
    }

    // 15. Full circle: angle = 360° -> FULL_CIRCLE
    @Test
    fun test15_fullCircle() {
        assertEquals(ArcType.FULL_CIRCLE, ArcCalculator.determineArcType(BigDecimal("360")))
    }

    // 16. Zero angle rejection: angle = 0 -> InvalidAngle
    @Test
    fun test16_zeroAngleRejection() {
        val error = ArcFindValidator.validateAngle(BigDecimal.ZERO)
        assertEquals(ArcFindError.InvalidAngle, error)
        assertEquals("Angle must be greater than 0° and no greater than 360°.", ArcFindValidator.getErrorMessage(error!!))
    }

    // 17. Negative angle rejection: angle = -45 -> InvalidAngle
    @Test
    fun test17_negativeAngleRejection() {
        val error = ArcFindValidator.validateAngle(BigDecimal("-45"))
        assertEquals(ArcFindError.InvalidAngle, error)
        assertEquals("Angle must be greater than 0° and no greater than 360°.", ArcFindValidator.getErrorMessage(error!!))
    }

    // 18. Angle > 360° rejection: angle = 361 -> InvalidAngle
    @Test
    fun test18_angleOver360Rejection() {
        val error = ArcFindValidator.validateAngle(BigDecimal("361"))
        assertEquals(ArcFindError.InvalidAngle, error)
        assertEquals("Angle must be greater than 0° and no greater than 360°.", ArcFindValidator.getErrorMessage(error!!))
    }

    // 19. Zero radius rejection: radius = 0 -> NonPositiveRadius
    @Test
    fun test19_zeroRadiusRejection() {
        val error = ArcFindValidator.validateRadius(BigDecimal.ZERO)
        assertEquals(ArcFindError.NonPositiveRadius, error)
        assertEquals("Radius must be greater than zero.", ArcFindValidator.getErrorMessage(error!!))
    }

    // 20. Negative radius rejection: radius = -5 -> NonPositiveRadius
    @Test
    fun test20_negativeRadiusRejection() {
        val error = ArcFindValidator.validateRadius(BigDecimal("-5"))
        assertEquals(ArcFindError.NonPositiveRadius, error)
        assertEquals("Radius must be greater than zero.", ArcFindValidator.getErrorMessage(error!!))
    }

    // 21. Zero arc length rejection: arc = 0 -> NonPositiveArcLength
    @Test
    fun test21_zeroArcLengthRejection() {
        val error = ArcFindValidator.validateArcLength(BigDecimal.ZERO)
        assertEquals(ArcFindError.NonPositiveArcLength, error)
        assertEquals("Arc length must be greater than zero.", ArcFindValidator.getErrorMessage(error!!))
    }

    // 22. Negative arc length rejection: arc = -10 -> NonPositiveArcLength
    @Test
    fun test22_negativeArcLengthRejection() {
        val error = ArcFindValidator.validateArcLength(BigDecimal("-10"))
        assertEquals(ArcFindError.NonPositiveArcLength, error)
        assertEquals("Arc length must be greater than zero.", ArcFindValidator.getErrorMessage(error!!))
    }

    // 23. Calculated angle > 360° rejection: arc = 100 cm, radius = 10 cm -> AngleExceedsFullCircle
    @Test
    fun test23_calculatedAngleOver360Rejection() {
        val arcLength = BigDecimal("100")
        val radius = BigDecimal("10")
        val calculatedAngle = ArcCalculator.calculateAngle(arcLength, radius)
        val error = ArcFindValidator.validateCalculatedAngle(calculatedAngle)
        assertEquals(ArcFindError.AngleExceedsFullCircle, error)
        assertEquals("Arc length is too large for a single 360° arc with this radius.", ArcFindValidator.getErrorMessage(error!!))
    }

    // 24. Decimal values: "100.5" -> 100.5
    @Test
    fun test24_decimalValues() {
        val result = ArcFindParser.parseNumber("100.5")
        assertTrue(result.isSuccess)
        assertEquals(BigDecimal("100.5"), result.getOrThrow())
    }

    // 25. Leading decimal: ".5" -> 0.5
    @Test
    fun test25_leadingDecimal() {
        val result = ArcFindParser.parseNumber(".5")
        assertTrue(result.isSuccess)
        assertEquals(BigDecimal("0.5"), result.getOrThrow())
    }

    // 26. Trailing decimal: "10." -> 10
    @Test
    fun test26_trailingDecimal() {
        val result = ArcFindParser.parseNumber("10.")
        assertTrue(result.isSuccess)
        assertEquals(BigDecimal("10"), result.getOrThrow())
    }

    // 27. Scientific notation: "1E+5" and "1.5E-2"
    @Test
    fun test27_scientificNotation() {
        val res1 = ArcFindParser.parseNumber("1E+5")
        assertTrue(res1.isSuccess)
        assertEquals(0, BigDecimal("100000").compareTo(res1.getOrThrow()))

        val res2 = ArcFindParser.parseNumber("1.5E-2")
        assertTrue(res2.isSuccess)
        assertEquals(0, BigDecimal("0.015").compareTo(res2.getOrThrow()))
    }

    // 28. Very small values: radius = 0.000001 cm, angle = 0.001°
    @Test
    fun test28_verySmallValues() {
        val radius = BigDecimal("0.000001")
        val angle = BigDecimal("0.001")
        val arcLength = ArcCalculator.calculateArcLength(radius, angle)
        assertTrue(arcLength > BigDecimal.ZERO)
        // High precision without underflow
        val formatted = ArcFindFormatter.formatLength(arcLength, LengthUnit.CENTIMETER, 12, RoundingMode.HALF_UP)
        assertTrue(formatted.contains("cm"))
    }

    // 29. Very large values: radius = 999999999999999999999999, angle = 60°
    @Test
    fun test29_veryLargeValues() {
        val radius = BigDecimal("999999999999999999999999")
        val angle = BigDecimal("60")
        val arcLength = ArcCalculator.calculateArcLength(radius, angle)
        assertTrue(arcLength > radius) // angle 60° = pi/3 rad > 1, so s > r
    }

    // 30. High-precision PI: at least 50 digits
    @Test
    fun test30_highPrecisionPI() {
        val piStr = MathConstants.PI.toPlainString()
        assertTrue(piStr.startsWith("3.1415926535897932384626433832795028841971693993751"))
        assertTrue(piStr.length >= 52)
    }

    // 31. Degree-to-radian conversion: 180° = π rad
    @Test
    fun test31_degreesToRadians() {
        val radians = BigDecimalMath.degreesToRadians(BigDecimal("180"))
        val expectedPiAtMathContext = MathConstants.PI.round(MathConstants.MATH_CONTEXT)
        assertEquals(0, expectedPiAtMathContext.compareTo(radians))
    }

    // 32. Radian-to-degree conversion: π rad = 180°
    @Test
    fun test32_radiansToDegrees() {
        val degrees = BigDecimalMath.radiansToDegrees(MathConstants.PI)
        assertEquals(0, BigDecimal("180").compareTo(degrees))
    }

    // 33. BigDecimal division
    @Test
    fun test33_bigDecimalDivision() {
        val result = BigDecimalMath.safeDivide(BigDecimal("10"), BigDecimal("3"))
        assertTrue(result.toPlainString().startsWith("3.33333333333333333333"))
    }

    // 34. BigDecimal multiplication
    @Test
    fun test34_bigDecimalMultiplication() {
        val result = BigDecimalMath.safeMultiply(BigDecimal("123.456"), BigDecimal("78.9"))
        assertEquals(0, BigDecimal("9740.6784").compareTo(result))
    }

    // 35. Length unit conversion: 100 cm = 1 m, 1000 mm = 1 m, 12 in = 0.3048 m
    @Test
    fun test35_lengthUnitConversion() {
        val metersFromCm = LengthUnitConverter.toMeters(BigDecimal("100"), LengthUnit.CENTIMETER)
        assertEquals(0, BigDecimal("1").compareTo(metersFromCm))

        val metersFromMm = LengthUnitConverter.toMeters(BigDecimal("1000"), LengthUnit.MILLIMETER)
        assertEquals(0, BigDecimal("1").compareTo(metersFromMm))

        val metersFromIn = LengthUnitConverter.toMeters(BigDecimal("12"), LengthUnit.INCH)
        assertEquals(0, BigDecimal("0.3048").compareTo(metersFromIn))
    }

    // 36. Mixed units: Arc Length = 1 m, Angle = 90°, Result unit = cm -> 63.661977 cm
    @Test
    fun test36_mixedUnits() {
        val arcLengthMeters = LengthUnitConverter.toMeters(BigDecimal("1"), LengthUnit.METER)
        val radiusMeters = ArcCalculator.calculateRadius(arcLengthMeters, BigDecimal("90"))
        val radiusCm = LengthUnitConverter.fromMeters(radiusMeters, LengthUnit.CENTIMETER)
        val formatted = ArcFindFormatter.formatLength(radiusCm, LengthUnit.CENTIMETER, 6, RoundingMode.HALF_UP)
        assertEquals("63.661977 cm", formatted)

        // Arc Length = 100 cm, Radius = 0.5 m -> Angle ≈ 114.591559°
        val arcMeters = LengthUnitConverter.toMeters(BigDecimal("100"), LengthUnit.CENTIMETER)
        val radMeters = LengthUnitConverter.toMeters(BigDecimal("0.5"), LengthUnit.METER)
        val angleDeg = ArcCalculator.calculateAngle(arcMeters, radMeters)
        val angleFormatted = ArcFindFormatter.formatAngle(angleDeg, 6, RoundingMode.HALF_UP)
        assertEquals("114.591559°", angleFormatted)
    }

    // 37. Result formatting: formats length and angle correctly
    @Test
    fun test37_resultFormatting() {
        val lenStr = ArcFindFormatter.formatLength(BigDecimal("10.471976"), LengthUnit.CENTIMETER, 6)
        assertEquals("10.471976 cm", lenStr)
        val angStr = ArcFindFormatter.formatAngle(BigDecimal("57.29577951308232"), 6)
        assertEquals("57.295780°", angStr)
    }

    // 38. Trailing-zero removal: 10.000000 -> 10, 0.125000 -> 0.125
    @Test
    fun test38_trailingZeroRemoval() {
        val stripped1 = ArcFindFormatter.removeTrailingZeros(BigDecimal("10.000000"))
        assertEquals("10", stripped1)

        val stripped2 = ArcFindFormatter.removeTrailingZeros(BigDecimal("0.125000"))
        assertEquals("0.125", stripped2)

        val stripped3 = ArcFindFormatter.removeTrailingZeros(BigDecimal("10.47197600"))
        assertEquals("10.471976", stripped3)

        val formatted1 = ArcFindFormatter.formatNumber(BigDecimal("10"), 6)
        assertEquals("10", formatted1)

        val formatted2 = ArcFindFormatter.formatNumber(BigDecimal("0.125"), 6)
        assertEquals("0.125", formatted2)
    }

    // 39. HALF_UP: 2.5 -> 3, 2.55 (scale 1) -> 2.6
    @Test
    fun test39_halfUp() {
        val formatted = ArcFindFormatter.formatNumber(BigDecimal("2.55"), 1, RoundingMode.HALF_UP)
        assertEquals("2.6", formatted)
    }

    // 40. HALF_DOWN: 2.55 (scale 1) -> 2.5
    @Test
    fun test40_halfDown() {
        val formatted = ArcFindFormatter.formatNumber(BigDecimal("2.55"), 1, RoundingMode.HALF_DOWN)
        assertEquals("2.5", formatted)
    }

    // 41. HALF_EVEN: 2.5 -> 2, 3.5 -> 4
    @Test
    fun test41_halfEven() {
        val val1 = BigDecimal("2.5").setScale(0, RoundingMode.HALF_EVEN)
        assertEquals(BigDecimal("2"), val1)
        val val2 = BigDecimal("3.5").setScale(0, RoundingMode.HALF_EVEN)
        assertEquals(BigDecimal("4"), val2)
    }

    // 42. UP: 2.1 (scale 0) -> 3
    @Test
    fun test42_up() {
        val formatted = ArcFindFormatter.formatNumber(BigDecimal("2.1"), 0, RoundingMode.UP)
        assertEquals("3", formatted)
    }

    // 43. DOWN: 2.9 (scale 0) -> 2
    @Test
    fun test43_down() {
        val formatted = ArcFindFormatter.formatNumber(BigDecimal("2.9"), 0, RoundingMode.DOWN)
        assertEquals("2", formatted)
    }

    // 44. CEILING: -2.9 (scale 0) -> -2
    @Test
    fun test44_ceiling() {
        val formatted = ArcFindFormatter.formatNumber(BigDecimal("-2.9"), 0, RoundingMode.CEILING)
        assertEquals("-2", formatted)
    }

    // 45. FLOOR: 2.9 (scale 0) -> 2, -2.1 (scale 0) -> -3
    @Test
    fun test45_floor() {
        val formatted1 = ArcFindFormatter.formatNumber(BigDecimal("2.9"), 0, RoundingMode.FLOOR)
        assertEquals("2", formatted1)
        val formatted2 = ArcFindFormatter.formatNumber(BigDecimal("-2.1"), 0, RoundingMode.FLOOR)
        assertEquals("-3", formatted2)
    }

    // 46. Arc percentage: 90° -> 25%, 180° -> 50%, 270° -> 75%, 360° -> 100%
    @Test
    fun test46_arcPercentage() {
        assertEquals("25%", ArcFindFormatter.formatPercentage(ArcCalculator.calculateArcPercentage(BigDecimal("90")), 2))
        assertEquals("50%", ArcFindFormatter.formatPercentage(ArcCalculator.calculateArcPercentage(BigDecimal("180")), 2))
        assertEquals("75%", ArcFindFormatter.formatPercentage(ArcCalculator.calculateArcPercentage(BigDecimal("270")), 2))
        assertEquals("100%", ArcFindFormatter.formatPercentage(ArcCalculator.calculateArcPercentage(BigDecimal("360")), 2))
    }

    // 47. Diameter: d = 2r -> radius 10 -> diameter 20
    @Test
    fun test47_diameter() {
        val diameter = ArcCalculator.calculateDiameter(BigDecimal("10"))
        assertEquals(0, BigDecimal("20").compareTo(diameter))
    }

    // 48. Circumference: C = 2πr -> radius 10 -> 62.831853 cm
    @Test
    fun test48_circumference() {
        val circ = ArcCalculator.calculateCircumference(BigDecimal("10"))
        val formatted = ArcFindFormatter.formatLength(circ, LengthUnit.CENTIMETER, 6, RoundingMode.HALF_UP)
        assertEquals("62.831853 cm", formatted)
    }

    // 49. History save: save one calculation -> one history entry
    @Test
    fun test49_historySave() {
        val prefs = FakeSharedPreferences()
        val repo = HistoryRepository(prefs)
        val entry = ArcFindHistory(
            id = "test-1",
            solveType = SolveType.FIND_ARC_LENGTH,
            radius = "10",
            radiusUnit = LengthUnit.CENTIMETER,
            angle = "60",
            arcLength = "10.471976",
            arcLengthUnit = LengthUnit.CENTIMETER,
            resultUnit = LengthUnit.CENTIMETER,
            resultValue = "10.471976 cm",
            savedAt = System.currentTimeMillis()
        )
        val saved = repo.saveCalculation(entry)
        assertTrue(saved)
        assertEquals(1, repo.getHistory().size)
    }

    // 50. History restore: tap saved calculation -> all fields match
    @Test
    fun test50_historyRestore() {
        val prefs = FakeSharedPreferences()
        val repo = HistoryRepository(prefs)
        val entry = ArcFindHistory(
            id = "test-restore",
            solveType = SolveType.FIND_RADIUS,
            radius = "9.549297",
            radiusUnit = LengthUnit.CENTIMETER,
            angle = "60",
            arcLength = "10",
            arcLengthUnit = LengthUnit.CENTIMETER,
            resultUnit = LengthUnit.CENTIMETER,
            resultValue = "9.549297 cm",
            savedAt = System.currentTimeMillis()
        )
        repo.saveCalculation(entry)
        val restored = repo.getHistory().first()
        assertEquals(SolveType.FIND_RADIUS, restored.solveType)
        assertEquals("10", restored.arcLength)
        assertEquals("60", restored.angle)
    }

    // 51. History limit: save 11 entries -> maximum 10 retained, oldest removed
    @Test
    fun test51_historyLimit() {
        val prefs = FakeSharedPreferences()
        val repo = HistoryRepository(prefs)
        for (i in 1..11) {
            val entry = ArcFindHistory(
                id = "test-$i",
                solveType = SolveType.FIND_ARC_LENGTH,
                radius = "$i",
                radiusUnit = LengthUnit.CENTIMETER,
                angle = "60",
                arcLength = "10",
                arcLengthUnit = LengthUnit.CENTIMETER,
                resultUnit = LengthUnit.CENTIMETER,
                resultValue = "$i cm",
                savedAt = System.currentTimeMillis() + i
            )
            repo.saveCalculation(entry)
        }
        assertEquals(10, repo.getHistory().size)
        // Entry 11 (newest) is first, entry 2 is last (entry 1 was removed)
        assertEquals("test-11", repo.getHistory().first().id)
        assertEquals("test-2", repo.getHistory().last().id)
    }

    // 52. History deletion: delete entry by id
    @Test
    fun test52_historyDeletion() {
        val prefs = FakeSharedPreferences()
        val repo = HistoryRepository(prefs)
        val entry = ArcFindHistory(
            id = "to-delete",
            solveType = SolveType.FIND_ARC_LENGTH,
            radius = "10",
            radiusUnit = LengthUnit.CENTIMETER,
            angle = "60",
            arcLength = "10",
            arcLengthUnit = LengthUnit.CENTIMETER,
            resultUnit = LengthUnit.CENTIMETER,
            resultValue = "10.47 cm",
            savedAt = System.currentTimeMillis()
        )
        repo.saveCalculation(entry)
        assertEquals(1, repo.getHistory().size)
        repo.deleteEntry("to-delete")
        assertEquals(0, repo.getHistory().size)
    }

    // 53. Duplicate history: saving identical calculation consecutively produces no duplicate
    @Test
    fun test53_duplicateHistory() {
        val prefs = FakeSharedPreferences()
        val repo = HistoryRepository(prefs)
        val entry = ArcFindHistory(
            id = "dup-1",
            solveType = SolveType.FIND_ARC_LENGTH,
            radius = "10",
            radiusUnit = LengthUnit.CENTIMETER,
            angle = "60",
            arcLength = "10",
            arcLengthUnit = LengthUnit.CENTIMETER,
            resultUnit = LengthUnit.CENTIMETER,
            resultValue = "10.47 cm",
            savedAt = System.currentTimeMillis()
        )
        val firstSave = repo.saveCalculation(entry)
        assertTrue(firstSave)
        assertEquals(1, repo.getHistory().size)

        // Consecutive duplicate save
        val secondEntry = entry.copy(id = "dup-2", savedAt = System.currentTimeMillis() + 100)
        val secondSave = repo.saveCalculation(secondEntry)
        assertFalse(secondSave)
        assertEquals(1, repo.getHistory().size)
    }

    // 54. Settings persistence: change dark mode and decimal places, restart/reload -> persists
    @Test
    fun test54_settingsPersistence() {
        val prefs = FakeSharedPreferences()
        val repo1 = SettingsRepository(prefs)
        val customSettings = AppSettings(
            theme = AppTheme.DARK,
            decimalPlaces = 4,
            roundingMode = RoundingMode.HALF_DOWN
        )
        repo1.updateSettings(customSettings)

        // Reload from same SharedPreferences
        val repo2 = SettingsRepository(prefs)
        val loaded = repo2.getSettings()
        assertEquals(AppTheme.DARK, loaded.theme)
        assertEquals(4, loaded.decimalPlaces)
        assertEquals(RoundingMode.HALF_DOWN, loaded.roundingMode)
    }

    // 55. Reset: default solve mode = FIND_ARC_LENGTH, radius = 10, angle = 60
    @Test
    fun test55_reset() {
        val prefs = FakeSharedPreferences()
        val settingsRepo = SettingsRepository(prefs)
        val historyRepo = HistoryRepository(prefs)

        // Add history item
        historyRepo.saveCalculation(
            ArcFindHistory(
                id = "keep-me",
                solveType = SolveType.FIND_ANGLE,
                radius = "5",
                radiusUnit = LengthUnit.METER,
                angle = "90",
                arcLength = "7",
                arcLengthUnit = LengthUnit.METER,
                resultUnit = LengthUnit.METER,
                resultValue = "90°",
                savedAt = System.currentTimeMillis()
            )
        )

        val viewModel = com.example.arcfind.ui.ArcFindViewModel(settingsRepo, historyRepo)
        // Switch to FIND_ANGLE
        viewModel.onSolveTypeSelected(SolveType.FIND_ANGLE)
        assertEquals(SolveType.FIND_ANGLE, viewModel.uiState.value.solveType)

        // Call reset
        viewModel.onResetAll()

        val state = viewModel.uiState.value
        assertEquals(SolveType.FIND_ARC_LENGTH, state.solveType)
        assertEquals("10", state.radiusInput)
        assertEquals("60", state.angleInput)
        assertNotNull(state.result)
        // History must remain intact
        assertEquals(1, state.history.size)
    }

    // 56. Invalid number rejection: "abc", "12..5", "1.2.3", "--"
    @Test
    fun test56_invalidNumber() {
        assertTrue(ArcFindParser.parseNumber("abc").isFailure)
        assertTrue(ArcFindParser.parseNumber("12..5").isFailure)
        assertTrue(ArcFindParser.parseNumber("1.2.3").isFailure)
        assertTrue(ArcFindParser.parseNumber("--").isFailure)
    }

    // 57. Empty input rejection
    @Test
    fun test57_emptyInput() {
        val err1 = ArcFindValidator.validateRawInput("")
        assertEquals(ArcFindError.EmptyInput, err1)
        val err2 = ArcFindValidator.validateRawInput("   ")
        assertEquals(ArcFindError.EmptyInput, err2)
    }

    // 58. Maximum input length: > 500 digits -> InputTooLong
    @Test
    fun test58_maximumInputLength() {
        val longString = "9".repeat(501)
        val err = ArcFindValidator.validateRawInput(longString)
        assertEquals(ArcFindError.InputTooLong, err)
        assertEquals("Number is too long. Maximum 500 digits.", ArcFindValidator.getErrorMessage(err!!))

        val parseRes = ArcFindParser.parseNumber(longString)
        assertTrue(parseRes.isFailure)
    }

    // Mathematical utility check: Newton-Raphson BigDecimal sqrt
    @Test
    fun test_bigDecimalSqrt() {
        val val4 = BigDecimalMath.sqrt(BigDecimal("4"))
        assertEquals(0, BigDecimal("2").compareTo(val4))

        val val2 = BigDecimalMath.sqrt(BigDecimal("2"))
        val val2Squared = val2.multiply(val2, MathConstants.MATH_CONTEXT)
        assertTrue(val2Squared.subtract(BigDecimal("2")).abs() < BigDecimal("0.0000000000000000000000000000001"))

        assertEquals(BigDecimal.ZERO, BigDecimalMath.sqrt(BigDecimal.ZERO))
    }
}
