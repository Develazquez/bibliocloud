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
import com.develazquez.bibliocloud.core.network.TokenManager
import com.develazquez.bibliocloud.features.admin.domain.repositories.AdminRepository
import com.develazquez.bibliocloud.features.loans.domain.entities.EstadoPrestamo
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class AdminAlertWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val adminRepository: AdminRepository,
    private val tokenManager: TokenManager,
    private val sharedPrefs: SharedPreferences
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        try {
            if (tokenManager.getToken() == null || tokenManager.getUserRole() != "ADMIN") {
                return Result.success()
            }

            val loansResult = adminRepository.getAllLoansForAdmin()
            if (loansResult.isSuccess) {
                val loans = loansResult.getOrNull() ?: emptyList()
                val totalLoans = loans.size
                val expiredLoansCount = loans.count { it.estado == EstadoPrestamo.VENCIDO }

                // 1. Notificar sobre préstamos vencidos
                if (expiredLoansCount > 0) {
                    val lastNotifiedExpiredKey = "admin_last_expired_count"
                    val lastExpiredCount = sharedPrefs.getInt(lastNotifiedExpiredKey, 0)
                    
                    if (expiredLoansCount > lastExpiredCount) {
                        showNotification(
                            999,
                            "Alerta: Préstamos Vencidos",
                            "Hay $expiredLoansCount préstamos vencidos en el sistema."
                        )
                    }
                    sharedPrefs.edit().putInt(lastNotifiedExpiredKey, expiredLoansCount).apply()
                }

                // 2. Notificar sobre nuevos préstamos (incremento en el total)
                val lastTotalLoansKey = "admin_last_total_loans"
                val lastTotal = sharedPrefs.getInt(lastTotalLoansKey, 0)

                if (totalLoans > lastTotal && lastTotal != 0) { // lastTotal != 0 para evitar notificar la primera vez que inicia la app
                    val diff = totalLoans - lastTotal
                    val message = if (diff == 1) "Se ha realizado 1 nuevo préstamo." else "Se han realizado $diff nuevos préstamos."
                    showNotification(
                        888,
                        "Nuevo Préstamo",
                        message
                    )
                }
                sharedPrefs.edit().putInt(lastTotalLoansKey, totalLoans).apply()
            }
            return Result.success()
        } catch (e: Exception) {
            return Result.retry()
        }
    }

    private fun showNotification(id: Int, title: String, message: String) {
        val channelId = "admin_alerts_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alertas Admin",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(id, notification)
    }
}
