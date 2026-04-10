package com.example.hywater.presentation.common

/**
 * Generic wrapper for UI state in every ViewModel.
 * ViewModels expose StateFlow<UiState<T>> and the UI renders accordingly.
 */
sealed class UiState<out T> {
    data object Idle : UiState<Nothing>()
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
