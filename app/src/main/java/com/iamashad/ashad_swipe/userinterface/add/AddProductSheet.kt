package com.iamashad.ashad_swipe.userinterface.add

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AddAPhoto
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.PriceChange
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.Tag
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.iamashad.ashad_swipe.util.isNonNegativeDecimal
import com.iamashad.ashad_swipe.util.sanitizeDecimal
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductSheet(
    onDismiss: () -> Unit,
    onResult: (Boolean, String, String) -> Unit
) {
    val context = LocalContext.current
    val vm: AddProductViewModel = koinViewModel()
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val keyboard = LocalSoftwareKeyboardController.current

    // Focus controllers for smooth form navigation
    val nameFocus = remember { androidx.compose.ui.focus.FocusRequester() }
    val priceFocus = remember { androidx.compose.ui.focus.FocusRequester() }
    val taxFocus = remember { androidx.compose.ui.focus.FocusRequester() }

    // Launch image picker
    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? -> vm.image = uri }

    val actionsBarHeight = 64.dp

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        BoxWithConstraints(
            Modifier
                .fillMaxWidth()
                .imePadding()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            val maxSheetHeight = maxHeight * 0.74f

            Box(
                Modifier
                    .heightIn(max = maxSheetHeight)
                    .fillMaxWidth()
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(bottom = actionsBarHeight)
                ) {
                    // --- Header ---
                    Header(vm)
                    HorizontalDivider()

                    // --- Form ---
                    Column(
                        Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(Modifier.height(8.dp))

                        // Product name input
                        OutlinedTextField(
                            value = vm.name,
                            onValueChange = { vm.name = it },
                            label = { Text("Product name") },
                            leadingIcon = { Icon(Icons.Rounded.ShoppingCart, null) },
                            singleLine = true,
                            supportingText = {
                                val len = vm.name.trim().length
                                Text(if (len < 3) "Name should ideally be at least 3 characters" else "Looks good")
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            enabled = !vm.submitting,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                                .focusRequester(nameFocus)
                        )

                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Type",
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(Modifier.height(6.dp))

                        TypeSelector(
                            selected = vm.type,
                            enabled = !vm.submitting,
                            onSelect = { vm.type = it },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        // Price field
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = vm.price,
                            onValueChange = { vm.price = sanitizeDecimal(it) },
                            label = { Text("Price") },
                            leadingIcon = { Icon(Icons.Rounded.PriceChange, null) },
                            prefix = { Text("₹") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Next
                            ),
                            enabled = !vm.submitting,
                            isError = vm.price.isNotBlank() && !isNonNegativeDecimal(vm.price),
                            supportingText = {
                                if (vm.price.isNotBlank() && !isNonNegativeDecimal(vm.price))
                                    Text("Enter a valid non-negative amount")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                                .focusRequester(priceFocus)
                        )

                        // Tax field
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = vm.tax,
                            onValueChange = { vm.tax = sanitizeDecimal(it) },
                            label = { Text("Tax") },
                            leadingIcon = { Icon(Icons.Rounded.Tag, null) },
                            suffix = { Text("%") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(onDone = {
                                keyboard?.hide()
                                focusManager.clearFocus()
                            }),
                            enabled = !vm.submitting,
                            isError = vm.tax.isNotBlank() && !isNonNegativeDecimal(vm.tax),
                            supportingText = {
                                if (vm.tax.isNotBlank() && !isNonNegativeDecimal(vm.tax))
                                    Text("Enter a valid non-negative percent")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                                .focusRequester(taxFocus)
                        )

                        // Image picker
                        Spacer(Modifier.height(12.dp))
                        ImagePickerCard(
                            image = vm.image,
                            enabled = !vm.submitting,
                            onPick = {
                                picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            },
                            onRemove = { vm.image = null },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(Modifier.height(16.dp))
                    }
                }

                // --- Sticky bottom bar ---
                Surface(
                    tonalElevation = 2.dp,
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(actionsBarHeight)
                ) {
                    Row(
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                focusManager.clearFocus(true)
                                onDismiss()
                            },
                            enabled = !vm.submitting
                        ) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Cancel")
                        }

                        FilledTonalButton(
                            onClick = {
                                scope.launch {
                                    val (ok, message) = vm.submit(context)
                                    onResult(ok, message, vm.name)
                                    if (ok) {
                                        focusManager.clearFocus(true)
                                        vm.clear()
                                        runCatching { sheetState.hide() }
                                        onDismiss()
                                    }
                                }
                            },
                            enabled = !vm.submitting &&
                                    vm.name.isNotBlank() &&
                                    (vm.type == "Product" || vm.type == "Service") &&
                                    vm.price.toDoubleOrNull() != null &&
                                    vm.tax.toDoubleOrNull() != null,
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(vertical = 12.dp)
                        ) {
                            if (vm.submitting) {
                                CircularProgressIndicator(
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("Saving…", style = MaterialTheme.typography.labelLarge)
                            } else {
                                Icon(Icons.Rounded.Check, null)
                                Spacer(Modifier.width(8.dp))
                                Text("Submit")
                            }
                        }
                    }
                }

                // --- Loading overlay ---
                this@ModalBottomSheet.AnimatedVisibility(
                    visible = vm.submitting,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.matchParentSize()
                ) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.35f))
                    )
                }
            }
        }
    }
}

