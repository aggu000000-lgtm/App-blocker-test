package com.example.appblocker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.appblocker.ui.theme.spacing

@Composable
fun ScreenHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineMedium,
        modifier = modifier.padding(vertical = MaterialTheme.spacing.medium)
    )
}

@Composable
fun SectionGroup(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(
                start = MaterialTheme.spacing.small,
                bottom = MaterialTheme.spacing.small
            )
        )
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(vertical = MaterialTheme.spacing.small)) {
                content()
            }
        }
    }
}

@Composable
fun ItemTextColumn(
    title: String,
    description: String? = null,
    titleStyle: androidx.compose.ui.text.TextStyle? = null,
    descriptionStyle: androidx.compose.ui.text.TextStyle? = null,
    titleColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Unspecified,
    descriptionColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Unspecified,
    modifier: Modifier = Modifier
) {
    val defaultTitleStyle = MaterialTheme.typography.titleMedium
    val defaultDescStyle = MaterialTheme.typography.bodyMedium
    val defaultDescColor = MaterialTheme.colorScheme.onSurfaceVariant

    Column(modifier = modifier) {
        Text(
            text = title,
            style = titleStyle ?: defaultTitleStyle,
            color = titleColor
        )
        if (description != null) {
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.textGap))
            Text(
                text = description,
                style = descriptionStyle ?: defaultDescStyle,
                color = if (descriptionColor == androidx.compose.ui.graphics.Color.Unspecified) defaultDescColor else descriptionColor
            )
        }
    }
}

@Composable
fun SettingItem(
    title: String,
    description: String? = null,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues? = null,
    action: (@Composable () -> Unit)? = null
) {
    val padding = contentPadding ?: PaddingValues(
        horizontal = MaterialTheme.spacing.itemHorizontal,
        vertical = MaterialTheme.spacing.itemVertical
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(padding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ItemTextColumn(
            title = title,
            description = description,
            modifier = Modifier.weight(1f, fill = false)
        )
        if (action != null) {
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.medium))
            action()
        }
    }
}
