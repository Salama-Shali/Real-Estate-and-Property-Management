package com.example.realestateandpropertymanagementsystem.util

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.realestateandpropertymanagementsystem.R
import java.util.concurrent.TimeUnit

// The three states a lease can be in.
enum class LeaseState { ACTIVE, EXPIRING_SOON, EXPIRED }

// Works out a lease's status from its end date. No database needed.
object LeaseStatus {

    // A lease ending within this many days counts as "expiring soon".
    const val EXPIRING_SOON_DAYS = 30

    // Days from now until the lease ends. Negative = already ended.
    fun daysLeft(leaseEnd: Long, now: Long = System.currentTimeMillis()): Int =
        TimeUnit.MILLISECONDS.toDays(leaseEnd - now).toInt()

    fun stateFor(leaseEnd: Long, now: Long = System.currentTimeMillis()): LeaseState {
        val days = daysLeft(leaseEnd, now)
        return when {
            days < 0 -> LeaseState.EXPIRED
            days <= EXPIRING_SOON_DAYS -> LeaseState.EXPIRING_SOON
            else -> LeaseState.ACTIVE
        }
    }

    // The text shown to the user, e.g. "Lease expiring soon: 12 days left".
    // leaseEnd is null for month-to-month tenancies, which have no expiry to count down to.
    fun label(context: Context, leaseEnd: Long?): String {
        if (leaseEnd == null) return context.getString(R.string.no_end_date)
        val days = daysLeft(leaseEnd)
        return when {
            days < 0 -> context.getString(R.string.lease_status_expired, -days)
            days == 0 -> context.getString(R.string.lease_status_today)
            days <= EXPIRING_SOON_DAYS -> context.getString(R.string.lease_status_expiring, days)
            else -> context.getString(R.string.lease_status_active, days)
        }
    }

    // Green, orange or red depending on the state; grey for month-to-month.
    fun color(context: Context, leaseEnd: Long?): Int {
        if (leaseEnd == null) return ContextCompat.getColor(context, android.R.color.darker_gray)
        val colorRes = when (stateFor(leaseEnd)) {
            LeaseState.ACTIVE -> R.color.lease_ok
            LeaseState.EXPIRING_SOON -> R.color.lease_warning
            LeaseState.EXPIRED -> R.color.lease_expired
        }
        return ContextCompat.getColor(context, colorRes)
    }
}