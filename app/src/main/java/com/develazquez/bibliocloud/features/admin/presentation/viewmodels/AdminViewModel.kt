package com.develazquez.bibliocloud.features.admin.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.develazquez.bibliocloud.features.admin.domain.repositories.AdminRepository
import com.develazquez.bibliocloud.features.auth.domain.entities.Usuario
import com.develazquez.bibliocloud.features.loans.domain.entities.Prestamo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AdminUiState {
    object Loading : AdminUiState()
    data class Success(val users: List<Usuario>, val loans: List<Prestamo>) : AdminUiState()
    data class Error(val message: String) : AdminUiState()
}

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val adminRepository: AdminRepository,
    private val logoutUseCase: com.develazquez.bibliocloud.features.auth.domain.usescases.LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AdminUiState>(AdminUiState.Loading)
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        fetchAdminData()
    }

    fun fetchAdminData() {
        viewModelScope.launch {
            _uiState.value = AdminUiState.Loading
            val usersResult = adminRepository.getAllUsersForAdmin()
            val loansResult = adminRepository.getAllLoansForAdmin()

            if (usersResult.isSuccess && loansResult.isSuccess) {
                _uiState.value = AdminUiState.Success(
                    users = usersResult.getOrNull() ?: emptyList(),
                    loans = loansResult.getOrNull() ?: emptyList()
                )
            } else {
                val errorMsg = usersResult.exceptionOrNull()?.message ?: loansResult.exceptionOrNull()?.message ?: "Error desconocido"
                _uiState.value = AdminUiState.Error(errorMsg)
            }
        }
    }

    fun updateUserRole(userId: String, newRole: String) {
        viewModelScope.launch {
            adminRepository.updateUserRole(userId, newRole)
            fetchAdminData() // Refresca los datos
        }
    }

    fun updateUserStatus(userId: String, newStatus: String) {
        viewModelScope.launch {
            adminRepository.updateUserStatus(userId, newStatus)
            fetchAdminData()
        }
    }

    fun markLoanAsReturned(loanId: String) {
        viewModelScope.launch {
            adminRepository.markLoanAsReturned(loanId)
            fetchAdminData()
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = logoutUseCase()
            if (result.isSuccess) {
                onSuccess()
            }
        }
    }
}
