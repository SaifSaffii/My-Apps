package com.example.ui.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    initialYear: Int,
    initialMonth: Int, // 1..12
    initialDay: Int,
    title: String = "Select Date",
    onDateSelected: (year: Int, month: Int, day: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val initialCal = Calendar.getInstance().apply {
        set(initialYear, initialMonth - 1, initialDay, 12, 0, 0)
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialCal.timeInMillis
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val cal = Calendar.getInstance().apply {
                            timeInMillis = millis
                        }
                        onDateSelected(
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH) + 1,
                            cal.get(Calendar.DAY_OF_MONTH)
                        )
                    }
                    onDismiss()
                },
                modifier = Modifier.testTag("date_picker_confirm_btn")
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("date_picker_cancel_btn")
            ) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            title = {
                Text(
                    text = title,
                    modifier = Modifier.testTag("date_picker_title")
                )
            }
        )
    }
}
