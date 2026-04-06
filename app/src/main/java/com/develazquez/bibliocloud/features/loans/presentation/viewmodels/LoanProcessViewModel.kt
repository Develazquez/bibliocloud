package com.develazquez.bibliocloud.features.loans.presentation.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.develazquez.bibliocloud.features.loans.domain.usescases.BorrowItemUseCase
import com.develazquez.bibliocloud.features.loans.domain.usescases.ReturnItemUseCase
import com.develazquez.bibliocloud.core.ui.state.LoanProcessState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoanProcessViewModel @Inject constructor(
    private val borrowItemUseCase: BorrowItemUseCase,
    private val returnItemUseCase: ReturnItemUseCase
) : ViewModel() {

    private val _loanState = MutableStateFlow<LoanProcessState>(LoanProcessState.Idle)
    val loanState: StateFlow<LoanProcessState> = _loanState.asStateFlow()

    fun solicitarPrestamo(recursoId: String) {
        viewModelScope.launch {
            _loanState.value = LoanProcessState.Loading

            val result = borrowItemUseCase(recursoId)

            _loanState.value = if (result.isSuccess) {
                LoanProcessState.Success(result.getOrNull()!!)
            } else {
                val errorMessage = result.exceptionOrNull()?.message ?: "Error desconocido"

                when {
                    errorMessage.contains("no está disponible") ->
                        LoanProcessState.AvailabilityError(errorMessage)
                    errorMessage.contains("Límite") || errorMessage.contains("deudor") ->
                        LoanProcessState.ValidationError(errorMessage)
                    else ->
                        LoanProcessState.Error(errorMessage)
                }
            }
        }
    }

    fun devolverPrestamo(prestamoId: String) {
        viewModelScope.launch {
            _loanState.value = LoanProcessState.Loading

            val result = returnItemUseCase(prestamoId)

            _loanState.value = if (result.isSuccess) {
                LoanProcessState.Success(result.getOrNull()!!)
            } else {
                LoanProcessState.Error(result.exceptionOrNull()?.message ?: "Error al devolver")
            }
        }
    }

    fun resetState() {
        _loanState.value = LoanProcessState.Idle
    }
}
