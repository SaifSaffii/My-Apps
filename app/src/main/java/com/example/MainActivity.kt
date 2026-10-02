package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.AgeInputUnit
import com.example.ui.AgeConverterViewModel
import com.example.ui.components.AddProfileDialog
import com.example.ui.components.AppHeader
import com.example.ui.components.DatePickerModal
import com.example.ui.tabs.MilestonesTab
import com.example.ui.tabs.PetAgeTab
import com.example.ui.tabs.PlanetsTab
import com.example.ui.tabs.QuickConvertTab
import com.example.ui.tabs.TimeUnitsTab
import com.example.ui.theme.MyApplicationTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AgeConverterApp()
            }
        }
    }
}

@Composable
fun AgeConverterApp(
    viewModel: AgeConverterViewModel = viewModel()
) {
    val profiles by viewModel.profiles.collectAsStateWithLifecycle()
    val activeProfileId by viewModel.activeProfileId.collectAsStateWithLifecycle()
    val birthYear by viewModel.birthYear.collectAsStateWithLifecycle()
    val birthMonth by viewModel.birthMonth.collectAsStateWithLifecycle()
    val birthDay by viewModel.birthDay.collectAsStateWithLifecycle()
    val targetMillis by viewModel.targetDateMillis.collectAsStateWithLifecycle()
    val isCustomTarget by viewModel.isCustomTargetDate.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()

    val quickValue by viewModel.quickValue.collectAsStateWithLifecycle()
    val quickUnit by viewModel.quickUnit.collectAsStateWithLifecycle()

    val isPetToHumanMode by viewModel.isPetToHumanMode.collectAsStateWithLifecycle()
    val selectedPetType by viewModel.selectedPetType.collectAsStateWithLifecycle()
    val petAgeInput by viewModel.petAgeInput.collectAsStateWithLifecycle()

    var showBirthDatePicker by remember { mutableStateOf(false) }
    var showAddProfileDialog by remember { mutableStateOf(false) }

    // Computations
    val timeBreakdown = viewModel.getTimeBreakdown()
    val petAges = viewModel.getPetAges()
    val planetAges = viewModel.getPlanetaryAges()
    val milestones = viewModel.getMilestones()
    val zodiacData = viewModel.getZodiacData()
    val quickBreakdown = viewModel.getQuickTimeBreakdown()
    val petToHumanAge = viewModel.getPetToHumanConvertedAge()

    // BackHandler: Return to TimeUnitsTab if on any secondary tab
    BackHandler(enabled = selectedTab != 0) {
        viewModel.setSelectedTab(0)
    }

    val shareSummary = remember(timeBreakdown, zodiacData, birthYear, birthMonth, birthDay) {
        "⏳ Age Summary: I am ${timeBreakdown.years} years, ${timeBreakdown.months} months, and ${timeBreakdown.days} days old!\n" +
        "• Total Days Lived: ${timeBreakdown.totalDays} days\n" +
        "• Total Hours: ${timeBreakdown.totalHours} hours\n" +
        "• Heartbeats: ~${timeBreakdown.heartbeats} beats\n" +
        "• Next Birthday: ${zodiacData.nextBirthdayDateString} (${zodiacData.daysUntilNextBirthday} days away)\n" +
        "• Western Zodiac: ${zodiacData.westernSign} ${zodiacData.westernSymbol}\n" +
        "• Chinese Zodiac: ${zodiacData.chineseAnimal}\n" +
        "Calculated with Age Converter"
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppHeader(
                profiles = profiles,
                activeProfileId = activeProfileId,
                birthYear = birthYear,
                birthMonth = birthMonth,
                birthDay = birthDay,
                onSelectProfile = { viewModel.selectProfile(it) },
                onAddProfileClick = { showAddProfileDialog = true },
                onDeleteProfile = { viewModel.deleteProfile(it) },
                onPickBirthDateClick = { showBirthDatePicker = true },
                onAdjustYear = { viewModel.adjustYear(it) },
                shareSummaryText = shareSummary
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("bottom_nav_bar")
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { viewModel.setSelectedTab(0) },
                    icon = { Icon(Icons.Default.AccessTime, contentDescription = "Units") },
                    label = { Text("Units") },
                    modifier = Modifier.testTag("nav_item_units")
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { viewModel.setSelectedTab(1) },
                    icon = { Icon(Icons.Default.Pets, contentDescription = "Pets") },
                    label = { Text("Pets") },
                    modifier = Modifier.testTag("nav_item_pets")
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { viewModel.setSelectedTab(2) },
                    icon = { Icon(Icons.Default.Public, contentDescription = "Planets") },
                    label = { Text("Planets") },
                    modifier = Modifier.testTag("nav_item_planets")
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { viewModel.setSelectedTab(3) },
                    icon = { Icon(Icons.Default.Celebration, contentDescription = "Milestones") },
                    label = { Text("Milestones") },
                    modifier = Modifier.testTag("nav_item_milestones")
                )
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { viewModel.setSelectedTab(4) },
                    icon = { Icon(Icons.Default.Calculate, contentDescription = "Quick") },
                    label = { Text("Quick") },
                    modifier = Modifier.testTag("nav_item_quick")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> TimeUnitsTab(
                    breakdown = timeBreakdown,
                    targetMillis = targetMillis,
                    isCustomTarget = isCustomTarget,
                    onSetTargetDate = { viewModel.setTargetDate(it) },
                    onResetTargetDate = { viewModel.resetTargetDateToNow() }
                )
                1 -> PetAgeTab(
                    petAges = petAges,
                    humanAgeYears = timeBreakdown.totalYearsFraction,
                    isPetToHumanMode = isPetToHumanMode,
                    onToggleMode = { viewModel.setPetToHumanMode(it) },
                    selectedPetType = selectedPetType,
                    onSelectPetType = { viewModel.setSelectedPetType(it) },
                    petAgeInput = petAgeInput,
                    onPetAgeInputChange = { viewModel.setPetAgeInput(it) },
                    convertedPetToHumanAge = petToHumanAge
                )
                2 -> PlanetsTab(
                    planetAges = planetAges,
                    totalEarthDays = timeBreakdown.totalDays
                )
                3 -> MilestonesTab(
                    zodiacData = zodiacData,
                    milestones = milestones
                )
                4 -> QuickConvertTab(
                    quickValue = quickValue,
                    quickUnit = quickUnit,
                    onValueChange = { viewModel.setQuickValue(it) },
                    onUnitChange = { viewModel.setQuickUnit(it) },
                    breakdown = quickBreakdown
                )
            }
        }
    }

    // Dialogs
    if (showBirthDatePicker) {
        DatePickerModal(
            initialYear = birthYear,
            initialMonth = birthMonth,
            initialDay = birthDay,
            title = "Set Date of Birth",
            onDateSelected = { y, m, d ->
                viewModel.setBirthDate(y, m, d)
            },
            onDismiss = { showBirthDatePicker = false }
        )
    }

    if (showAddProfileDialog) {
        AddProfileDialog(
            onDismiss = { showAddProfileDialog = false },
            onSave = { name, y, m, d, emoji, isPet, petType ->
                viewModel.addProfile(name, y, m, d, emoji, isPet, petType)
            }
        )
    }
}
