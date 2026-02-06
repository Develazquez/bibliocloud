package com.develazquez.bibliocloud.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.develazquez.bibliocloud.domain.usecase.LoginUseCase
import com.develazquez.bibliocloud.presentation.state.LoginState // Asegúrate que esta ruta sea correcta
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    // Mantenemos tu estado de Compose para que tu diseño siga funcionando igual
    var state by mutableStateOf(LoginState())
        private set

    fun onEmailChanged(nuevoEmail: String) {
        state = state.copy(email = nuevoEmail)
    }

    fun onPasswordChanged(nuevaClave: String) {
        state = state.copy(password = nuevaClave)
    }

    // Tu función de botón "Entrar" ahora es REAL
    fun onLoginClick() {
        if (state.email.isBlank() || state.password.isBlank()) {
            state = state.copy(errorMessage = "¡Oye! No dejes campos vacíos")
            return
        }

        // Activamos el cargando y limpiamos errores
        state = state.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            // Llamamos al UseCase que programó tu amigo
            val result = loginUseCase(state.email, state.password)

            if (result.isSuccess) {
                state = state.copy(isLoading = false)
                // Aquí podrías activar un flag para navegar al Inicio (CatalogScreen)
                println("¡Login exitoso para: ${result.getOrNull()?.usuario?.nombre}!")
            } else {
                // Si la API falla, mostramos el error en tu diseño
                val errorMsg = result.exceptionOrNull()?.message ?: "Error desconocido"
                state = state.copy(isLoading = false, errorMessage = errorMsg)
            }
        }
    }
}