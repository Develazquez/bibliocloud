package com.develazquez.bibliocloud.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.develazquez.bibliocloud.domain.usecase.GetMyLoansUseCase
import com.develazquez.bibliocloud.presentation.state.MyLoansState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyLoansViewModel @Inject constructor(
    private val getMyLoansUseCase: GetMyLoansUseCase
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

            _loansState.value = if (result.isSuccess) {
                MyLoansState.Success(result.getOrNull() ?: emptyList())
            } else {
                MyLoansState.Error(result.exceptionOrNull()?.message ?: "Error al cargar préstamos")
            }
        }
    }

    fun refresh() {
        loadMyLoans()
    }
}