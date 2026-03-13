package com.develazquez.bibliocloud.data.remote

import com.develazquez.bibliocloud.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface BiblioCloudApiService {


    @POST("usuarios/login")
    suspend fun login(@Body request: LoginRequestDto): Response<LoginResponseDto>

    @POST("usuarios")
    suspend fun register(@Body request: RegisterRequestDto): Response<LoginResponseDto>

    @GET("usuarios/{id}")
    suspend fun getCurrentUser(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<UsuarioDto>

    @GET("usuarios")
    suspend fun getAllUsers(@Header("Authorization") token: String): Response<List<UsuarioDto>>

    @PUT("usuarios/{id}")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body usuario: UsuarioDto
    ): Response<UsuarioDto>



    @DELETE("usuarios/{id}")
    suspend fun deleteUser(
        @Header("Authorization") token: String,
        @Path("id") id: String?
    ): Response<Unit>
    // ============ RECURSOS ============

    @GET("recursos")
    suspend fun getRecursos(@Header("Authorization") token: String): Response<List<RecursoDto>>

    @GET("recursos/{id}")
    suspend fun getRecursoById(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<RecursoDto>

    @POST("recursos")
    suspend fun createRecurso(
        @Header("Authorization") token: String,
        @Body recurso: RecursoDto
    ): Response<RecursoDto>

    @PUT("recursos/{id}")
    suspend fun updateRecurso(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body recurso: RecursoDto
    ): Response<RecursoDto>

    @DELETE("recursos/{id}")
    suspend fun deleteRecurso(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<Unit>


    // ============ PRÉSTAMOS ============

    @POST("prestamos")
    suspend fun solicitarPrestamo(
        @Header("Authorization") token: String,
        @Body request: SolicitarPrestamoRequestDto
    ): Response<PrestamoDto>

    @GET("prestamos")
    suspend fun getAllPrestamos(@Header("Authorization") token: String): Response<List<PrestamoDto>>

    @GET("prestamos/{id}")
    suspend fun getPrestamoById(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<PrestamoDto>

    @PUT("prestamos/{id}")
    suspend fun updatePrestamo(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body prestamo: PrestamoDto
    ): Response<PrestamoDto>

    @DELETE("prestamos/{id}")
    suspend fun deletePrestamo(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<Unit>

    @POST("prestamos/{id}/devolver")
    suspend fun devolverPrestamo(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<PrestamoDto>
}