package com.develazquez.bibliocloud.core.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.develazquez.bibliocloud.R
import com.develazquez.bibliocloud.core.network.TokenManager
import com.develazquez.bibliocloud.features.loans.domain.entities.EstadoPrestamo
import com.develazquez.bibliocloud.data.remote.BiblioCloudApiService
import com.develazquez.bibliocloud.data.remote.mapper.toDomain
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.Date
import java.util.concurrent.TimeUnit

@HiltWorker
class LoanExpiryWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val apiService: BiblioCloudApiService,
    private val tokenManager: TokenManager,
    private val sharedPrefs: SharedPreferences
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        try {
            val token = tokenManager.getToken() ?: return Result.success()

            val response = apiService.getAllPrestamos("Bearer $token")
            if (response.isSuccessful) {
                val prestamos = response.body()?.map { it.toDomain() } ?: emptyList()
                val currentUserId = tokenManager.getUserId()
                
                val userActiveLoans = prestamos.filter { 
                    it.usuarioId == currentUserId && 
                    (it.estado == EstadoPrestamo.ACTIVO || it.estado == EstadoPrestamo.ATRASADO)
                }

                val now = Date().time
                val threshold = TimeUnit.HOURS.toMillis(24) // 24 hours

                userActiveLoans.forEach { prestamo ->
                    val diff = prestamo.fechaFinPrevista.time - now
                    if (diff in 0..threshold) {
                        val notifiedKey = "notified_loan_${prestamo.id}"
                        if (!sharedPrefs.getBoolean(notifiedKey, false)) {
                            showNotification(
                                prestamo.id.hashCode(),
                                "Préstamo próximo a vencer",
                                "Tu préstamo de ${prestamo.recurso?.titulo ?: "un recurso"} vencerá pronto. Devuélvelo para no perder acceso."
                            )
                            sharedPrefs.edit().putBoolean(notifiedKey, true).apply()
                        }
                    }
                }
            }
            return Result.success()
        } catch (e: Exception) {
            return Result.retry()
        }
    }

    private fun showNotification(id: Int, title: String, message: String) {
        val channelId = "loan_alerts_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alertas de Préstamos",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_alert) // Fallback icon
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(id, notification)
    }
}
