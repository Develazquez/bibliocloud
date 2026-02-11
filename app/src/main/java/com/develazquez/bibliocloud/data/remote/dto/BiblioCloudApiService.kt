package com.develazquez.bibliocloud.data.remote

import com.develazquez.bibliocloud.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface BiblioCloudApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<LoginResponseDto>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequestDto): Response<LoginResponseDto>

    @GET("auth/me")
    suspend fun getCurrentUser(@Header("Authorization") token: String): Response<UsuarioDto>

    // NUEVO: Endpoint para borrar el usuario actual en el backend de Go
    @DELETE("auth/me")
    suspend fun deleteUser(@Header("Authorization") token: String): Response<Unit>

    @GET("recursos")
    suspend fun getRecursos(@Header("Authorization") token: String): Response<List<RecursoDto>>

    @GET("recursos/disponibles")
    suspend fun getRecursosDisponibles(@Header("Authorization") token: String): Response<List<RecursoDto>>

    @GET("recursos/{id}")
    suspend fun getRecursoById(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<RecursoDto>

    @GET("recursos/buscar")
    suspend fun buscarRecursos(
        @Header("Authorization") token: String,
        @Query("q") query: String
    ): Response<List<RecursoDto>>

    @GET("recursos/categoria/{categoria}")
    suspend fun getRecursosPorCategoria(
        @Header("Authorization") token: String,
        @Path("categoria") categoria: String
    ): Response<List<RecursoDto>>

    @POST("prestamos")
    suspend fun solicitarPrestamo(
        @Header("Authorization") token: String,
        @Body request: SolicitarPrestamoRequestDto
    ): Response<PrestamoDto>

    @PUT("prestamos/{id}/devolver")
    suspend fun devolverPrestamo(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<PrestamoDto>

    @GET("prestamos/mis-prestamos")
    suspend fun getMisPrestamos(@Header("Authorization") token: String): Response<List<PrestamoDto>>

    @GET("prestamos/{id}")
    suspend fun getPrestamoById(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<PrestamoDto>
}