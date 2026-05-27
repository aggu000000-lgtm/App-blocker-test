package com.sharma.focusblocker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.designsystem.components.*
import com.sharma.focusblocker.data.Schedule

@Composable
fun ScheduleManagementScreen(
    onNavigateBack: () -> Unit,
    onEditSchedule: (String?) -> Unit,
    viewModel: ScheduleViewModel = hiltViewModel()
) {
    val schedules by viewModel.schedules.collectAsState()

    CatalogScaffold(
        topBar = {
            CatalogTopAppBar(title = { CatalogText("Schedules") })
        },
        floatingActionButton = {
            CatalogFloatingAppBar {
                CatalogButton(onClick = { onEditSchedule(null) }) {
                    CatalogText("New Schedule")
                }
            }
        }
    ) { padding ->
        val items = schedules.map { s ->
            CatalogScheduleItem(
                id = s.id,
                name = s.name,
                timeInfo = "${s.startHour.toString().padStart(2, '0')}:${s.startMinute.toString().padStart(2, '0')} - ${s.endHour.toString().padStart(2, '0')}:${s.endMinute.toString().padStart(2, '0')}",
                daysInfo = formatDays(s.daysOfWeek),
                isEnabled = s.isEnabled,
                transitionTag = "schedule_card_${s.id}"
            )
        }
        
        CatalogScheduleList(
            items = items,
            onToggle = { id, enabled ->
                val s = schedules.find { it.id == id }
                if (s != null) viewModel.toggleSchedule(s, enabled)
            },
            onEdit = { id ->
                onEditSchedule(id)
            },
            modifier = Modifier.padding(padding).fillMaxSize().cinematicLayout()
        )
    }
}

fun formatDays(days: Set<Int>): String {
    val map = mapOf(1 to "Sun", 2 to "Mon", 3 to "Tue", 4 to "Wed", 5 to "Thu", 6 to "Fri", 7 to "Sat")
    return days.sorted().joinToString(", ") { map[it] ?: "" }
}
