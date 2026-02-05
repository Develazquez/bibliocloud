package com.develazquez.bibliocloud.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.develazquez.bibliocloud.domain.usecase.GetAvailableResourcesUseCase
import com.develazquez.bibliocloud.presentation.state.RecursoState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val getAvailableResourcesUseCase: GetAvailableResourcesUseCase
) : ViewModel() {

    private val _recursoState = MutableStateFlow<RecursoState>(RecursoState.Idle)
    val recursoState: StateFlow<RecursoState> = _recursoState.asStateFlow()

    init {
        loadRecursos()
    }

    fun loadRecursos() {
        viewModelScope.launch {
            _recursoState.value = RecursoState.Loading

            val result = getAvailableResourcesUseCase()

            _recursoState.value = if (result.isSuccess) {
                RecursoState.Success(result.getOrNull() ?: emptyList())
            } else {
                RecursoState.Error(result.exceptionOrNull()?.message ?: "Error al cargar recursos")
            }
        }
    }

    fun refresh() {
        loadRecursos()
    }
}