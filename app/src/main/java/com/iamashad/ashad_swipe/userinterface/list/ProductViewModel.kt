package com.iamashad.ashad_swipe.userinterface.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iamashad.ashad_swipe.domain.model.Product
import com.iamashad.ashad_swipe.domain.repo.Resource
import com.iamashad.ashad_swipe.domain.repo.SwipeRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class ProductListViewModel(private val repo: SwipeRepository) : ViewModel() {

    // User search input (debounced to avoid spamming recompositions)
    private val query = MutableStateFlow("")

    // Public UI state stream
    val state: StateFlow<Resource<List<Product>>> =
        repo.observeProducts()
            .combine(
                query
                    .debounce(250)
                    .map { it.trim() }
                    .distinctUntilChanged()
            ) { res, q ->
                when (res) {
                    is Resource.Success -> {
                        val qLower = q.lowercase()

                        // 1) De-dupe by a stable key; prefer non-pending over pending
                        val deduped = res.data
                            .groupBy(::stableKey)
                            .map { (_, items) -> items.minBy { if (it.isPending) 1 else 0 } }

                        // 2) Filter (case-insensitive) — cheap lowercase once
                        val filtered = if (qLower.isEmpty()) {
                            deduped
                        } else {
                            deduped.filter { p ->
                                p.name.lowercase().contains(qLower) ||
                                        p.type.lowercase().contains(qLower)
                            }
                        }

                        Resource.Success(filtered)
                    }
                    else -> res // Loading/Error pass-through
                }
            }
            // Keep active while observed; fall back to Loading
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = Resource.Loading
            )

    // Builds a stable identity for deduping products
    private fun stableKey(p: Product): String = buildString {
        append(p.name.trim())
        append('|'); append(p.type.trim())
        append('|'); append(p.price) // numbers already stable in domain
        append('|'); append(p.tax)
    }

    // Two small helpers for UI
    fun setQuery(q: String) { query.value = q }
    fun refresh() = viewModelScope.launch { repo.refresh() }
}
