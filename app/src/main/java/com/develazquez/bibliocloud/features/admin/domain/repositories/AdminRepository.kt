package com.develazquez.bibliocloud.features.admin.domain.repositories

import com.develazquez.bibliocloud.features.auth.domain.entities.Usuario
import com.develazquez.bibliocloud.features.loans.domain.entities.Prestamo

interface AdminRepository {
    suspend fun getAllLoansForAdmin(): Result<List<Prestamo>>
    suspend fun getAllUsersForAdmin(): Result<List<Usuario>>
    suspend fun updateUserRole(userId: String, newRole: String): Result<Usuario>
    suspend fun updateUserStatus(userId: String, newStatus: String): Result<Usuario>
    suspend fun markLoanAsReturned(loanId: String): Result<Prestamo>
}
