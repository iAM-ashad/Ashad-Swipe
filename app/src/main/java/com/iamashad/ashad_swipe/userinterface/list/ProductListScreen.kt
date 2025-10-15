package com.iamashad.ashad_swipe.userinterface.list

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CloudUpload
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.work.WorkInfo
import androidx.work.WorkManager
import coil3.BitmapImage
import coil3.DrawableImage
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import com.iamashad.ashad_swipe.R
import com.iamashad.ashad_swipe.domain.model.Product
import com.iamashad.ashad_swipe.domain.repo.Resource
import com.iamashad.ashad_swipe.userinterface.add.AddProductSheet
import com.iamashad.ashad_swipe.util.NotificationHelper
import com.iamashad.ashad_swipe.util.NotificationPrefs
import com.iamashad.ashad_swipe.util.bestOnColor
import com.iamashad.ashad_swipe.util.extractGradientColors
import com.iamashad.ashad_swipe.widgets.badges.PendingBadge
import com.iamashad.ashad_swipe.widgets.bars.FilterRow
import com.iamashad.ashad_swipe.widgets.bars.ProductsTopBar
import com.iamashad.ashad_swipe.widgets.dialogs.ElegantAlertDialog
import com.iamashad.ashad_swipe.widgets.effects.ShineEffect
import com.iamashad.ashad_swipe.widgets.fabs.SmartFab
import com.iamashad.ashad_swipe.work.UploadPendingWorker
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    dynamicColor: Boolean,
    onToggleDynamicColor: (Boolean) -> Unit
) {
    val vm: ProductListViewModel = koinViewModel()
    val state by vm.state.collectAsState()
    val context = LocalContext.current

    // UI states
    var showAdd by remember { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }
    var selectedType by rememberSaveable { mutableStateOf<String?>(null) } // "All" or specific type
    var sort by rememberSaveable { mutableStateOf("Name") }

    // Dialog states
    var showAddedDialog by remember { mutableStateOf(false) }
    var showSavedOfflineDialog by remember { mutableStateOf(false) }
    var showSyncedDialog by remember { mutableStateOf(false) }
    var lastAddedName by remember { mutableStateOf("") }
    var lastSyncedCount by remember { mutableIntStateOf(0) }

    // Notification permission handling (Android 13+)
    var showNotifRationale by remember { mutableStateOf(false) }
    val notifPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        NotificationPrefs.markAsked(context)
        if (granted) {
            if (showAddedDialog) NotificationHelper.notifyProductAdded(context, lastAddedName)
            else if (showSyncedDialog && lastSyncedCount > 0)
                NotificationHelper.notifyPendingSynced(context, lastSyncedCount)
        } else NotificationPrefs.markDeclined(context)
    }

    // Helper for showing notifications with permission handling
    fun requestNotifPermissionIfNeeded(action: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            when {
                granted -> action()
                NotificationPrefs.shouldShowRationale(context) -> showNotifRationale = true
            }
        } else action()
    }

    fun tryNotifyAdded(name: String) = requestNotifPermissionIfNeeded {
        NotificationHelper.notifyProductAdded(context, name)
    }

    fun tryNotifySynced(count: Int) = requestNotifPermissionIfNeeded {
        NotificationHelper.notifyPendingSynced(context, count)
    }

    // Pull to refresh setup
    var isRefreshing by remember { mutableStateOf(false) }
    val pullState = rememberPullToRefreshState()

    LaunchedEffect(state) {
        if (isRefreshing && state !is Resource.Loading) isRefreshing = false
    }

    // Gentle haptic feedback when refresh completes
    val haptics = LocalHapticFeedback.current
    var wasRefreshing by remember { mutableStateOf(false) }
    LaunchedEffect(isRefreshing, state) {
        if (wasRefreshing && !isRefreshing && state is Resource.Success)
            haptics.performHapticFeedback(HapticFeedbackType.Confirm)
        wasRefreshing = isRefreshing
    }

    // Debounced search input
    LaunchedEffect(query) {
        delay(250)
        vm.setQuery(query)
    }

    val listState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    // Filter count for top bar badge
    val products = (state as? Resource.Success)?.data ?: emptyList()
    val filtered = when (selectedType) {
        null, "All" -> products
        else -> products.filter { it.type.equals(selectedType, true) }
    }
    val resultCount = filtered.size

    Scaffold(
        floatingActionButton = { SmartFab(listState) { showAdd = true } }
    ) { pad ->

        // Observe background worker for syncing pending uploads
        ObservePendingUploads { count ->
            if (count > 0) {
                lastSyncedCount = count
                showSyncedDialog = true
                tryNotifySynced(count)
            }
        }

        // --- Add Product Sheet ---
        if (showAdd) {
            AddProductSheet(
                onDismiss = { showAdd = false },
                onResult = { ok, message, name ->
                    if (ok && message.contains("Successfully", true)) {
                        lastAddedName = name
                        showAddedDialog = true
                        tryNotifyAdded(name)
                    } else if (ok && message.contains("Saved offline", true)) {
                        showSavedOfflineDialog = true
                    }
                }
            )
        }

        // --- Alerts ---
        if (showAddedDialog)
            ElegantAlertDialog(
                title = "Product added",
                message = "“$lastAddedName” was added successfully.",
                icon = Icons.Rounded.CheckCircle,
                onDismiss = { showAddedDialog = false },
                onConfirm = { showAddedDialog = false },
                confirmText = "OK"
            )

        if (showSavedOfflineDialog)
            ElegantAlertDialog(
                title = "Saved offline",
                message = "We’ll upload this product automatically when you’re back online.",
                icon = Icons.Rounded.WifiOff,
                onDismiss = { showSavedOfflineDialog = false },
                onConfirm = { showSavedOfflineDialog = false },
                confirmText = "Got it"
            )

        if (showSyncedDialog)
            ElegantAlertDialog(
                title = "Upload complete",
                message = "Uploaded $lastSyncedCount pending item(s).",
                icon = Icons.Rounded.CloudUpload,
                onDismiss = { showSyncedDialog = false },
                onConfirm = { showSyncedDialog = false },
                confirmText = "Great"
            )

        if (showNotifRationale && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            ElegantAlertDialog(
                title = "Enable notifications?",
                message = "Allow notifications to get alerts when offline items are uploaded.",
                icon = Icons.Rounded.Notifications,
                onDismiss = {
                    showNotifRationale = false
                    NotificationPrefs.markDeclined(context)
                },
                onConfirm = {
                    showNotifRationale = false
                    NotificationPrefs.markAsked(context)
                    notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                },
                confirmText = "Allow",
                dismissText = "Not now"
            )

        // --- Main content ---
        Column(
            Modifier
                .fillMaxSize()
                .padding(pad)
                .navigationBarsPadding()
        ) {
            // Search + Dynamic color toggle
            ProductsTopBar(
                query = query,
                onQueryChange = { query = it; vm.setQuery(it) },
                resultCount = resultCount,
                dynamicColor = dynamicColor,
                onToggleDynamicColor = onToggleDynamicColor,
                scrollBehavior = scrollBehavior
            )

            // Filter and sort row
            FilterRow(
                selectedType = selectedType,
                onTypeChange = { selectedType = it },
                sort = sort,
                onSortChange = { sort = it }
            )

            PullToRefreshBox(
                state = pullState,
                isRefreshing = isRefreshing,
                onRefresh = {
                    isRefreshing = true
                    vm.refresh()
                },
                modifier = Modifier.fillMaxSize()
            ) {
                Box(Modifier.fillMaxSize()) {
                    when (state) {
                        is Resource.Loading ->
                            if (!isRefreshing)
                                CircularProgressIndicator(Modifier.align(Alignment.Center))

                        is Resource.Error ->
                            ErrorSection(onRetry = { vm.refresh() })

                        is Resource.Success -> {
                            val sortedItems = sortAndFilter(filtered, sort)
                            if (sortedItems.isEmpty()) EmptySection()
                            else ProductList(sortedItems, listState)
                        }
                    }
                }
            }
        }
    }
}

