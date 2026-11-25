package com.example.realestateandpropertymanagementsystem

import com.example.realestateandpropertymanagementsystem.util.RentReminderLogic
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for RentReminderLogic. They run on the laptop (no phone needed).
 * Each @Test is one scenario: we give inputs and check the answer is what we expect.
 */
class RentReminderLogicTest {

    @Test
    fun rentDueToday_isZeroDaysAway() {
        assertEquals(0, RentReminderLogic.daysUntilDue(dueDay = 15, today = 15, daysInMonth = 30))
    }

    @Test
    fun rentDueLaterThisMonth_countsDaysRemaining() {
        assertEquals(5, RentReminderLogic.daysUntilDue(dueDay = 20, today = 15, daysInMonth = 30))
    }

    @Test
    fun rentDueDayAlreadyPassed_countsIntoNextMonth() {
        // 15 -> 30 is 15 days, plus 10 days into next month = 25
        assertEquals(25, RentReminderLogic.daysUntilDue(dueDay = 10, today = 15, daysInMonth = 30))
    }

    @Test
    fun februaryRollover_usesShortMonthLength() {
        // Today 27 Feb (28 days), due on the 1st: 1 + 28 - 27 = 2 days away
        assertEquals(2, RentReminderLogic.daysUntilDue(dueDay = 1, today = 27, daysInMonth = 28))
    }

    @Test
    fun dueInTwoDays_isDueSoon() {
        assertTrue(RentReminderLogic.isDueSoon(dueDay = 17, today = 15, daysInMonth = 30))
    }

    @Test
    fun dueInExactlyWindowDays_isDueSoon() {
        assertTrue(RentReminderLogic.isDueSoon(dueDay = 18, today = 15, daysInMonth = 30))
    }

    @Test
    fun dueInTenDays_isNotDueSoon() {
        assertFalse(RentReminderLogic.isDueSoon(dueDay = 25, today = 15, daysInMonth = 30))
    }

    @Test
    fun rentPaidForMonth_returnsTrueWhenPaidEqualOrMore() {
        assertTrue(RentReminderLogic.isRentPaidForMonth(totalPaidThisMonth = 15000, requiredRent = 15000))
        assertTrue(RentReminderLogic.isRentPaidForMonth(totalPaidThisMonth = 20000, requiredRent = 15000))
        assertFalse(RentReminderLogic.isRentPaidForMonth(totalPaidThisMonth = 10000, requiredRent = 15000))
    }

    @Test
    fun dueSoonAndUnpaid_returnsFalseIfAlreadyPaid() {
        // Due today (due 15, today 15), but paid
        assertFalse(RentReminderLogic.isDueSoonAndUnpaid(dueDay = 15, today = 15, daysInMonth = 30, isPaid = true))
        // Due today, unpaid
        assertTrue(RentReminderLogic.isDueSoonAndUnpaid(dueDay = 15, today = 15, daysInMonth = 30, isPaid = false))
    }
}