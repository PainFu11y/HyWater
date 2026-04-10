package com.example.hywater.presentation.screens.add_fountain

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hywater.domain.usecase.AddFountainUseCase
import com.example.hywater.presentation.common.LocationHelper
import com.example.hywater.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddFountainViewModel @Inject constructor(
    private val addFountainUseCase: AddFountainUseCase,
    private val locationHelper: LocationHelper,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _submitState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val submitState: StateFlow<UiState<Unit>> = _submitState.asStateFlow()

    /** Current GPS coordinates; null until resolved from FusedLocationProvider. */
    private val _location = MutableStateFlow<Pair<Double, Double>?>(null)
    val location: StateFlow<Pair<Double, Double>?> = _location.asStateFlow()

    fun resolveLocation() {
        viewModelScope.launch {
            runCatching { locationHelper.getLastKnownOrCurrentLocation(context) }
                .onSuccess { loc ->
                    loc?.let { _location.value = Pair(it.latitude, it.longitude) }
                }
        }
    }

    fun submit(photoUri: Uri, description: String) {
        val coords = _location.value ?: run {
            _submitState.value = UiState.Error("Location not available. Please wait.")
            return
        }
        if (description.isBlank()) {
            _submitState.value = UiState.Error("Please add a description.")
            return
        }

        viewModelScope.launch {
            _submitState.value = UiState.Loading
            runCatching {
                addFountainUseCase(
                    photoUri = photoUri,
                    latitude = coords.first,
                    longitude = coords.second,
                    description = description.trim()
                )
            }
                .onSuccess { _submitState.value = UiState.Success(Unit) }
                .onFailure { _submitState.value = UiState.Error(it.message ?: "Upload failed") }
        }
    }
}
