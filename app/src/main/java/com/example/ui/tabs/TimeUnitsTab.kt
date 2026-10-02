package com.example.ui.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TimeBreakdown
import com.example.ui.components.DatePickerModal
import com.example.ui.components.StatCard
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun TimeUnitsTab(
    breakdown: TimeBreakdown,
    targetMillis: Long,
    isCustomTarget: Boolean,
    onSetTargetDate: (Long) -> Unit,
    onResetTargetDate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val numberFormat = remember { NumberFormat.getNumberInstance(Locale.US) }
    var showTargetDatePicker by remember { mutableStateOf(false) }

    val targetDateString = remember(targetMillis) {
        val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        sdf.format(Date(targetMillis))
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Primary Hero Card
            ElevatedCard(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("primary_age_hero_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isCustomTarget) "Age on $targetDateString" else "Current Age",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Years, Months, Days row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AgeUnitHeroItem(
                            value = breakdown.years.toString(),
                            label = if (breakdown.years == 1) "Year" else "Years"
                        )
                        Text(
                            text = "·",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        AgeUnitHeroItem(
                            value = breakdown.months.toString(),
                            label = if (breakdown.months == 1) "Month" else "Months"
                        )
                        Text(
                            text = "·",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        AgeUnitHeroItem(
                            value = breakdown.days.toString(),
                            label = if (breakdown.days == 1) "Day" else "Days"
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                    ) {
                        Text(
                            text = "Exact decimal: ${String.format(Locale.US, "%.3f", breakdown.totalYearsFraction)} years",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Custom Date / Target Date Toolbar
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = "Target Date",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isCustomTarget) "Age as of specific date" else "Comparing with Today",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (isCustomTarget) {
                                Text(
                                    text = targetDateString,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Row {
                        if (isCustomTarget) {
                            FilledTonalButton(
                                onClick = onResetTargetDate,
                                contentPadding = ButtonDefaults.TextButtonContentPadding,
                                modifier = Modifier.testTag("reset_today_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RestartAlt,
                                    contentDescription = "Reset",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Today", fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                        }

                        OutlinedButton(
                            onClick = { showTargetDatePicker = true },
                            contentPadding = ButtonDefaults.TextButtonContentPadding,
                            modifier = Modifier.testTag("pick_target_date_btn")
                        ) {
                            Text("Change", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Time Units Breakdown Grid
        item {
            Text(
                text = "Full Time Breakdown",
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
                    title = "Total Months",
                    value = numberFormat.format(breakdown.totalMonths),
                    subtitle = "≈ ${(breakdown.totalMonths / 12.0).toInt()} full years",
                    icon = "📅",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Total Weeks",
                    value = numberFormat.format(breakdown.totalWeeks),
                    subtitle = "${breakdown.totalDays % 7} extra days",
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
                    title = "Total Days",
                    value = numberFormat.format(breakdown.totalDays),
                    subtitle = "Days alive on Earth",
                    icon = "☀️",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Total Hours",
                    value = numberFormat.format(breakdown.totalHours),
                    subtitle = "Hours passed",
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
                    title = "Total Minutes",
                    value = numberFormat.format(breakdown.totalMinutes),
                    subtitle = "Minutes lived",
                    icon = "⏱️",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Total Seconds",
                    value = numberFormat.format(breakdown.totalSeconds),
                    subtitle = "Seconds of life",
                    icon = "⚡",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Biological & Cosmic Stats
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Life & Biological Insights",
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
                    title = "Heartbeats",
                    value = numberFormat.format(breakdown.heartbeats),
                    subtitle = "Est. at 72 bpm resting rate",
                    icon = "💓",
                    badgeText = "Vital",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Breaths Taken",
                    value = numberFormat.format(breakdown.breaths),
                    subtitle = "Est. at 16 breaths/min",
                    icon = "🫁",
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
                    title = "Time Asleep",
                    value = "${String.format(Locale.US, "%.1f", breakdown.sleepYears)} yrs",
                    subtitle = "${numberFormat.format(breakdown.sleepHours)} hours (1/3 of life)",
                    icon = "😴",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Cosmic Journey",
                    value = "${String.format(Locale.US, "%.1f", breakdown.solarDistanceKm / 1_000_000_000.0)}B km",
                    subtitle = "${String.format(Locale.US, "%.2f", breakdown.earthOrbits)} Earth orbits around Sun",
                    icon = "🚀",
                    badgeText = "Cosmic",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showTargetDatePicker) {
        val targetCal = Calendar.getInstance().apply { timeInMillis = targetMillis }
        DatePickerModal(
            initialYear = targetCal.get(Calendar.YEAR),
            initialMonth = targetCal.get(Calendar.MONTH) + 1,
            initialDay = targetCal.get(Calendar.DAY_OF_MONTH),
            title = "Select Comparison Date",
            onDateSelected = { y, m, d ->
                val newTarget = Calendar.getInstance().apply {
                    set(y, m - 1, d, 12, 0, 0)
                }
                onSetTargetDate(newTarget.timeInMillis)
            },
            onDismiss = { showTargetDatePicker = false }
        )
    }
}

@Composable
private fun AgeUnitHeroItem(
    value: String,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
        )
    }
}
