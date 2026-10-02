package com.example

import com.example.model.AgeCalculator
import com.example.model.AgeInputUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class ExampleUnitTest {

    @Test
    fun testTimeBreakdownCalculation() {
        val birthCal = Calendar.getInstance().apply {
            set(2000, Calendar.JANUARY, 1, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val targetCal = Calendar.getInstance().apply {
            set(2025, Calendar.JANUARY, 1, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val breakdown = AgeCalculator.calculateTimeBreakdown(
            birthYear = 2000,
            birthMonth = 1,
            birthDay = 1,
            targetMillis = targetCal.timeInMillis
        )

        assertEquals(25, breakdown.years)
        assertEquals(0, breakdown.months)
        assertEquals(0, breakdown.days)
        assertTrue(breakdown.totalDays > 9120) // 25 years with leap years
        assertTrue(breakdown.totalHours > 200_000)
        assertTrue(breakdown.heartbeats > 500_000_000)
        assertTrue(breakdown.sleepYears > 8.0)
    }

    @Test
    fun testPetAgeFormulas() {
        // Cat at 1 year is 15 human years, at 2 years is 24, at 3 years is 28
        val cat1 = AgeCalculator.convertPetToHuman("CAT", 1.0)
        assertEquals(15.0, cat1, 0.1)

        val cat2 = AgeCalculator.convertPetToHuman("CAT", 2.0)
        assertEquals(24.0, cat2, 0.1)

        val cat3 = AgeCalculator.convertPetToHuman("CAT", 3.0)
        assertEquals(28.0, cat3, 0.1)

        // Medium Dog at 1 year is 15, at 2 years is 24, at 3 years is 29
        val dogMed3 = AgeCalculator.convertPetToHuman("DOG_MEDIUM", 3.0)
        assertEquals(29.0, dogMed3, 0.1)
    }

    @Test
    fun testPlanetaryAges() {
        // 687 Earth days is ~1 Mars year
        val planetAges = AgeCalculator.calculatePlanetaryAges(687L)
        val mars = planetAges.first { it.name == "Mars" }
        assertEquals(1.0, mars.ageOnPlanet, 0.05)

        val mercury = planetAges.first { it.name == "Mercury" }
        assertTrue(mercury.ageOnPlanet > 7.0) // 687 / 88 ≈ 7.8
    }

    @Test
    fun testZodiacAndBirthday() {
        val zodiacLeo = AgeCalculator.calculateZodiacAndBirthday(
            birthYear = 1996,
            birthMonth = 8,
            birthDay = 10
        )
        assertEquals("Leo", zodiacLeo.westernSign)
        assertEquals("Fire", zodiacLeo.westernElement)
        assertTrue(zodiacLeo.chineseAnimal.contains("Rat"))

        val zodiacPisces = AgeCalculator.calculateZodiacAndBirthday(
            birthYear = 2000,
            birthMonth = 3,
            birthDay = 5
        )
        assertEquals("Pisces", zodiacPisces.westernSign)
        assertEquals("Water", zodiacPisces.westernElement)
    }

    @Test
    fun testNumericUnitConversion() {
        val breakdown = AgeCalculator.convertFromNumeric(10.0, AgeInputUnit.YEARS)
        assertEquals(10, breakdown.years)
        assertTrue(breakdown.totalMonths >= 120)
        assertTrue(breakdown.totalDays >= 3650)
    }
}
