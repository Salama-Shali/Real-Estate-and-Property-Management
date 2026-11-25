package com.example.realestateandpropertymanagementsystem.util

/**
 * RentReminderLogic = the rule for "should we remind this tenant's rent?".
 *
 * It uses only plain numbers (no Android classes), so it can be tested on a laptop
 * without a phone. See RentReminderLogicTest.
 */
object RentReminderLogic {

    // We start reminding this many days BEFORE the due date (and on the due day itself).
    const val REMINDER_WINDOW_DAYS = 3

    /**
     * How many days from today until the rent is next due.
     *
     * @param dueDay      day of the month the rent is due (1-28)
     * @param today       today's day of the month (1-31)
     * @param daysInMonth how many days the current month has (28-31)
     *
     * Examples (30-day month):
     *   due 20, today 15 -> 5   (due later this month)
     *   due 15, today 15 -> 0   (due today)
     *   due 10, today 15 -> 25  (already passed, so next due date is next month)
     */
    fun daysUntilDue(dueDay: Int, today: Int, daysInMonth: Int): Int =
        if (dueDay >= today) {
            dueDay - today
        } else {
            // The due day already passed this month: count the rest of this month
            // plus the days into next month.
            dueDay + daysInMonth - today
        }

    // True when the due date is today or within the reminder window.
    fun isDueSoon(
        dueDay: Int,
        today: Int,
        daysInMonth: Int,
        windowDays: Int = REMINDER_WINDOW_DAYS
    ): Boolean = daysUntilDue(dueDay, today, daysInMonth) <= windowDays

    // Checks if the total payment made this month covers or exceeds the expected rent.
    fun isRentPaidForMonth(totalPaidThisMonth: Int, requiredRent: Int): Boolean =
        totalPaidThisMonth >= requiredRent

    // True when rent is due soon AND the tenant has not yet paid for the current month.
    fun isDueSoonAndUnpaid(
        dueDay: Int,
        today: Int,
        daysInMonth: Int,
        isPaid: Boolean,
        windowDays: Int = REMINDER_WINDOW_DAYS
    ): Boolean = !isPaid && isDueSoon(dueDay, today, daysInMonth, windowDays)

    // Calculates the timestamp in millis for 00:00:00 on the first day of the current month.
    fun getStartOfMonthMillis(calendar: java.util.Calendar = java.util.Calendar.getInstance()): Long {
        val cal = calendar.clone() as java.util.Calendar
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1)
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}