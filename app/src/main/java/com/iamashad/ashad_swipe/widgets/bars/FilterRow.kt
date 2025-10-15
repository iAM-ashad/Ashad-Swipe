package com.iamashad.ashad_swipe.widgets.bars

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FilterRow(
    selectedType: String?,
    onTypeChange: (String?) -> Unit,
    sort: String,
    onSortChange: (String) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("All", "Product", "Service").forEach { t ->
                val selected = (selectedType ?: "All").equals(t, ignoreCase = true)
                AssistChip(
                    onClick = { onTypeChange(if (t == "All") null else t) },
                    label = { Text(t) },
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        bottomEnd = 16.dp,
                        topEnd = 4.dp,
                        bottomStart = 4.dp
                    ),
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (selected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = if (selected)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        var open by remember { mutableStateOf(false) }
        val items = listOf("Name", "Low to High", "High to Low")

        Box {
            TextButton(onClick = { open = true }) { Text(sort) }
            DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
                items.forEach {
                    DropdownMenuItem(
                        text = { Text(it) },
                        onClick = {
                            onSortChange(it)
                            open = false
                        }
                    )
                }
            }
        }
    }
}