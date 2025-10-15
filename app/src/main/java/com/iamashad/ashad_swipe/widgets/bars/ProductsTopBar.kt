package com.iamashad.ashad_swipe.widgets.bars

import android.os.Build
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.iamashad.ashad_swipe.R
import com.iamashad.ashad_swipe.widgets.dialogs.ElegantAlertDialog
import com.iamashad.ashad_swipe.widgets.inputs.SearchBarField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    resultCount: Int,
    dynamicColor: Boolean,
    onToggleDynamicColor: (Boolean) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior?
) {
    var menuOpen by remember { mutableStateOf(false) }
    var showAbout by remember { mutableStateOf(false) }
    val dynamicSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    Column {
        CenterAlignedTopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(
                                topStart = 20.dp,
                                bottomEnd = 20.dp,
                                topEnd = 4.dp,
                                bottomStart = 4.dp
                            )
                        )
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.surface,
                                    MaterialTheme.colorScheme.primary
                                )
                            )
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .animateContentSize()
                ) {
                    Image(
                        painter = painterResource(R.drawable.app_logo),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(24.dp)
                    )
                    Text(
                        buildAnnotatedString {
                            withStyle(
                                SpanStyle(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontStyle = FontStyle.Italic
                                )
                            ) {
                                append("Swipe")
                            }
                            withStyle(
                                SpanStyle(
                                    color = MaterialTheme.colorScheme.surface,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("Products")
                            }
                        },
                        style = MaterialTheme.typography.headlineSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            },
            scrollBehavior = scrollBehavior,
            actions = {
                IconButton(onClick = { menuOpen = true }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "More")
                }
                DropdownMenu(
                    expanded = menuOpen,
                    onDismissRequest = { menuOpen = false },
                    modifier = Modifier
                        .width(240.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.errorContainer,
                                    MaterialTheme.colorScheme.surface
                                )
                            )
                        )
                ) {
                    if (dynamicSupported) {
                        DropdownMenuItem(
                            text = {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Use dynamic color")
                                        Text(
                                            text = if (dynamicColor) "Using Material You" else "Using app theme",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    // Display-only switch; toggle handled by onClick
                                    Switch(checked = dynamicColor, onCheckedChange = null)
                                }
                            },
                            leadingIcon = { Icon(Icons.Filled.ColorLens, null) },
                            onClick = {
                                onToggleDynamicColor(!dynamicColor)
                                menuOpen = false
                            }
                        )
                    } else {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Dynamic color requires Android 12+",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Warning,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            enabled = false,
                            onClick = { }
                        )
                    }

                    HorizontalDivider()

                    DropdownMenuItem(
                        text = { Text("About") },
                        leadingIcon = { Icon(Icons.Filled.Info, null) },
                        onClick = {
                            menuOpen = false
                            showAbout = true
                        }
                    )
                }
            }
        )

        SearchBarField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = "Search products or types"
        )

        Text(
            "$resultCount result${if (resultCount == 1) "" else "s"}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 16.dp, bottom = 6.dp)
        )
    }

    if (showAbout) {
        ElegantAlertDialog(
            title = "Dynamic Theme",
            message = "When enabled on Android 12+, the app adapts its colors to your wallpaper, " +
                    "for a more personal look. You can turn this on/off anytime from the menu.",
            confirmText = "Got it",
            dismissText = "Close",
            icon = Icons.Filled.ColorLens,
            onConfirm = { showAbout = false },
            onDismiss = { showAbout = false }
        )
    }
}