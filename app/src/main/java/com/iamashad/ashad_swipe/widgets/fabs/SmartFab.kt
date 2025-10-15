package com.iamashad.ashad_swipe.widgets.fabs

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp

@Composable
fun SmartFab(
    listState: LazyListState,
    onClick: () -> Unit
) {
    val expanded by remember {
        derivedStateOf { listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset < 20 }
    }
    ExtendedFloatingActionButton(
        onClick = onClick,
        expanded = expanded,
        text = {
            Text(
                "Add",
                color = MaterialTheme.colorScheme.primary
            )
        },
        icon = {
            Icon(
                Icons.Default.Add,
                null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(
            topStart = 20.dp,
            bottomEnd = 20.dp,
            topEnd = 4.dp,
            bottomStart = 4.dp
        )
    )
}