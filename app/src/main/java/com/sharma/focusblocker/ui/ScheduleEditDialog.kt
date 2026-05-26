package com.sharma.focusblocker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.designsystem.components.*
import com.sharma.focusblocker.data.Schedule

@Composable
fun ScheduleEditDialog(
    schedule: Schedule?,
    onSave: (Schedule) -> Unit,
    onDelete: (() -> Unit)? = null,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(schedule?.name ?: "") }
    var startHour by remember { mutableStateOf(schedule?.startHour ?: 9) }
    var startMinute by remember { mutableStateOf(schedule?.startMinute ?: 0) }
    var endHour by remember { mutableStateOf(schedule?.endHour ?: 17) }
    var endMinute by remember { mutableStateOf(schedule?.endMinute ?: 0) }
    var daysOfWeek by remember { mutableStateOf(schedule?.daysOfWeek ?: setOf(2,3,4,5,6)) }
    var blockedPackages by remember { mutableStateOf(schedule?.blockedPackages ?: setOf()) }
    
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    var packageInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            CatalogButton(onClick = {
                onSave(
                    Schedule(
                        id = schedule?.id ?: java.util.UUID.randomUUID().toString(),
                        name = name.ifBlank { "Untitled" },
                        isEnabled = schedule?.isEnabled ?: true,
                        startHour = startHour,
                        startMinute = startMinute,
                        endHour = endHour,
                        endMinute = endMinute,
                        daysOfWeek = daysOfWeek,
                        blockedPackages = blockedPackages
                    )
                )
            }) {
                CatalogText("Save")
            }
        },
        dismissButton = {
            Row {
                if (onDelete != null) {
                    CatalogTextButton(onClick = onDelete) {
                        CatalogText("Delete")
                    }
                }
                CatalogTextButton(onClick = onDismiss) {
                    CatalogText("Cancel")
                }
            }
        },
        title = { CatalogText(if (schedule == null) "New Schedule" else "Edit Schedule") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                CatalogOutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { CatalogText("Schedule Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    CatalogButton(onClick = { showStartTimePicker = true }) {
                        CatalogText("Start: ${startHour.toString().padStart(2, '0')}:${startMinute.toString().padStart(2, '0')}")
                    }
                    CatalogButton(onClick = { showEndTimePicker = true }) {
                        CatalogText("End: ${endHour.toString().padStart(2, '0')}:${endMinute.toString().padStart(2, '0')}")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                CatalogText("Days of Week")
                val days = listOf(
                    1 to "Sun", 2 to "Mon", 3 to "Tue", 4 to "Wed", 
                    5 to "Thu", 6 to "Fri", 7 to "Sat"
                )
                // Use a wrap layout or columns
                days.chunked(3).forEach { rowDays ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        rowDays.forEach { (day, label) ->
                            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                CatalogCheckbox(
                                    checked = daysOfWeek.contains(day),
                                    onCheckedChange = { checked ->
                                        val newDays = daysOfWeek.toMutableSet()
                                        if (checked) newDays.add(day) else newDays.remove(day)
                                        daysOfWeek = newDays
                                    }
                                )
                                CatalogText(label)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                CatalogText("Blocked Packages")
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    CatalogOutlinedTextField(
                        value = packageInput,
                        onValueChange = { packageInput = it },
                        modifier = Modifier.weight(1f)
                    )
                    CatalogButton(onClick = {
                        if (packageInput.isNotBlank()) {
                            val newPkgs = blockedPackages.toMutableSet()
                            newPkgs.add(packageInput.trim())
                            blockedPackages = newPkgs
                            packageInput = ""
                        }
                    }) {
                        CatalogText("Add")
                    }
                }
                
                blockedPackages.forEach { pkg ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        CatalogText(pkg)
                        CatalogTextButton(onClick = {
                            val newPkgs = blockedPackages.toMutableSet()
                            newPkgs.remove(pkg)
                            blockedPackages = newPkgs
                        }) {
                            CatalogText("X")
                        }
                    }
                }
            }
        }
    )

    if (showStartTimePicker) {
        CatalogTimePickerDialog(
            initialHour = startHour,
            initialMinute = startMinute,
            onConfirm = { h, m ->
                startHour = h
                startMinute = m
                showStartTimePicker = false
            },
            onDismiss = { showStartTimePicker = false }
        )
    }

    if (showEndTimePicker) {
        CatalogTimePickerDialog(
            initialHour = endHour,
            initialMinute = endMinute,
            onConfirm = { h, m ->
                endHour = h
                endMinute = m
                showEndTimePicker = false
            },
            onDismiss = { showEndTimePicker = false }
        )
    }
}
