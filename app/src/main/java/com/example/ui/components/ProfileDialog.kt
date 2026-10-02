package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddProfileDialog(
    onDismiss: () -> Unit,
    onSave: (
        name: String,
        year: Int,
        month: Int,
        day: Int,
        emoji: String,
        isPet: Boolean,
        petType: String
    ) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var isPet by remember { mutableStateOf(false) }
    var selectedEmoji by remember { mutableStateOf("👤") }
    var selectedPetType by remember { mutableStateOf("DOG_MEDIUM") }

    val today = Calendar.getInstance()
    var birthYear by remember { mutableStateOf(if (isPet) today.get(Calendar.YEAR) - 3 else 2000) }
    var birthMonth by remember { mutableStateOf(1) }
    var birthDay by remember { mutableStateOf(1) }
    var showDatePicker by remember { mutableStateOf(false) }

    val humanEmojis = listOf("👤", "👩", "👨", "👶", "🧒", "👵", "👴", "🌟", "👑", "❤️")
    val petEmojis = listOf("🐶", "🐱", "🐕", "🐈", "🐎", "🐇", "🐹", "🦜", "🐢", "🐾")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "New Profile",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.testTag("add_profile_title")
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Category Selector: Person vs Pet
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = !isPet,
                        onClick = {
                            isPet = false
                            selectedEmoji = "👤"
                        },
                        label = { Text("Person 👤") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("profile_type_person_chip")
                    )
                    FilterChip(
                        selected = isPet,
                        onClick = {
                            isPet = true
                            selectedEmoji = "🐶"
                            birthYear = (today.get(Calendar.YEAR) - 3).coerceAtLeast(1990)
                        },
                        label = { Text("Pet 🐾") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("profile_type_pet_chip")
                    )
                }

                // Name Input
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (isPet) "Pet Name (e.g. Luna)" else "Name (e.g. Alex)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_name_input")
                )

                // If pet, choose species
                if (isPet) {
                    Text(
                        text = "Pet Species",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val species = listOf(
                            "DOG_MEDIUM" to "Dog 🐕",
                            "CAT" to "Cat 🐈",
                            "RABBIT" to "Rabbit 🐇",
                            "HORSE" to "Horse 🐎",
                            "HAMSTER" to "Hamster 🐹"
                        )
                        species.forEach { (type, label) ->
                            FilterChip(
                                selected = selectedPetType == type,
                                onClick = { selectedPetType = type },
                                label = { Text(label, fontSize = 12.sp) },
                                modifier = Modifier.testTag("pet_species_${type.lowercase()}_chip")
                            )
                        }
                    }
                }

                // Date Picker Button
                Text(
                    text = "Date of Birth",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_pick_date_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Select Date",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$birthYear-${birthMonth.toString().padStart(2, '0')}-${birthDay.toString().padStart(2, '0')}"
                    )
                }

                // Emoji Avatar Selector
                Text(
                    text = "Choose Icon",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val emojis = if (isPet) petEmojis else humanEmojis
                    emojis.forEach { emoji ->
                        val isSelected = selectedEmoji == emoji
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable { selectedEmoji = emoji }
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 20.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        name,
                        birthYear,
                        birthMonth,
                        birthDay,
                        selectedEmoji,
                        isPet,
                        selectedPetType
                    )
                    onDismiss()
                },
                modifier = Modifier.testTag("profile_save_btn")
            ) {
                Text("Save Profile")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("profile_cancel_btn")
            ) {
                Text("Cancel")
            }
        }
    )

    if (showDatePicker) {
        DatePickerModal(
            initialYear = birthYear,
            initialMonth = birthMonth,
            initialDay = birthDay,
            title = "Select Date of Birth",
            onDateSelected = { y, m, d ->
                birthYear = y
                birthMonth = m
                birthDay = d
            },
            onDismiss = { showDatePicker = false }
        )
    }
}
