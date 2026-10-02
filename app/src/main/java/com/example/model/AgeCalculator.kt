package com.example.model

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

object AgeCalculator {

    private val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    private val dayOfWeekFormat = SimpleDateFormat("EEEE", Locale.getDefault())

    fun calculateTimeBreakdown(
        birthYear: Int,
        birthMonth: Int, // 1..12
        birthDay: Int,
        targetMillis: Long = System.currentTimeMillis()
    ): TimeBreakdown {
        val birthCal = Calendar.getInstance().apply {
            set(birthYear, birthMonth - 1, birthDay, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val targetCal = Calendar.getInstance().apply {
            timeInMillis = targetMillis
        }

        val diffMillis = max(0L, targetCal.timeInMillis - birthCal.timeInMillis)
        val totalSeconds = diffMillis / 1000L
        val totalMinutes = totalSeconds / 60L
        val totalHours = totalMinutes / 60L
        val totalDays = totalHours / 24L
        val totalWeeks = totalDays / 7L

        // Exact Years, Months, Days calculation
        var years = targetCal.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR)
        var months = targetCal.get(Calendar.MONTH) - birthCal.get(Calendar.MONTH)
        var days = targetCal.get(Calendar.DAY_OF_MONTH) - birthCal.get(Calendar.DAY_OF_MONTH)

        if (days < 0) {
            months -= 1
            val tempCal = Calendar.getInstance().apply {
                set(Calendar.YEAR, targetCal.get(Calendar.YEAR))
                set(Calendar.MONTH, targetCal.get(Calendar.MONTH) - 1)
                set(Calendar.DAY_OF_MONTH, 1)
            }
            days += tempCal.getActualMaximum(Calendar.DAY_OF_MONTH)
        }

        if (months < 0) {
            years -= 1
            months += 12
        }

        if (years < 0) {
            years = 0
            months = 0
            days = 0
        }

        val totalMonths = years * 12L + months
        val totalYearsFraction = totalDays / 365.25

        // Biological stats
        val heartbeats = totalMinutes * 72L // 72 bpm avg
        val breaths = totalMinutes * 16L // 16 breaths/min avg
        val sleepHours = totalDays * 8L // 8 hrs sleep/day
        val sleepYears = sleepHours / (365.25 * 24.0)
        val solarDistanceKm = totalYearsFraction * 940_000_000.0 // Earth orbits ~940 million km/year

        return TimeBreakdown(
            years = years,
            months = months,
            days = days,
            totalYearsFraction = totalYearsFraction,
            totalMonths = totalMonths,
            totalWeeks = totalWeeks,
            totalDays = totalDays,
            totalHours = totalHours,
            totalMinutes = totalMinutes,
            totalSeconds = totalSeconds,
            heartbeats = heartbeats,
            breaths = breaths,
            sleepYears = sleepYears,
            sleepHours = sleepHours,
            solarDistanceKm = solarDistanceKm,
            earthOrbits = totalYearsFraction
        )
    }

    /**
     * Convert age from direct numeric input
     */
    fun convertFromNumeric(value: Double, unit: AgeInputUnit): TimeBreakdown {
        val totalDays = when (unit) {
            AgeInputUnit.YEARS -> (value * 365.25).toLong()
            AgeInputUnit.MONTHS -> (value * 30.4375).toLong()
            AgeInputUnit.WEEKS -> (value * 7.0).toLong()
            AgeInputUnit.DAYS -> value.toLong()
            AgeInputUnit.HOURS -> (value / 24.0).toLong()
        }

        val totalYearsFraction = totalDays / 365.25
        val years = totalYearsFraction.toInt()
        val remainingDaysAfterYears = totalDays - (years * 365.25).toLong()
        val months = (remainingDaysAfterYears / 30.4375).toInt().coerceIn(0, 11)
        val days = (remainingDaysAfterYears - (months * 30.4375).toLong()).toInt().coerceAtLeast(0)

        val totalSeconds = totalDays * 86400L
        val totalMinutes = totalDays * 1440L
        val totalHours = totalDays * 24L
        val totalWeeks = totalDays / 7L
        val totalMonths = (totalYearsFraction * 12.0).toLong()

        val heartbeats = totalMinutes * 72L
        val breaths = totalMinutes * 16L
        val sleepHours = totalDays * 8L
        val sleepYears = sleepHours / (365.25 * 24.0)
        val solarDistanceKm = totalYearsFraction * 940_000_000.0

        return TimeBreakdown(
            years = max(0, years),
            months = max(0, months),
            days = max(0, days),
            totalYearsFraction = max(0.0, totalYearsFraction),
            totalMonths = max(0L, totalMonths),
            totalWeeks = max(0L, totalWeeks),
            totalDays = max(0L, totalDays),
            totalHours = max(0L, totalHours),
            totalMinutes = max(0L, totalMinutes),
            totalSeconds = max(0L, totalSeconds),
            heartbeats = max(0L, heartbeats),
            breaths = max(0L, breaths),
            sleepYears = max(0.0, sleepYears),
            sleepHours = max(0L, sleepHours),
            solarDistanceKm = max(0.0, solarDistanceKm),
            earthOrbits = max(0.0, totalYearsFraction)
        )
    }

    /**
     * Calculate Pet Ages:
     * - Returns pet equivalent age if human years are provided
     * - Can also calculate human equivalent if pet age is entered
     */
    fun calculatePetAges(humanAgeYears: Double): List<PetAge> {
        val age = max(0.0, humanAgeYears)

        // Dog Small (<20 lbs)
        val dogSmallAge = if (age <= 0.0) 0.0 else when {
            age <= 1.0 -> age * 15.0
            age <= 2.0 -> 15.0 + (age - 1.0) * 9.0
            else -> 24.0 + (age - 2.0) * 4.0
        }
        val dogSmallStage = when {
            age < 1.0 -> "Puppy"
            age < 3.0 -> "Young Adult"
            age < 10.0 -> "Adult"
            age < 14.0 -> "Senior"
            else -> "Super Senior"
        }

        // Dog Medium (20-50 lbs)
        val dogMediumAge = if (age <= 0.0) 0.0 else when {
            age <= 1.0 -> age * 15.0
            age <= 2.0 -> 15.0 + (age - 1.0) * 9.0
            else -> 24.0 + (age - 2.0) * 5.0
        }
        val dogMediumStage = when {
            age < 1.0 -> "Puppy"
            age < 3.0 -> "Young Adult"
            age < 8.0 -> "Adult"
            age < 12.0 -> "Senior"
            else -> "Super Senior"
        }

        // Dog Large (>50 lbs)
        val dogLargeAge = if (age <= 0.0) 0.0 else when {
            age <= 1.0 -> age * 14.0
            age <= 2.0 -> 14.0 + (age - 1.0) * 8.0
            else -> 22.0 + (age - 2.0) * 7.0
        }
        val dogLargeStage = when {
            age < 1.0 -> "Puppy"
            age < 2.5 -> "Young Adult"
            age < 7.0 -> "Adult"
            age < 10.0 -> "Senior"
            else -> "Super Senior"
        }

        // Cat
        val catAge = if (age <= 0.0) 0.0 else when {
            age <= 1.0 -> age * 15.0
            age <= 2.0 -> 15.0 + (age - 1.0) * 9.0
            else -> 24.0 + (age - 2.0) * 4.0
        }
        val catStage = when {
            age < 1.0 -> "Kitten"
            age < 3.0 -> "Junior"
            age < 6.0 -> "Prime Adult"
            age < 10.0 -> "Mature"
            age < 14.0 -> "Senior"
            else -> "Super Senior"
        }

        // Horse
        val horseAge = if (age <= 0.0) 0.0 else when {
            age <= 1.0 -> age * 6.5
            age <= 2.0 -> 6.5 + (age - 1.0) * 6.5
            age <= 3.0 -> 13.0 + (age - 2.0) * 5.0
            else -> 18.0 + (age - 3.0) * 2.5
        }
        val horseStage = when {
            age < 3.0 -> "Foal / Colt"
            age < 7.0 -> "Young Horse"
            age < 18.0 -> "Prime Horse"
            else -> "Senior"
        }

        // Rabbit
        val rabbitAge = if (age <= 0.0) 0.0 else when {
            age <= 0.5 -> age * 32.0 // 6 mos = 16 human yrs
            age <= 1.0 -> 16.0 + (age - 0.5) * 10.0 // 1 yr = 21 human yrs
            else -> 21.0 + (age - 1.0) * 6.0
        }
        val rabbitStage = when {
            age < 1.0 -> "Kit / Juvenile"
            age < 5.0 -> "Adult"
            age < 8.0 -> "Senior"
            else -> "Geriatric"
        }

        // Hamster (Lifespan ~2-3 yrs)
        val hamsterAge = age * 26.0
        val hamsterStage = when {
            age < 0.25 -> "Pup"
            age < 1.5 -> "Adult"
            else -> "Senior"
        }

        // Bird (Parrot / Cockatiel - long lived)
        val birdAge = age * 1.2
        val birdStage = when {
            age < 2.0 -> "Fledgling"
            age < 15.0 -> "Adult"
            else -> "Senior Companion"
        }

        // Elephant (Lifespan ~70 yrs)
        val elephantAge = age * 0.95
        val elephantStage = when {
            age < 5.0 -> "Calf"
            age < 15.0 -> "Juvenile"
            age < 45.0 -> "Prime Adult"
            else -> "Matriarch / Bull"
        }

        // Reverse equivalent (If human age entered is interpreted as pet age -> what is human equivalent):
        return listOf(
            PetAge(
                id = "dog_small",
                speciesName = "Small Dog (<20 lbs)",
                emoji = "🐕",
                equivalentHumanAge = dogSmallAge,
                petAgeIfHumanEntered = dogSmallAge,
                lifeStage = dogSmallStage,
                lifeExpectancy = "14–16 years",
                explanation = "1st year = 15 yrs, 2nd = 9 yrs, then +4 yrs per year"
            ),
            PetAge(
                id = "dog_medium",
                speciesName = "Medium Dog (20–50 lbs)",
                emoji = "🐕‍🦺",
                equivalentHumanAge = dogMediumAge,
                petAgeIfHumanEntered = dogMediumAge,
                lifeStage = dogMediumStage,
                lifeExpectancy = "11–13 years",
                explanation = "1st year = 15 yrs, 2nd = 9 yrs, then +5 yrs per year"
            ),
            PetAge(
                id = "dog_large",
                speciesName = "Large Dog (>50 lbs)",
                emoji = "🦮",
                equivalentHumanAge = dogLargeAge,
                petAgeIfHumanEntered = dogLargeAge,
                lifeStage = dogLargeStage,
                lifeExpectancy = "8–11 years",
                explanation = "Large breeds age faster: +7 yrs per year after year 2"
            ),
            PetAge(
                id = "cat",
                speciesName = "Cat",
                emoji = "🐈",
                equivalentHumanAge = catAge,
                petAgeIfHumanEntered = catAge,
                lifeStage = catStage,
                lifeExpectancy = "15–18 years",
                explanation = "1st year = 15 yrs, 2nd = 24 yrs, each subsequent year = +4 yrs"
            ),
            PetAge(
                id = "rabbit",
                speciesName = "Rabbit",
                emoji = "🐇",
                equivalentHumanAge = rabbitAge,
                petAgeIfHumanEntered = rabbitAge,
                lifeStage = rabbitStage,
                lifeExpectancy = "9–12 years",
                explanation = "Reaches human 21 in first year, then +6 yrs per year"
            ),
            PetAge(
                id = "horse",
                speciesName = "Horse",
                emoji = "🐎",
                equivalentHumanAge = horseAge,
                petAgeIfHumanEntered = horseAge,
                lifeStage = horseStage,
                lifeExpectancy = "25–30 years",
                explanation = "1st year = 6.5 yrs, 2nd = 13 yrs, 3rd = 18 yrs, then +2.5 yrs/yr"
            ),
            PetAge(
                id = "hamster",
                speciesName = "Hamster",
                emoji = "🐹",
                equivalentHumanAge = hamsterAge,
                petAgeIfHumanEntered = hamsterAge,
                lifeStage = hamsterStage,
                lifeExpectancy = "2–3 years",
                explanation = "Fast metabolism: 1 year ≈ 26 human years"
            ),
            PetAge(
                id = "bird",
                speciesName = "Parrot / Cockatiel",
                emoji = "🦜",
                equivalentHumanAge = birdAge,
                petAgeIfHumanEntered = birdAge,
                lifeStage = birdStage,
                lifeExpectancy = "40–60 years",
                explanation = "Long-lived birds age remarkably close to human rates"
            ),
            PetAge(
                id = "elephant",
                speciesName = "Elephant",
                emoji = "🐘",
                equivalentHumanAge = elephantAge,
                petAgeIfHumanEntered = elephantAge,
                lifeStage = elephantStage,
                lifeExpectancy = "60–70 years",
                explanation = "Similar lifespan and milestones to humans"
            )
        )
    }

    /**
     * Convert Pet's Age into Human equivalent
     */
    fun convertPetToHuman(petType: String, petAgeYears: Double): Double {
        val a = max(0.0, petAgeYears)
        return when (petType) {
            "DOG_SMALL" -> if (a <= 1.0) a * 15.0 else if (a <= 2.0) 15.0 + (a - 1.0) * 9.0 else 24.0 + (a - 2.0) * 4.0
            "DOG_MEDIUM" -> if (a <= 1.0) a * 15.0 else if (a <= 2.0) 15.0 + (a - 1.0) * 9.0 else 24.0 + (a - 2.0) * 5.0
            "DOG_LARGE" -> if (a <= 1.0) a * 14.0 else if (a <= 2.0) 14.0 + (a - 1.0) * 8.0 else 22.0 + (a - 2.0) * 7.0
            "CAT" -> if (a <= 1.0) a * 15.0 else if (a <= 2.0) 15.0 + (a - 1.0) * 9.0 else 24.0 + (a - 2.0) * 4.0
            "HORSE" -> if (a <= 1.0) a * 6.5 else if (a <= 2.0) 13.0 else if (a <= 3.0) 18.0 else 18.0 + (a - 3.0) * 2.5
            "RABBIT" -> if (a <= 1.0) a * 21.0 else 21.0 + (a - 1.0) * 6.0
            "HAMSTER" -> a * 26.0
            else -> a * 5.0
        }
    }

    /**
     * Calculate Planetary Ages:
     * Orbital periods relative to Earth's 365.25 days
     */
    fun calculatePlanetaryAges(earthAgeDays: Long): List<PlanetAge> {
        val days = max(0L, earthAgeDays).toDouble()

        data class PlanetSpec(
            val name: String,
            val emoji: String,
            val orbitalDays: Double,
            val summary: String,
            val colorHex: Long
        )

        val planets = listOf(
            PlanetSpec("Mercury", "☿", 87.97, "88 Earth days per orbit", 0xFF9E9E9E),
            PlanetSpec("Venus", "♀", 224.70, "225 Earth days per orbit", 0xFFE0A96D),
            PlanetSpec("Earth", "♁", 365.25, "365.25 Earth days per orbit", 0xFF4A90E2),
            PlanetSpec("Mars", "♂", 686.98, "1.88 Earth years per orbit", 0xFFE57373),
            PlanetSpec("Jupiter", "♃", 4332.59, "11.86 Earth years per orbit", 0xFFD4A373),
            PlanetSpec("Saturn", "♄", 10759.22, "29.45 Earth years per orbit", 0xFFF4D06F),
            PlanetSpec("Uranus", "♅", 30685.40, "84.02 Earth years per orbit", 0xFF80DEEA),
            PlanetSpec("Neptune", "♆", 60189.00, "164.79 Earth years per orbit", 0xFF5C6BC0),
            PlanetSpec("Pluto", "♇", 90560.00, "247.92 Earth years per orbit", 0xFFB0BEC5)
        )

        return planets.map { p ->
            val planetAge = if (p.orbitalDays > 0) days / p.orbitalDays else 0.0
            val currentOrbits = planetAge.toLong()
            val nextOrbitDays = (currentOrbits + 1) * p.orbitalDays
            val daysUntilNext = max(0L, (nextOrbitDays - days).toLong())

            PlanetAge(
                name = p.name,
                emoji = p.emoji,
                ageOnPlanet = planetAge,
                orbitalPeriodEarthDays = p.orbitalDays,
                orbitalSummary = p.summary,
                daysUntilNextBirthday = daysUntilNext,
                colorHex = p.colorHex
            )
        }
    }

    /**
     * Calculate Milestone progress & dates
     */
    fun calculateMilestones(
        birthYear: Int,
        birthMonth: Int,
        birthDay: Int,
        currentDays: Long
    ): List<MilestoneItem> {
        val birthCal = Calendar.getInstance().apply {
            set(birthYear, birthMonth - 1, birthDay, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val milestoneTargets = listOf(
            Triple("1k_days", "1,000 Days on Earth", 1_000L),
            Triple("5k_days", "5,000 Days on Earth", 5_000L),
            Triple("10k_days", "10,000 Days Alive (~27.4 yrs)", 10_000L),
            Triple("1b_sec", "1 Billion Seconds (~31.7 yrs)", (1_000_000_000L / 86400L)),
            Triple("15k_days", "15,000 Days on Earth (~41.1 yrs)", 15_000L),
            Triple("20k_days", "20,000 Days on Earth (~54.8 yrs)", 20_000L),
            Triple("2b_sec", "2 Billion Seconds (~63.4 yrs)", (2_000_000_000L / 86400L)),
            Triple("25k_days", "25,000 Days on Earth (~68.5 yrs)", 25_000L),
            Triple("30k_days", "30,000 Days on Earth (~82.1 yrs)", 30_000L)
        )

        return milestoneTargets.map { (id, title, targetDays) ->
            val targetCal = (birthCal.clone() as Calendar).apply {
                add(Calendar.DAY_OF_YEAR, targetDays.toInt())
            }
            val targetDateStr = dateFormat.format(targetCal.time)
            val isAchieved = currentDays >= targetDays
            val daysDiff = abs(targetDays - currentDays)
            val progress = min(1f, currentDays.toFloat() / targetDays.toFloat()).coerceAtLeast(0f)

            val subtitle = if (isAchieved) {
                "Reached on $targetDateStr ($daysDiff days ago)"
            } else {
                "Coming in $daysDiff days ($targetDateStr)"
            }

            MilestoneItem(
                id = id,
                title = title,
                subtitle = subtitle,
                targetDays = targetDays,
                targetDateString = targetDateStr,
                isAchieved = isAchieved,
                daysDifference = daysDiff,
                progress = progress
            )
        }
    }

    /**
     * Calculate Zodiac, Day of week, and Next Birthday
     */
    fun calculateZodiacAndBirthday(
        birthYear: Int,
        birthMonth: Int, // 1..12
        birthDay: Int,
        targetMillis: Long = System.currentTimeMillis()
    ): ZodiacData {
        val birthCal = Calendar.getInstance().apply {
            set(birthYear, birthMonth - 1, birthDay, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val dayOfWeekBorn = dayOfWeekFormat.format(birthCal.time)

        val targetCal = Calendar.getInstance().apply {
            timeInMillis = targetMillis
        }
        val currentYear = targetCal.get(Calendar.YEAR)

        // Next Birthday calculation
        val nextBirthdayCal = Calendar.getInstance().apply {
            set(currentYear, birthMonth - 1, birthDay, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // If today is past this year's birthday, next birthday is next year
        if (nextBirthdayCal.timeInMillis < targetCal.timeInMillis &&
            (nextBirthdayCal.get(Calendar.DAY_OF_YEAR) != targetCal.get(Calendar.DAY_OF_YEAR) ||
             nextBirthdayCal.get(Calendar.YEAR) != targetCal.get(Calendar.YEAR))) {
            nextBirthdayCal.add(Calendar.YEAR, 1)
        }

        val daysUntil = max(0, ((nextBirthdayCal.timeInMillis - targetCal.timeInMillis) / (1000L * 86400L)).toInt())
        val dayOfWeekNextBirthday = dayOfWeekFormat.format(nextBirthdayCal.time)
        val nextBirthdayStr = dateFormat.format(nextBirthdayCal.time)
        val turningAgeNext = nextBirthdayCal.get(Calendar.YEAR) - birthYear

        // Half-birthday (6 months from birthday)
        val halfBirthdayCal = (nextBirthdayCal.clone() as Calendar).apply {
            add(Calendar.MONTH, 6)
        }
        val halfBirthdayStr = dateFormat.format(halfBirthdayCal.time)

        // Western Zodiac Sign
        val (westernSign, symbol, element, dateRange, traits) = getWesternZodiac(birthMonth, birthDay)

        // Chinese Zodiac Animal
        val (chineseAnimal, chineseElement) = getChineseZodiac(birthYear)

        return ZodiacData(
            westernSign = westernSign,
            westernSymbol = symbol,
            westernElement = element,
            westernDateRange = dateRange,
            westernTraits = traits,
            chineseAnimal = chineseAnimal,
            chineseElement = chineseElement,
            dayOfWeekBorn = dayOfWeekBorn,
            nextBirthdayDateString = nextBirthdayStr,
            daysUntilNextBirthday = daysUntil,
            dayOfWeekNextBirthday = dayOfWeekNextBirthday,
            turningAgeNext = turningAgeNext,
            halfBirthdayDateString = halfBirthdayStr
        )
    }

    private fun getWesternZodiac(month: Int, day: Int): ZodiacSignInfo {
        return when (month) {
            1 -> if (day < 20) ZodiacSignInfo("Capricorn", "♑", "Earth", "Dec 22 – Jan 19", "Disciplined, ambitious, patient")
                 else ZodiacSignInfo("Aquarius", "♒", "Air", "Jan 20 – Feb 18", "Innovative, independent, humanitarian")
            2 -> if (day < 19) ZodiacSignInfo("Aquarius", "♒", "Air", "Jan 20 – Feb 18", "Innovative, independent, humanitarian")
                 else ZodiacSignInfo("Pisces", "♓", "Water", "Feb 19 – Mar 20", "Compassionate, artistic, intuitive")
            3 -> if (day < 21) ZodiacSignInfo("Pisces", "♓", "Water", "Feb 19 – Mar 20", "Compassionate, artistic, intuitive")
                 else ZodiacSignInfo("Aries", "♈", "Fire", "Mar 21 – Apr 19", "Courageous, energetic, pioneering")
            4 -> if (day < 20) ZodiacSignInfo("Aries", "♈", "Fire", "Mar 21 – Apr 19", "Courageous, energetic, pioneering")
                 else ZodiacSignInfo("Taurus", "♉", "Earth", "Apr 20 – May 20", "Reliable, patient, devoted")
            5 -> if (day < 21) ZodiacSignInfo("Taurus", "♉", "Earth", "Apr 20 – May 20", "Reliable, patient, devoted")
                 else ZodiacSignInfo("Gemini", "♊", "Air", "May 21 – Jun 20", "Adaptable, curious, expressive")
            6 -> if (day < 21) ZodiacSignInfo("Gemini", "♊", "Air", "May 21 – Jun 20", "Adaptable, curious, expressive")
                 else ZodiacSignInfo("Cancer", "♋", "Water", "Jun 21 – Jul 22", "Intuitive, sentimental, loyal")
            7 -> if (day < 23) ZodiacSignInfo("Cancer", "♋", "Water", "Jun 21 – Jul 22", "Intuitive, sentimental, loyal")
                 else ZodiacSignInfo("Leo", "♌", "Fire", "Jul 23 – Aug 22", "Generous, warm-hearted, charismatic")
            8 -> if (day < 23) ZodiacSignInfo("Leo", "♌", "Fire", "Jul 23 – Aug 22", "Generous, warm-hearted, charismatic")
                 else ZodiacSignInfo("Virgo", "♍", "Earth", "Aug 23 – Sep 22", "Loyal, analytical, practical")
            9 -> if (day < 23) ZodiacSignInfo("Virgo", "♍", "Earth", "Aug 23 – Sep 22", "Loyal, analytical, practical")
                 else ZodiacSignInfo("Libra", "♎", "Air", "Sep 23 – Oct 22", "Harmonious, fair, diplomatic")
            10 -> if (day < 23) ZodiacSignInfo("Libra", "♎", "Air", "Sep 23 – Oct 22", "Harmonious, fair, diplomatic")
                  else ZodiacSignInfo("Scorpio", "♏", "Water", "Oct 23 – Nov 21", "Passionate, resourceful, brave")
            11 -> if (day < 22) ZodiacSignInfo("Scorpio", "♏", "Water", "Oct 23 – Nov 21", "Passionate, resourceful, brave")
                  else ZodiacSignInfo("Sagittarius", "♐", "Fire", "Nov 22 – Dec 21", "Optimistic, philosophical, adventurous")
            else -> if (day < 22) ZodiacSignInfo("Sagittarius", "♐", "Fire", "Nov 22 – Dec 21", "Optimistic, philosophical, adventurous")
                    else ZodiacSignInfo("Capricorn", "♑", "Earth", "Dec 22 – Jan 19", "Disciplined, ambitious, patient")
        }
    }

    private fun getChineseZodiac(year: Int): Pair<String, String> {
        val animals = arrayOf(
            "Rat 🐀", "Ox 🐂", "Tiger 🐅", "Rabbit 🐇", "Dragon 🐉", "Snake 🐍",
            "Horse 🐎", "Goat 🐐", "Monkey 🐒", "Rooster 🐓", "Dog 🐕", "Pig 🐖"
        )
        val elements = arrayOf("Metal", "Water", "Wood", "Fire", "Earth")

        val animalIndex = (year - 4) % 12
        val safeAnimalIndex = if (animalIndex < 0) animalIndex + 12 else animalIndex

        val lastDigit = year % 10
        val safeLastDigit = if (lastDigit < 0) lastDigit + 10 else lastDigit
        val elementIndex = (safeLastDigit / 2) % 5

        return Pair(animals[safeAnimalIndex], elements[elementIndex])
    }

    private data class ZodiacSignInfo(
        val name: String,
        val symbol: String,
        val element: String,
        val dateRange: String,
        val traits: String
    )
}