/* ----- Helper UI sections ----- */

@Composable
fun ProductRow(p: Product) {
    val context = LocalContext.current
    val imageModel = p.image?.takeIf { it.isNotBlank() }

    // Controls the “shine” animation when card is tapped
    var shine by remember { mutableStateOf(false) }

    // Default placeholder gradient colors
    val placeholderPrimary = Color(138, 9, 95)
    val placeholderSecondary = MaterialTheme.colorScheme.errorContainer

    // Gradient colors extracted from image
    var c1 by remember(imageModel) { mutableStateOf(placeholderPrimary) }
    var c2 by remember(imageModel) { mutableStateOf(placeholderSecondary) }

    val onColor = remember(c1) { bestOnColor(c1) }
    val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                shine = true
                scope.launch {
                    delay(1100)
                    shine = false
                }
            },
        shape = RoundedCornerShape(
            topStart = 36.dp,
            bottomEnd = 36.dp,
            topEnd = 9.dp,
            bottomStart = 9.dp
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 32.dp)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(Brush.linearGradient(listOf(c1, c2)))
        ) {
            // Highlight animation overlay
            ShineEffect(
                modifier = Modifier
                    .matchParentSize()
                    .clip(
                        RoundedCornerShape(
                            topStart = 36.dp,
                            bottomEnd = 36.dp,
                            topEnd = 9.dp,
                            bottomStart = 9.dp
                        )
                    ),
                trigger = shine
            )

            // Product info column
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth(0.62f)
                    .padding(start = 24.dp, end = 20.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = p.name,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    color = onColor,
                    maxLines = 2,
                    modifier = Modifier.padding(start = 4.dp)
                )
                Spacer(Modifier.height(8.dp))
                Column(Modifier.padding(bottom = 8.dp)) {
                    MetaBullets(
                        text = p.type,
                        style = MaterialTheme.typography.bodySmall,
                        cardBg = onColor,
                        textColor = c1
                    )
                    Spacer(Modifier.height(4.dp))
                    MetaBullets(
                        text = "₹${p.price}",
                        style = MaterialTheme.typography.bodySmall,
                        cardBg = onColor,
                        textColor = c1
                    )
                    Spacer(Modifier.height(4.dp))
                    MetaBullets(
                        text = "${p.tax}% Tax",
                        style = MaterialTheme.typography.bodySmall,
                        cardBg = onColor,
                        textColor = c1
                    )
                }
            }

            // Pending badge if unsynced
            if (p.isPending) {
                PendingBadge(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                )
            }

            // Product image with color extraction
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageModel)
                    .scale(coil3.size.Scale.FIT)
                    .allowHardware(true)
                    .crossfade(true)
                    .listener(
                        onSuccess = { _, result ->
                            scope.launch {
                                val bmp = when (val img = result.image) {
                                    is BitmapImage -> img.bitmap.copy(
                                        Bitmap.Config.ARGB_8888,
                                        false
                                    )

                                    is DrawableImage -> img.drawable.toBitmap(300, 300)
                                    else -> null
                                } ?: return@launch
                                val (primary, secondary) = extractGradientColors(bmp)
                                c1 = primary
                                c2 = secondary
                            }
                        }
                    )
                    .build(),
                contentDescription = p.name,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.product_placeholder),
                error = painterResource(R.drawable.product_placeholder),
                modifier = Modifier
                    .fillMaxWidth(0.38f)
                    .fillMaxHeight()
                    .align(Alignment.BottomEnd)
                    .clip(RoundedCornerShape(topStart = 48.dp))
            )
        }
    }
}

