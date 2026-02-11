package com.develazquez.bibliocloud.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.develazquez.bibliocloud.domain.usecase.DeleteAccountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val deleteAccountUseCase: DeleteAccountUseCase
) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var deleteResult by mutableStateOf<Result<Unit>?>(null)
        private set

    fun deleteAccount() {
        viewModelScope.launch {
            isLoading = true
            deleteResult = deleteAccountUseCase()
            isLoading = false
        }
    }

    fun resetState() {
        deleteResult = null
    }
}