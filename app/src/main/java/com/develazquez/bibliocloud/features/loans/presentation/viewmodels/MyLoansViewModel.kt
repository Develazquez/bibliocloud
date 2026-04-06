package com.develazquez.bibliocloud.features.loans.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.develazquez.bibliocloud.features.loans.domain.usescases.GetMyLoansUseCase
import com.develazquez.bibliocloud.features.catalog.domain.repositories.RecursoRepository
import com.develazquez.bibliocloud.core.ui.state.MyLoansState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyLoansViewModel @Inject constructor(
    private val getMyLoansUseCase: GetMyLoansUseCase,
    private val recursoRepository: RecursoRepository
) : ViewModel() {

    private val _loansState = MutableStateFlow<MyLoansState>(MyLoansState.Idle)
    val loansState: StateFlow<MyLoansState> = _loansState.asStateFlow()

    init {
        loadMyLoans()
    }

    fun loadMyLoans() {
        viewModelScope.launch {
            _loansState.value = MyLoansState.Loading

            val result = getMyLoansUseCase()

            if (result.isSuccess) {
                val prestamos = result.getOrNull() ?: emptyList()
                
                // Fetch recursos to populate the prestamo.recurso if missing
                val recursosResult = recursoRepository.getRecursos()
                val recursos = recursosResult.getOrNull() ?: emptyList()
                
                val prestamosEnriquecidos = prestamos.map { prestamo ->
                    if (prestamo.recurso == null) {
                        prestamo.copy(recurso = recursos.find { it.id == prestamo.recursoId })
                    } else {
                        prestamo
                    }
                }
                
                _loansState.value = MyLoansState.Success(prestamosEnriquecidos)
            } else {
                _loansState.value = MyLoansState.Error(result.exceptionOrNull()?.message ?: "Error al cargar préstamos")
            }
        }
    }

    fun refresh() {
        loadMyLoans()
    }
}
