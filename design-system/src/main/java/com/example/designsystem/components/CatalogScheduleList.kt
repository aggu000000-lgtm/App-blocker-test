package com.example.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class CatalogScheduleItem(
    val id: String,
    val name: String,
    val timeInfo: String,
    val daysInfo: String,
    val isEnabled: Boolean,
    val transitionTag: String? = null
)

@Composable
fun CatalogScheduleList(
    items: List<CatalogScheduleItem>,
    onToggle: (String, Boolean) -> Unit,
    onEdit: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(items, key = { it.id }) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .let { if (item.transitionTag != null) it.cinematicSharedElement(item.transitionTag) else it }
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = item.name, style = MaterialTheme.typography.titleMedium)
                        Text(text = item.timeInfo, style = MaterialTheme.typography.bodyMedium)
                        Text(text = item.daysInfo, style = MaterialTheme.typography.bodySmall)
                        Button(onClick = { onEdit(item.id) }, modifier = Modifier.padding(top = 8.dp)) {
                            Text("Edit")
                        }
                    }
                    Switch(
                        checked = item.isEnabled,
                        onCheckedChange = { onToggle(item.id, it) }
                    )
                }
            }
        }
    }
}
