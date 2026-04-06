package com.develazquez.bibliocloud.features.auth.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.develazquez.bibliocloud.features.auth.domain.usescases.RegisterUseCase
import com.develazquez.bibliocloud.core.ui.state.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun register(nombre: String, email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val result = registerUseCase(nombre, email, password)

            _authState.value = if (result.isSuccess) {
                AuthState.Success(result.getOrNull()!!.usuario)
            } else {
                AuthState.Error(result.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
