package com.example.hywater.presentation.screens.map

import android.content.Context
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hywater.domain.model.Fountain
import com.example.hywater.domain.usecase.GetFountainsUseCase
import com.example.hywater.domain.repository.FountainRepository
import com.example.hywater.presentation.common.LocationHelper
import com.example.hywater.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getFountainsUseCase: GetFountainsUseCase,
    private val repository: FountainRepository,
    private val locationHelper: LocationHelper,
    @ApplicationContext private val context: Context
) : ViewModel() {

    // ── Fountains ─────────────────────────────────────────────────────────────

    /**
     * Converts the raw Flow<List<Fountain>> into a StateFlow<UiState<…>> so the
     * UI always has a concrete state to render.
     */
    val fountainsState: StateFlow<UiState<List<Fountain>>> =
        getFountainsUseCase()
            .map<List<Fountain>, UiState<List<Fountain>>> { UiState.Success(it) }
            .catch { emit(UiState.Error(it.message ?: "Unknown error")) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = UiState.Loading
            )

    // ── User location ─────────────────────────────────────────────────────────

    private val _userLocation = MutableStateFlow<Location?>(null)
    val userLocation: StateFlow<Location?> = _userLocation.asStateFlow()

    private val _locationError = MutableStateFlow<String?>(null)
    val locationError: StateFlow<String?> = _locationError.asStateFlow()

    // ── Init ──────────────────────────────────────────────────────────────────

    init {
        refresh()
    }

    /** Trigger a network refresh and update the Room cache. */
    fun refresh() {
        viewModelScope.launch {
            runCatching { repository.refreshFountains() }
                .onFailure { /* Cache already shown — silently log in production */ }
        }
    }

    /** Resolve the device's current position. Call after location permission is granted. */
    fun fetchUserLocation() {
        viewModelScope.launch {
            runCatching { locationHelper.getLastKnownOrCurrentLocation(context) }
                .onSuccess { _userLocation.value = it }
                .onFailure { _locationError.value = it.message }
        }
    }
}
