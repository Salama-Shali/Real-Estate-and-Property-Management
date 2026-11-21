package com.example.realestateandpropertymanagementsystem.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * ReminderScheduler = the one place that tells WorkManager when to run the reminder job.
 */
object ReminderScheduler {

    private const val WORK_NAME = "daily_rent_reminder"

    // Runs RentReminderWorker about once every 24 hours, even when the app is closed.
    // KEEP means: if this job is already scheduled, don't create a duplicate.
    // (We call schedule() every time the app opens, so this matters.)
    fun schedule(context: Context) {
        val request = PeriodicWorkRequestBuilder<RentReminderWorker>(1, TimeUnit.DAYS).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    // Runs the job ONCE, right now. Used by the "Check reminders" button,
    // which is also handy for demonstrating the feature live.
    fun checkNow(context: Context) {
        WorkManager.getInstance(context).enqueue(OneTimeWorkRequestBuilder<RentReminderWorker>().build())
    }
}