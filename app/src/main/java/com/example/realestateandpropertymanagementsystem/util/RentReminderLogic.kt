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
}