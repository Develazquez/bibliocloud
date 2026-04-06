package com.develazquez.bibliocloud.features.catalog.data.datasources

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET

data class BookDto(
    @SerializedName("id") val id: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("author") val author: String?,
    @SerializedName("coverUrl") val coverUrl: String?,
    @SerializedName("audioUrl") val audioUrl: String?,
    @SerializedName("available") val isAvailable: Boolean?,
    @SerializedName("loanedByMe") val isLoanedByMe: Boolean?
)

interface CatalogApiService {
    @GET("books/available")
    suspend fun getAvailableBooks(): List<BookDto>
}
