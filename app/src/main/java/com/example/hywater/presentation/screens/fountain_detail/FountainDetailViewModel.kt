package com.example.hywater.presentation.screens.fountain_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hywater.domain.model.Fountain
import com.example.hywater.domain.usecase.GetFountainByIdUseCase
import com.example.hywater.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FountainDetailViewModel @Inject constructor(
    private val getFountainByIdUseCase: GetFountainByIdUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<Fountain>>(UiState.Loading)
    val state: StateFlow<UiState<Fountain>> = _state.asStateFlow()

    fun load(fountainId: Long) {
        // Guard against re-loading if data is already present
        if (_state.value is UiState.Success) return

        viewModelScope.launch {
            _state.value = UiState.Loading
            runCatching { getFountainByIdUseCase(fountainId) }
                .onSuccess { _state.value = UiState.Success(it) }
                .onFailure { _state.value = UiState.Error(it.message ?: "Failed to load fountain") }
        }
    }
}