/* ===== Header ===== */
@Composable
private fun Header(vm: AddProductViewModel) {
    val gradient = Brush.linearGradient(
        listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primaryContainer
        )
    )

    Surface(
        color = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.onSecondary,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradient, RectangleShape)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image preview chip
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.55f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(0.4f)),
                modifier = Modifier.size(56.dp)
            ) {
                if (vm.image != null) {
                    AsyncImage(
                        model = vm.image,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.clip(RoundedCornerShape(14.dp))
                    )
                } else {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.Image, null)
                    }
                }
            }

            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                Text(
                    "Add Product",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.surface
                    )
                )
                val meta = buildString {
                    if (vm.name.isNotEmpty()) append(vm.name)
                    if (vm.price.isNotEmpty())
                        append(if (isNotEmpty()) "  •  ₹${vm.price}" else "₹${vm.price}")
                    if (vm.tax.isNotEmpty()) append("  •  ${vm.tax}%")
                    if (vm.type != null) append("  •  ${vm.type}")
                }
                if (meta.isNotEmpty()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        meta,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

/* ===== Type Selector ===== */
@Composable
private fun TypeSelector(
    selected: String?,
    enabled: Boolean,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf("Product", "Service")
    SingleChoiceSegmentedButtonRow(modifier) {
        items.forEachIndexed { index, label ->
            SegmentedButton(
                selected = selected == label,
                onClick = { if (enabled) onSelect(label) },
                enabled = enabled,
                colors = SegmentedButtonDefaults.colors(
                    inactiveBorderColor = MaterialTheme.colorScheme.onSurface,
                    activeBorderColor = MaterialTheme.colorScheme.onSurface,
                    activeContainerColor = MaterialTheme.colorScheme.primary
                ),
                shape = SegmentedButtonDefaults.itemShape(index, items.size),
                icon = {
                    if (selected == label) Icon(
                        Icons.Rounded.Check,
                        null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            ) {
                Text(
                    label,
                    fontWeight = if (selected == label) FontWeight.Bold else FontWeight.Normal,
                    color = if (selected == label) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

/* ===== Image Picker Card ===== */
@Composable
private fun ImagePickerCard(
    image: Uri?,
    enabled: Boolean,
    onPick: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val outline = MaterialTheme.colorScheme.outlineVariant
    Surface(
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 1.dp,
        border = BorderStroke(1.dp, outline.copy(alpha = 0.5f)),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            Modifier
                .clickable(enabled) { onPick() }
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ) {
                Icon(Icons.Rounded.AddAPhoto, null, modifier = Modifier.padding(10.dp))
            }

            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Product image", style = MaterialTheme.typography.titleSmall)
                Text(
                    if (image != null) "Tap to change" else "Tap to pick (optional)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (image != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = image,
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                    Spacer(Modifier.width(8.dp))
                    IconButton(onClick = onRemove, enabled = enabled) {
                        Icon(Icons.Rounded.Delete, null)
                    }
                }
            }
        }
    }
}
