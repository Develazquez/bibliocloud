package com.develazquez.bibliocloud.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.develazquez.bibliocloud.domain.usecase.LoginUseCase
import com.develazquez.bibliocloud.presentation.state.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val result = loginUseCase(email, password)

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