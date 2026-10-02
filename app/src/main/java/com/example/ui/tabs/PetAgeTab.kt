package com.example.ui.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PetAge
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PetAgeTab(
    petAges: List<PetAge>,
    humanAgeYears: Double,
    isPetToHumanMode: Boolean,
    onToggleMode: (Boolean) -> Unit,
    selectedPetType: String,
    onSelectPetType: (String) -> Unit,
    petAgeInput: String,
    onPetAgeInputChange: (String) -> Unit,
    convertedPetToHumanAge: Double,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Mode Switch TabRow
            TabRow(
                selectedTabIndex = if (isPetToHumanMode) 1 else 0,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .testTag("pet_mode_tab_row")
            ) {
                Tab(
                    selected = !isPetToHumanMode,
                    onClick = { onToggleMode(false) },
                    text = { Text("Human ➔ Pet Age", fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.testTag("human_to_pet_tab")
                )
                Tab(
                    selected = isPetToHumanMode,
                    onClick = { onToggleMode(true) },
                    text = { Text("Pet ➔ Human Age", fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.testTag("pet_to_human_tab")
                )
            }
        }

        if (isPetToHumanMode) {
            // Pet to Human Converter UI
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Pet to Human Age Equivalent",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "${String.format(Locale.US, "%.1f", convertedPetToHumanAge)}",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "Human Years Old",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stage and veterinary insight
                        val stageInfo = getPetStageLabel(selectedPetType, petAgeInput.toDoubleOrNull() ?: 0.0)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Life Stage: ${stageInfo.first}",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = stageInfo.second,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Input section
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Select Animal Species",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )

                        val speciesOptions = listOf(
                            "DOG_SMALL" to "Small Dog (<20lb) 🐕",
                            "DOG_MEDIUM" to "Medium Dog 🐕‍🦺",
                            "DOG_LARGE" to "Large Dog (>50lb) 🦮",
                            "CAT" to "Cat 🐈",
                            "HORSE" to "Horse 🐎",
                            "RABBIT" to "Rabbit 🐇",
                            "HAMSTER" to "Hamster 🐹"
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            speciesOptions.forEach { (type, label) ->
                                FilterChip(
                                    selected = selectedPetType == type,
                                    onClick = { onSelectPetType(type) },
                                    label = { Text(label, fontSize = 12.sp) },
                                    modifier = Modifier.testTag("select_pet_type_${type.lowercase()}")
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        OutlinedTextField(
                            value = petAgeInput,
                            onValueChange = onPetAgeInputChange,
                            label = { Text("Enter Pet's Actual Age (years)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("pet_age_numeric_input")
                        )

                        // Quick age buttons
                        Text(
                            text = "Quick Presets:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("0.5", "1", "2", "4", "7", "10", "13").forEach { preset ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { onPetAgeInputChange(preset) }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                        .testTag("preset_age_$preset")
                                ) {
                                    Text(text = "$preset y", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Human to Pet Species Grid
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🐾", fontSize = 32.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "If you were an animal at ${String.format(Locale.US, "%.1f", humanAgeYears)} yrs:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Standard American Veterinary Medical Association (AVMA) developmental equivalencies.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            items(petAges, key = { it.id }) { pet ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pet_card_${pet.id}")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = pet.emoji, fontSize = 28.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = pet.speciesName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Lifespan: ${pet.lifeExpectancy}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                contentColor = MaterialTheme.colorScheme.primary
                            ) {
                                Text(
                                    text = pet.lifeStage,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Equivalent Age",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${String.format(Locale.US, "%.1f", pet.equivalentHumanAge)} yrs",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Text(
                                text = pet.explanation,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 16.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private fun getPetStageLabel(type: String, age: Double): Pair<String, String> {
    return when (type) {
        "DOG_SMALL", "DOG_MEDIUM", "DOG_LARGE" -> when {
            age < 1.0 -> "Puppy" to "Rapid physical & cognitive development"
            age < 3.0 -> "Young Adult" to "High energy, peak strength"
            age < 8.0 -> "Mature Adult" to "Stable prime maintenance stage"
            age < 12.0 -> "Senior" to "Watch joint health & nutrition"
            else -> "Geriatric" to "Specialized senior veterinary care"
        }
        "CAT" -> when {
            age < 1.0 -> "Kitten" to "Fast growth & socialization"
            age < 6.0 -> "Prime Adult" to "Peak athletic vitality"
            age < 11.0 -> "Mature" to "Monitor weight & teeth"
            age < 15.0 -> "Senior" to "Kidney & thyroid health checks"
            else -> "Super Senior" to "Gentle comforts & routine checks"
        }
        "HORSE" -> when {
            age < 4.0 -> "Colt / Filly" to "Training & bone development"
            age < 18.0 -> "Prime Riding" to "Athletic performance years"
            else -> "Senior" to "Pasture rest & softer feed"
        }
        "RABBIT" -> when {
            age < 1.0 -> "Juvenile" to "Rapid growth"
            age < 6.0 -> "Adult" to "Active foraging stage"
            else -> "Senior" to "Low impact comfort"
        }
        else -> when {
            age < 1.0 -> "Young" to "Active lifecycle"
            else -> "Senior" to "Golden years"
        }
    }
}
