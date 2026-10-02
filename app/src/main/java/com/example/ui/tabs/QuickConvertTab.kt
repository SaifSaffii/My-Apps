package com.example.ui.tabs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AgeCalculator
import com.example.model.AgeInputUnit
import com.example.model.TimeBreakdown
import com.example.ui.components.StatCard
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuickConvertTab(
    quickValue: String,
    quickUnit: AgeInputUnit,
    onValueChange: (String) -> Unit,
    onUnitChange: (AgeInputUnit) -> Unit,
    breakdown: TimeBreakdown,
    modifier: Modifier = Modifier
) {
    val numberFormat = remember { NumberFormat.getNumberInstance(Locale.US) }
    val numericVal = quickValue.toDoubleOrNull() ?: 0.0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Input Control Card
            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Instant Numeric Converter",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // Unit Selection Chips
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AgeInputUnit.values().forEach { unit ->
                            FilterChip(
                                selected = quickUnit == unit,
                                onClick = { onUnitChange(unit) },
                                label = { Text(unit.label) },
                                modifier = Modifier.testTag("quick_unit_${unit.name.lowercase()}")
                            )
                        }
                    }

                    // Numeric Input TextField
                    OutlinedTextField(
                        value = quickValue,
                        onValueChange = onValueChange,
                        label = { Text("Enter value in ${quickUnit.label}") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("quick_value_input")
                    )

                    // Quick presets
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Presets:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            val presets = when (quickUnit) {
                                AgeInputUnit.YEARS -> listOf("1", "18", "25", "50", "80")
                                AgeInputUnit.MONTHS -> listOf("6", "12", "24", "120", "300")
                                AgeInputUnit.WEEKS -> listOf("12", "40", "100", "500", "1000")
                                AgeInputUnit.DAYS -> listOf("100", "1000", "5000", "10000")
                                AgeInputUnit.HOURS -> listOf("1000", "10000", "50000")
                            }
                            presets.forEach { preset ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { onValueChange(preset) }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                        .testTag("preset_$preset")
                                ) {
                                    Text(
                                        text = preset,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Summary Hero
        item {
            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Converts To",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${String.format(Locale.US, "%.2f", breakdown.totalYearsFraction)} Years",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "≈ ${breakdown.years} years, ${breakdown.months} months, ${breakdown.days} days",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // Converted Time Units Grid
        item {
            Text(
                text = "Converted Time Units",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Months",
                    value = numberFormat.format(breakdown.totalMonths),
                    icon = "📅",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Weeks",
                    value = numberFormat.format(breakdown.totalWeeks),
                    icon = "🗓️",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Days",
                    value = numberFormat.format(breakdown.totalDays),
                    icon = "☀️",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Hours",
                    value = numberFormat.format(breakdown.totalHours),
                    icon = "⏰",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Minutes",
                    value = numberFormat.format(breakdown.totalMinutes),
                    icon = "⏱️",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Seconds",
                    value = numberFormat.format(breakdown.totalSeconds),
                    icon = "⚡",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Fast Species & Planet Quick Cross-check
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Fast Cross-Equivalencies",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            val dogAge = AgeCalculator.convertPetToHuman("DOG_MEDIUM", breakdown.totalYearsFraction)
            val catAge = AgeCalculator.convertPetToHuman("CAT", breakdown.totalYearsFraction)
            val marsAge = breakdown.totalDays / 686.98
            val mercuryAge = breakdown.totalDays / 87.97

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Dog Equivalent",
                    value = "${String.format(Locale.US, "%.1f", dogAge)} yrs",
                    subtitle = "Medium Dog Formula",
                    icon = "🐕",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Cat Equivalent",
                    value = "${String.format(Locale.US, "%.1f", catAge)} yrs",
                    subtitle = "Feline Vet Formula",
                    icon = "🐈",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            val marsAge = breakdown.totalDays / 686.98
            val mercuryAge = breakdown.totalDays / 87.97

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Mars Age",
                    value = "${String.format(Locale.US, "%.2f", marsAge)} yrs",
                    subtitle = "687 Earth days/orbit",
                    icon = "♂",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Mercury Age",
                    value = "${String.format(Locale.US, "%.1f", mercuryAge)} yrs",
                    subtitle = "88 Earth days/orbit",
                    icon = "☿",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
