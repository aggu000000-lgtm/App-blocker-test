package com.example.appblocker.ui.blocker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.appblocker.ui.components.ItemTextColumn
import com.example.appblocker.ui.components.ScreenHeader
import com.example.appblocker.ui.foundation.springPress
import com.example.appblocker.ui.theme.spacing
import com.example.appblocker.ui.theme.motion
import java.util.UUID

data class BlockRule(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val isActive: Boolean = false
)

@Composable
fun BlockerScreen(modifier: Modifier = Modifier) {
    val rules = remember {
        mutableStateListOf(
            BlockRule(
                name = "Social Media",
                description = "Block Instagram, TikTok, Twitter during work hours",
                isActive = true
            ),
            BlockRule(
                name = "Games",
                description = "Restrict gaming apps after 10 PM",
                isActive = false
            ),
            BlockRule(
                name = "News",
                description = "Limit news apps to 15 min per day",
                isActive = false
            )
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    rules.add(BlockRule(name = "New Rule", description = "Tap to configure"))
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add rule")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = MaterialTheme.spacing.screenHorizontal)
        ) {
            ScreenHeader(title = "Blocking Rules")
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.itemGap)
            ) {
                items(rules, key = { it.id }) { rule ->
                    BlockRuleCard(
                        rule = rule,
                        onToggle = { active ->
                            val index = rules.indexOfFirst { it.id == rule.id }
                            if (index != -1) {
                                rules[index] = rule.copy(isActive = active)
                            }
                        },
                        modifier = Modifier.animateItem(
                            fadeInSpec = MaterialTheme.motion.interactiveSpring,
                            placementSpec = MaterialTheme.motion.interactiveSpringIntOffset,
                            fadeOutSpec = MaterialTheme.motion.interactiveSpring
                        )
                    )
                }
                item { Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraExtraLarge)) }
            }
        }
    }
}

@Composable
private fun BlockRuleCard(
    rule: BlockRule,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = androidx.compose.runtime.remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .springPress(interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onToggle(!rule.isActive) }
            .padding(MaterialTheme.spacing.itemHorizontal),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ItemTextColumn(
            title = rule.name,
            description = rule.description,
            titleStyle = MaterialTheme.typography.titleLarge,
            descriptionStyle = MaterialTheme.typography.bodyMedium,
            titleColor = if (rule.isActive)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurfaceVariant,
            descriptionColor = if (rule.isActive)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = rule.isActive,
            onCheckedChange = onToggle
        )
    }
}
