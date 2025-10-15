package com.iamashad.ashad_swipe

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.iamashad.ashad_swipe.util.ThemePrefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Holds global app-level state such as splash readiness and theme preference.
 * Shared between composables via Koin/Hilt or a top-level Activity viewModel.
 */
class AppViewModel(app: Application) : AndroidViewModel(app) {

    private val _isAppReady = MutableStateFlow(false)
    val isAppReady: StateFlow<Boolean> = _isAppReady

    val dynamicColor: StateFlow<Boolean> =
        ThemePrefs.dynamicColorFlow(app)
            .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    /** Called from the Activity to warm up DataStore and dismiss splash safely. */
    fun warmUpIfNeeded() {
        if (_isAppReady.value) return
        viewModelScope.launch {
            ThemePrefs.dynamicColorFlow(getApplication()).first()
            _isAppReady.value = true
        }
    }

    /** Updates the user’s dynamic color preference in DataStore. */
    fun setDynamicColor(enabled: Boolean) {
        viewModelScope.launch {
            ThemePrefs.setDynamicColor(getApplication(), enabled)
        }
    }
}
