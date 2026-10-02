package com.example.ui.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserProfile
import java.text.DateFormatSymbols
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppHeader(
    profiles: List<UserProfile>,
    activeProfileId: Int?,
    birthYear: Int,
    birthMonth: Int, // 1..12
    birthDay: Int,
    onSelectProfile: (UserProfile) -> Unit,
    onAddProfileClick: () -> Unit,
    onDeleteProfile: (UserProfile) -> Unit,
    onPickBirthDateClick: () -> Unit,
    onAdjustYear: (Int) -> Unit,
    shareSummaryText: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showProfileMenuFor by remember { mutableStateOf<UserProfile?>(null) }

    val monthName = DateFormatSymbols().shortMonths.getOrElse(birthMonth - 1) { "Jan" }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // App Title & Share Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("⏳", fontSize = 18.sp)
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Age Converter",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Time · Pets · Cosmic · Milestones",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(
                onClick = {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareSummaryText)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, "Share Age Summary")
                    context.startActivity(shareIntent)
                },
                modifier = Modifier.testTag("share_summary_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share Summary",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Profile Selector Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(profiles, key = { it.id }) { profile ->
                val isSelected = activeProfileId == profile.id
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectProfile(profile) },
                    leadingIcon = {
                        Text(text = profile.emoji, fontSize = 14.sp)
                    },
                    label = {
                        Text(
                            text = profile.name,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    trailingIcon = {
                        if (!profile.isDefault) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Profile Options",
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { showProfileMenuFor = profile }
                            )
                        }
                    },
                    modifier = Modifier.testTag("profile_chip_${profile.id}")
                )
            }

            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier
                        .height(32.dp)
                        .clickable { onAddProfileClick() }
                        .testTag("add_profile_chip")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Profile",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "New",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Birthdate Selector Banner Card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Year decrement stepper
                TextButton(
                    onClick = { onAdjustYear(-1) },
                    modifier = Modifier.testTag("prev_year_btn")
                ) {
                    Text("-1 yr", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                // Date Display with Click to Pick
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onPickBirthDateClick() }
                        .testTag("open_date_picker_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Pick Date",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "$monthName $birthDay, $birthYear",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // Year increment stepper
                TextButton(
                    onClick = { onAdjustYear(1) },
                    modifier = Modifier.testTag("next_year_btn")
                ) {
                    Text("+1 yr", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }

    // Profile context menu (to delete custom profiles)
    showProfileMenuFor?.let { profile ->
        DropdownMenu(
            expanded = true,
            onDismissRequest = { showProfileMenuFor = null }
        ) {
            DropdownMenuItem(
                text = { Text("Delete '${profile.name}'") },
                onClick = {
                    onDeleteProfile(profile)
                    showProfileMenuFor = null
                },
                modifier = Modifier.testTag("delete_profile_item")
            )
        }
    }
}
