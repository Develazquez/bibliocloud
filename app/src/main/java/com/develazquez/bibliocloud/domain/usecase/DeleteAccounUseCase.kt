package com.develazquez.bibliocloud.domain.usecase

import com.develazquez.bibliocloud.domain.repository.AuthRepository
import javax.inject.Inject

// Añadimos @Inject constructor para que Hilt sepa cómo crear esta clase
class DeleteAccountUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> = repository.deleteAccount()
}