package com.example.model

data class TimeBreakdown(
    val years: Int,
    val months: Int,
    val days: Int,
    val totalYearsFraction: Double,
    val totalMonths: Long,
    val totalWeeks: Long,
    val totalDays: Long,
    val totalHours: Long,
    val totalMinutes: Long,
    val totalSeconds: Long,
    val heartbeats: Long,
    val breaths: Long,
    val sleepYears: Double,
    val sleepHours: Long,
    val solarDistanceKm: Double,
    val earthOrbits: Double
)

data class PetAge(
    val id: String,
    val speciesName: String,
    val emoji: String,
    val equivalentHumanAge: Double,
    val petAgeIfHumanEntered: Double,
    val lifeStage: String,
    val lifeExpectancy: String,
    val explanation: String
)

data class PlanetAge(
    val name: String,
    val emoji: String,
    val ageOnPlanet: Double,
    val orbitalPeriodEarthDays: Double,
    val orbitalSummary: String,
    val daysUntilNextBirthday: Long,
    val colorHex: Long
)

data class MilestoneItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val targetDays: Long,
    val targetDateString: String,
    val isAchieved: Boolean,
    val daysDifference: Long,
    val progress: Float
)

data class ZodiacData(
    val westernSign: String,
    val westernSymbol: String,
    val westernElement: String,
    val westernDateRange: String,
    val westernTraits: String,
    val chineseAnimal: String,
    val chineseElement: String,
    val dayOfWeekBorn: String,
    val nextBirthdayDateString: String,
    val daysUntilNextBirthday: Int,
    val dayOfWeekNextBirthday: String,
    val turningAgeNext: Int,
    val halfBirthdayDateString: String
)

enum class AgeInputUnit(val label: String, val shortUnit: String) {
    YEARS("Years", "yrs"),
    MONTHS("Months", "mo"),
    WEEKS("Weeks", "wks"),
    DAYS("Days", "days"),
    HOURS("Hours", "hrs")
}
