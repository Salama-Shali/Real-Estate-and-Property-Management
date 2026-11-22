package com.example.realestateandpropertymanagementsystem

import com.example.realestateandpropertymanagementsystem.util.TenantValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Unit tests for TenantValidator, the rules behind the tenant form.
 *
 * Each validate...() function returns:
 *   null          -> the input is valid
 *   R.string.xxx  -> the input is invalid (this is the error message shown)
 * So a "valid" test uses assertNull, and an "invalid" test checks the exact error message.
 */
class TenantValidatorTest {

    // ---------- Name and unit ----------

    @Test
    fun emptyName_isRejected() {
        assertEquals(R.string.error_name_required, TenantValidator.validateName(""))
    }

    @Test
    fun nameOfOnlySpaces_isRejected() {
        assertEquals(R.string.error_name_required, TenantValidator.validateName("   "))
    }

    @Test
    fun normalName_isAccepted() {
        assertNull(TenantValidator.validateName("Jane Wanjiru"))
    }

    @Test
    fun emptyUnit_isRejected() {
        assertEquals(R.string.error_unit_required, TenantValidator.validateUnit(""))
    }

    @Test
    fun normalUnit_isAccepted() {
        assertNull(TenantValidator.validateUnit("A1"))
    }

    // ---------- Rent ----------

    @Test
    fun emptyRent_isRejected() {
        assertEquals(R.string.error_rent_required, TenantValidator.validateRent(""))
    }

    @Test
    fun rentThatIsNotANumber_isRejected() {
        assertEquals(R.string.error_rent_invalid, TenantValidator.validateRent("abc"))
    }

    @Test
    fun rentWithDecimalPoint_isRejected() {
        assertEquals(R.string.error_rent_invalid, TenantValidator.validateRent("1.5"))
    }

    @Test
    fun zeroRent_isRejected() {
        assertEquals(R.string.error_rent_invalid, TenantValidator.validateRent("0"))
    }

    @Test
    fun negativeRent_isRejected() {
        assertEquals(R.string.error_rent_invalid, TenantValidator.validateRent("-500"))
    }

    @Test
    fun normalRent_isAccepted() {
        assertNull(TenantValidator.validateRent("15000"))
    }

    // ---------- Phone ----------

    @Test
    fun emptyPhone_isRejected() {
        assertEquals(R.string.error_phone_required, TenantValidator.validatePhone(""))
    }

    @Test
    fun tooShortPhone_isRejected() {
        assertEquals(R.string.error_phone_invalid, TenantValidator.validatePhone("12345"))
    }

    @Test
    fun phoneWithTooManyDigits_isRejected() {
        assertEquals(R.string.error_phone_invalid, TenantValidator.validatePhone("07123456789"))
    }

    @Test
    fun phoneWithWrongPrefix_isRejected() {
        assertEquals(R.string.error_phone_invalid, TenantValidator.validatePhone("0812345678"))
    }

    @Test
    fun localKenyanNumber_isAccepted() {
        assertNull(TenantValidator.validatePhone("0712345678"))
    }

    @Test
    fun numberStartingWithZeroOne_isAccepted() {
        assertNull(TenantValidator.validatePhone("0112345678"))
    }

    @Test
    fun numberWithSpaces_isAccepted() {
        assertNull(TenantValidator.validatePhone("0712 345 678"))
    }

    @Test
    fun internationalNumber_isAccepted() {
        assertNull(TenantValidator.validatePhone("+254712345678"))
    }

    // ---------- Email (optional) ----------

    @Test
    fun emptyEmail_isAccepted_becauseItIsOptional() {
        assertNull(TenantValidator.validateEmail(""))
    }

    @Test
    fun emailWithoutAtSign_isRejected() {
        assertEquals(R.string.error_email_invalid, TenantValidator.validateEmail("abc"))
    }

    @Test
    fun emailWithoutDomainEnding_isRejected() {
        assertEquals(R.string.error_email_invalid, TenantValidator.validateEmail("jane@example"))
    }

    @Test
    fun normalEmail_isAccepted() {
        assertNull(TenantValidator.validateEmail("jane@example.com"))
    }

    // ---------- Rent due day (1-28) ----------

    @Test
    fun emptyDueDay_isRejected() {
        assertEquals(R.string.error_due_day_required, TenantValidator.validateDueDay(""))
    }

    @Test
    fun dueDayZero_isRejected() {
        assertEquals(R.string.error_due_day_invalid, TenantValidator.validateDueDay("0"))
    }

    @Test
    fun dueDay29_isRejected() {
        assertEquals(R.string.error_due_day_invalid, TenantValidator.validateDueDay("29"))
    }

    @Test
    fun dueDayThatIsNotANumber_isRejected() {
        assertEquals(R.string.error_due_day_invalid, TenantValidator.validateDueDay("abc"))
    }

    @Test
    fun firstAndLastAllowedDueDays_areAccepted() {
        assertNull(TenantValidator.validateDueDay("1"))
        assertNull(TenantValidator.validateDueDay("28"))
    }

    // ---------- Lease dates ----------

    @Test
    fun missingLeaseStart_isRejected() {
        assertEquals(R.string.error_lease_start_required, TenantValidator.validateLeaseStart(null))
    }

    @Test
    fun pickedLeaseStart_isAccepted() {
        assertNull(TenantValidator.validateLeaseStart(1_000L))
    }

    @Test
    fun fixedTermLease_withoutEndDate_isRejected() {
        assertEquals(
            R.string.error_lease_end_required,
            TenantValidator.validateLeaseEnd(start = 1_000L, end = null, hasFixedEnd = true)
        )
    }

    @Test
    fun monthToMonthLease_needsNoEndDate() {
        // "Month-to-month" ticked: hasFixedEnd = false, so a missing end date is fine.
        assertNull(TenantValidator.validateLeaseEnd(start = 1_000L, end = null, hasFixedEnd = false))
    }

    @Test
    fun leaseEndingBeforeItStarts_isRejected() {
        assertEquals(
            R.string.error_lease_end_before_start,
            TenantValidator.validateLeaseEnd(start = 2_000L, end = 1_000L, hasFixedEnd = true)
        )
    }

    @Test
    fun leaseEndingTheSameInstantItStarts_isRejected() {
        assertEquals(
            R.string.error_lease_end_before_start,
            TenantValidator.validateLeaseEnd(start = 1_000L, end = 1_000L, hasFixedEnd = true)
        )
    }

    @Test
    fun leaseEndingAfterItStarts_isAccepted() {
        assertNull(TenantValidator.validateLeaseEnd(start = 1_000L, end = 2_000L, hasFixedEnd = true))
    }
}