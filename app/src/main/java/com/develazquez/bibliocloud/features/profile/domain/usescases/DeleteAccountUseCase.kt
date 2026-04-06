package com.develazquez.bibliocloud.features.profile.domain.usescases

import com.develazquez.bibliocloud.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

// Añadimos @Inject constructor para que Hilt sepa cómo crear esta clase
class DeleteAccountUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> = repository.deleteAccount()
}
