package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.UserProfile
import com.example.data.UserProfileRepository
import com.example.model.AgeCalculator
import com.example.model.AgeInputUnit
import com.example.model.MilestoneItem
import com.example.model.PetAge
import com.example.model.PlanetAge
import com.example.model.TimeBreakdown
import com.example.model.ZodiacData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class AgeConverterViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserProfileRepository

    val profiles: StateFlow<List<UserProfile>>

    // Input States
    private val _birthYear = MutableStateFlow(2000)
    val birthYear = _birthYear.asStateFlow()

    private val _birthMonth = MutableStateFlow(1) // 1..12
    val birthMonth = _birthMonth.asStateFlow()

    private val _birthDay = MutableStateFlow(1)
    val birthDay = _birthDay.asStateFlow()

    private val _targetDateMillis = MutableStateFlow(System.currentTimeMillis())
    val targetDateMillis = _targetDateMillis.asStateFlow()

    private val _isCustomTargetDate = MutableStateFlow(false)
    val isCustomTargetDate = _isCustomTargetDate.asStateFlow()

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab = _selectedTab.asStateFlow()

    private val _activeProfileId = MutableStateFlow<Int?>(null)
    val activeProfileId = _activeProfileId.asStateFlow()

    // Quick Numeric Converter States
    private val _quickValue = MutableStateFlow("25")
    val quickValue = _quickValue.asStateFlow()

    private val _quickUnit = MutableStateFlow(AgeInputUnit.YEARS)
    val quickUnit = _quickUnit.asStateFlow()

    // Pet Calculator States
    private val _isPetToHumanMode = MutableStateFlow(false)
    val isPetToHumanMode = _isPetToHumanMode.asStateFlow()

    private val _selectedPetType = MutableStateFlow("DOG_MEDIUM")
    val selectedPetType = _selectedPetType.asStateFlow()

    private val _petAgeInput = MutableStateFlow("3")
    val petAgeInput = _petAgeInput.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = UserProfileRepository(database.userProfileDao())
        profiles = repository.allProfiles.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    // Public Calculation Getters
    fun getTimeBreakdown(): TimeBreakdown {
        return AgeCalculator.calculateTimeBreakdown(
            birthYear = _birthYear.value,
            birthMonth = _birthMonth.value,
            birthDay = _birthDay.value,
            targetMillis = _targetDateMillis.value
        )
    }

    fun getPetAges(): List<PetAge> {
        val breakdown = getTimeBreakdown()
        return AgeCalculator.calculatePetAges(breakdown.totalYearsFraction)
    }

    fun getPlanetaryAges(): List<PlanetAge> {
        val breakdown = getTimeBreakdown()
        return AgeCalculator.calculatePlanetaryAges(breakdown.totalDays)
    }

    fun getMilestones(): List<MilestoneItem> {
        val breakdown = getTimeBreakdown()
        return AgeCalculator.calculateMilestones(
            birthYear = _birthYear.value,
            birthMonth = _birthMonth.value,
            birthDay = _birthDay.value,
            currentDays = breakdown.totalDays
        )
    }

    fun getZodiacData(): ZodiacData {
        return AgeCalculator.calculateZodiacAndBirthday(
            birthYear = _birthYear.value,
            birthMonth = _birthMonth.value,
            birthDay = _birthDay.value,
            targetMillis = _targetDateMillis.value
        )
    }

    fun getQuickTimeBreakdown(): TimeBreakdown {
        val numericVal = _quickValue.value.toDoubleOrNull() ?: 0.0
        return AgeCalculator.convertFromNumeric(numericVal, _quickUnit.value)
    }

    fun getPetToHumanConvertedAge(): Double {
        val age = _petAgeInput.value.toDoubleOrNull() ?: 0.0
        return AgeCalculator.convertPetToHuman(_selectedPetType.value, age)
    }

    // Actions
    fun setBirthDate(year: Int, month: Int, day: Int) {
        _birthYear.value = year
        _birthMonth.value = month
        _birthDay.value = day
    }

    fun setTargetDate(millis: Long) {
        _targetDateMillis.value = millis
        _isCustomTargetDate.value = true
    }

    fun resetTargetDateToNow() {
        _targetDateMillis.value = System.currentTimeMillis()
        _isCustomTargetDate.value = false
    }

    fun setSelectedTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }

    fun setQuickValue(value: String) {
        _quickValue.value = value.filter { it.isDigit() || it == '.' }
    }

    fun setQuickUnit(unit: AgeInputUnit) {
        _quickUnit.value = unit
    }

    fun setPetToHumanMode(enabled: Boolean) {
        _isPetToHumanMode.value = enabled
    }

    fun setSelectedPetType(type: String) {
        _selectedPetType.value = type
    }

    fun setPetAgeInput(input: String) {
        _petAgeInput.value = input.filter { it.isDigit() || it == '.' }
    }

    fun selectProfile(profile: UserProfile) {
        _activeProfileId.value = profile.id
        _birthYear.value = profile.birthYear
        _birthMonth.value = profile.birthMonth
        _birthDay.value = profile.birthDay
        if (profile.isPet) {
            _selectedTab.value = 1 // Switch to Pet Age tab
            _isPetToHumanMode.value = true
            _selectedPetType.value = profile.petType
            val age = AgeCalculator.calculateTimeBreakdown(
                profile.birthYear,
                profile.birthMonth,
                profile.birthDay
            ).totalYearsFraction
            _petAgeInput.value = String.format(java.util.Locale.US, "%.1f", age)
        }
    }

    fun addProfile(
        name: String,
        year: Int,
        month: Int,
        day: Int,
        emoji: String,
        isPet: Boolean,
        petType: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val newProfile = UserProfile(
                name = name.ifBlank { if (isPet) "Pet" else "Person" },
                birthYear = year,
                birthMonth = month,
                birthDay = day,
                emoji = emoji,
                isPet = isPet,
                petType = petType
            )
            val newId = repository.insert(newProfile)
            _activeProfileId.value = newId.toInt()
            setBirthDate(year, month, day)
        }
    }

    fun deleteProfile(profile: UserProfile) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(profile)
            if (_activeProfileId.value == profile.id) {
                _activeProfileId.value = null
            }
        }
    }

    fun adjustYear(offset: Int) {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val newYear = (_birthYear.value + offset).coerceIn(1900, currentYear)
        _birthYear.value = newYear
    }
}
