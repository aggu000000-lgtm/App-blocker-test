package com.example.designsystem.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle

@Composable
fun CatalogText(text: String, modifier: Modifier = Modifier, style: TextStyle = MaterialTheme.typography.bodyLarge) {
    Text(text = text, modifier = modifier, style = style)
}

@Composable
fun CatalogButton(onClick: () -> Unit, modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) {
    Button(onClick = onClick, modifier = modifier, content = content)
}

@Composable
fun CatalogTextButton(onClick: () -> Unit, modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) {
    TextButton(onClick = onClick, modifier = modifier, content = content)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(value = value, onValueChange = onValueChange, label = label, modifier = modifier)
}

@Composable
fun CatalogSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    Switch(checked = checked, onCheckedChange = onCheckedChange, modifier = modifier)
}

@Composable
fun CatalogCheckbox(checked: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    Checkbox(checked = checked, onCheckedChange = onCheckedChange, modifier = modifier)
}

@Composable
fun CatalogCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = modifier, content = content)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogTopAppBar(title: @Composable () -> Unit, modifier: Modifier = Modifier) {
    TopAppBar(title = title, modifier = modifier)
}

@Composable
fun CatalogScaffold(
    topBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (androidx.compose.foundation.layout.PaddingValues) -> Unit
) {
    Scaffold(
        topBar = topBar,
        floatingActionButton = floatingActionButton,
        content = content
    )
}
