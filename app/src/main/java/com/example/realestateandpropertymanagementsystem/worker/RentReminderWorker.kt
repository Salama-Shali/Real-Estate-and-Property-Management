package com.example.realestateandpropertymanagementsystem.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.realestateandpropertymanagementsystem.MainActivity
import com.example.realestateandpropertymanagementsystem.R
import com.example.realestateandpropertymanagementsystem.data.AppDatabase
import com.example.realestateandpropertymanagementsystem.data.Tenant
import com.example.realestateandpropertymanagementsystem.repository.TenantRepository
import com.example.realestateandpropertymanagementsystem.util.RentReminderLogic
import java.util.Calendar

/**
 * RentReminderWorker = the background job that checks who owes rent soon.
 *
 * WorkManager creates this class and calls doWork() when it is time, even if
 * the app is closed. CoroutineWorker lets doWork() call "suspend" functions,
 * so the database is read off the main thread.
 */
class RentReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // 1. Get the active tenants and payment DAO from the database.
        val db = AppDatabase.getDatabase(applicationContext)
        val tenants = TenantRepository(db.tenantDao()).getActiveTenants()
        val paymentDao = db.paymentDao()

        // 2. Today's day of the month, how many days this month has, and start of current month.
        val today = Calendar.getInstance()
        val dayOfMonth = today.get(Calendar.DAY_OF_MONTH)
        val daysInMonth = today.getActualMaximum(Calendar.DAY_OF_MONTH)
        val startOfMonth = RentReminderLogic.getStartOfMonthMillis(today)

        // 3. Keep only tenants whose rent is due today or within the reminder window AND haven't paid yet.
        val dueSoon = tenants.filter { tenant ->
            val paymentsThisMonth = paymentDao.getPaymentsForTenantSince(tenant.id, startOfMonth)
            val totalPaid = paymentsThisMonth.sumOf { it.amount }
            val isPaid = RentReminderLogic.isRentPaidForMonth(totalPaid, tenant.rent)
            RentReminderLogic.isDueSoonAndUnpaid(tenant.rentDueDay, dayOfMonth, daysInMonth, isPaid)
        }

        // 4. Tell the landlord, but only if there is something to report.
        if (dueSoon.isNotEmpty()) showNotification(dueSoon)

        // success() = job finished normally, nothing needs to be retried.
        return Result.success()
    }

    private fun showNotification(tenants: List<Tenant>) {
        val context = applicationContext

        // Android 8+ requires every notification to belong to a "channel"
        // (the user can mute a channel in system settings).
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.reminder_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            context.getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }

        // Android 13+: if the user denied the permission, show nothing.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) return

        // Tapping the notification opens the app.
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Names shown when the notification is expanded, e.g. "Jane Wanjiru (A1), ..."
        val names = tenants.joinToString(", ") { "${it.name} (${it.unit})" }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(context.getString(R.string.reminder_title))
            .setContentText(
                context.getString(
                    R.string.reminder_text,
                    tenants.size,
                    RentReminderLogic.REMINDER_WINDOW_DAYS
                )
            )
            .setStyle(NotificationCompat.BigTextStyle().bigText(names))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // dismiss when tapped
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val CHANNEL_ID = "rent_reminders"
        private const val NOTIFICATION_ID = 1001
    }
}