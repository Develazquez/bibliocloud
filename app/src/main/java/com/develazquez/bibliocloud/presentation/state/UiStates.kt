package com.develazquez.bibliocloud.presentation.state

import com.develazquez.bibliocloud.domain.model.Prestamo
import com.develazquez.bibliocloud.domain.model.Recurso
import com.develazquez.bibliocloud.domain.model.Usuario

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val usuario: Usuario) : AuthState()
    data class Error(val message: String) : AuthState()
}

sealed class RecursoState {
    object Idle : RecursoState()
    object Loading : RecursoState()
    data class Success(val recursos: List<Recurso>) : RecursoState()
    data class Error(val message: String) : RecursoState()
}

sealed class RecursoDetailState {
    object Idle : RecursoDetailState()
    object Loading : RecursoDetailState()
    data class Success(val recurso: Recurso) : RecursoDetailState()
    data class Error(val message: String) : RecursoDetailState()
}

sealed class LoanProcessState {
    object Idle : LoanProcessState()
    object Loading : LoanProcessState()
    data class ValidationError(val message: String) : LoanProcessState()
    data class AvailabilityError(val message: String) : LoanProcessState()
    data class Success(val prestamo: Prestamo) : LoanProcessState()
    data class Error(val message: String) : LoanProcessState()
}

sealed class MyLoansState {
    object Idle : MyLoansState()
    object Loading : MyLoansState()
    data class Success(val prestamos: List<Prestamo>) : MyLoansState()
    data class Error(val message: String) : MyLoansState()
}