@Composable
private fun MetaBullets(
    text: String,
    cardBg: Color,
    textColor: Color,
    style: TextStyle,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(
            topStart = 16.dp,
            bottomEnd = 16.dp,
            topEnd = 4.dp,
            bottomStart = 4.dp
        ),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = text, style = style, color = textColor, fontWeight = FontWeight.Bold)
        }
    }
}


// Observes WorkManager output to trigger sync callback
@Composable
private fun ObservePendingUploads(onSynced: (Int) -> Unit) {
    val ctx = LocalContext.current
    val workInfos by WorkManager.getInstance(ctx)
        .getWorkInfosByTagLiveData(UploadPendingWorker.TAG)
        .observeAsState()
    LaunchedEffect(workInfos) {
        val done = workInfos?.firstOrNull { it.state == WorkInfo.State.SUCCEEDED }
        val count = done?.outputData?.getInt("uploaded_count", 0) ?: 0
        if (count > 0) onSynced(count)
    }
}

@Composable
private fun ErrorSection(onRetry: () -> Unit) = Column(
    Modifier
        .fillMaxSize()
        .padding(24.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
) {
    Text("Failed to load.")
    Spacer(Modifier.height(12.dp))
    Button(onClick = onRetry) { Text("Retry") }
}

@Composable
private fun EmptySection() = Column(
    Modifier
        .fillMaxSize()
        .padding(24.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
) {
    Text("No products yet", style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(8.dp))
    Text("Tap the Add button to create your first product.")
}

@Composable
private fun ProductList(items: List<Product>, listState: LazyListState) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items) { p -> ProductRow(p) }
    }
}

/* Sorting logic */
private fun sortAndFilter(list: List<Product>, sort: String): List<Product> = when (sort) {
    "Low to High" -> list.sortedBy { it.price }
    "High to Low" -> list.sortedByDescending { it.price }
    "Name" -> list.sortedBy { it.name }
    else -> list
}
