package com.develazquez.bibliocloud.features.admin.data.remote

import com.develazquez.bibliocloud.data.remote.dto.PrestamoDto
import com.develazquez.bibliocloud.data.remote.dto.UsuarioDto
import retrofit2.Response
import retrofit2.http.*

interface AdminApiService {

    @GET("prestamos")
    suspend fun getAllLoansForAdmin(
        @Header("Authorization") token: String
    ): Response<List<PrestamoDto>>

    @POST("prestamos/{id}/devolver")
    suspend fun markLoanAsReturned(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<PrestamoDto>

    @GET("usuarios")
    suspend fun getAllUsersForAdmin(
        @Header("Authorization") token: String
    ): Response<List<UsuarioDto>>

    @PUT("usuarios/{id}")
    suspend fun updateUserRole(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body body: UsuarioDto
    ): Response<UsuarioDto>

    @PUT("usuarios/{id}")
    suspend fun updateUserStatus(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body body: UsuarioDto
    ): Response<UsuarioDto>
}
