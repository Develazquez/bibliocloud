package com.develazquez.bibliocloud.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.develazquez.bibliocloud.domain.usecase.RegisterUseCase
import com.develazquez.bibliocloud.presentation.state.RegisterState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    // Mantenemos tu estado
    var state by mutableStateOf(RegisterState())
        private set

    fun onNombreChanged(nuevo: String) {
        state = state.copy(nombre = nuevo)
    }

    // Cambiamos el nombre de la función para que sea más claro que es un Email
    fun onEmailChanged(nuevo: String) {
        state = state.copy(telefono = nuevo) // Seguimos guardándolo en 'telefono' para no romper tu RegisterState
    }

    fun onClaveChanged(nuevo: String) {
        state = state.copy(clave = nuevo)
    }

    fun registrarse() {
        // Validación: verificamos que 'telefono' (donde ahora va el email) no esté vacío
        if (state.nombre.isBlank() || state.telefono.isBlank() || state.clave.isBlank()) {
            state = state.copy(mensajeError = "Por favor, completa todos los campos")
            return
        }

        state = state.copy(estaCargando = true, mensajeError = null)

        viewModelScope.launch {
            // Aquí ocurre la sustitución:
            // Pasamos lo que hay en 'state.telefono' al parámetro 'email' del UseCase
            val result = registerUseCase(
                nombre = state.nombre,
                email = state.telefono,
                password = state.clave
            )

            if (result.isSuccess) {
                state = state.copy(estaCargando = false)
                println("¡Registro exitoso para la API!")
            } else {
                val error = result.exceptionOrNull()?.message ?: "Error al crear cuenta"
                state = state.copy(estaCargando = false, mensajeError = error)
            }
        }
    }
}