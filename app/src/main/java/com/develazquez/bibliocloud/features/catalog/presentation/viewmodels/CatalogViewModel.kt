package com.develazquez.bibliocloud.features.catalog.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.develazquez.bibliocloud.core.hardware.NetworkMonitor
import com.develazquez.bibliocloud.features.catalog.domain.entities.Book
import com.develazquez.bibliocloud.features.catalog.domain.usescases.GetAvailableBooksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CatalogUiState(
    val books: List<Book> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val getAvailableBooksUseCase: GetAvailableBooksUseCase,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _uiState = MutableStateFlow(CatalogUiState())
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

    val isConnected: StateFlow<Boolean> = networkMonitor.isConnected

    init {
        loadBooks()
    }

    fun loadBooks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = getAvailableBooksUseCase()
            result.onSuccess { books ->
                _uiState.update { it.copy(isLoading = false, books = books) }
            }.onFailure { exception ->
                _uiState.update { it.copy(isLoading = false, error = exception.message ?: "Error desconocido") }
            }
        }
    }
}
