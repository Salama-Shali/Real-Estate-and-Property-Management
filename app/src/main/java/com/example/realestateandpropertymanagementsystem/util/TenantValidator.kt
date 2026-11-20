package com.example.realestateandpropertymanagementsystem.util

import androidx.annotation.StringRes
import com.example.realestateandpropertymanagementsystem.R

/**
 * TenantValidator = all the rules for checking what the user typed in the tenant form.
 *
 * Why a separate object (not inside the Fragment)?
 *  - The screen stays short and only deals with display.
 *  - These rules are plain Kotlin, so we can unit-test them later without a phone.
 *
 * Every function returns:
 *   null          -> the input is valid
 *   a string id   -> the input is invalid; the id is the error message to show
 * (R.string.xxx is an Int, so the return type is Int?)
 *
 * "object" means there is only ONE of these; we call it directly:
 * TenantValidator.validatePhone("0712345678")
 */
object TenantValidator {

    // Kenyan mobile numbers: 07xxxxxxxx or 01xxxxxxxx (10 digits),
    // or +2547xxxxxxxx / +2541xxxxxxxx / 2547xxxxxxxx.
    // ^ and $ mean "the WHOLE text must match", [17] = a 1 or a 7, \d{8} = 8 digits.
    private val PHONE_REGEX = Regex("^(\\+?254|0)[17]\\d{8}$")

    // Simple email shape: something@something.something
    private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    // Name must not be empty or only spaces.
    @StringRes
    fun validateName(name: String): Int? =
        if (name.isBlank()) R.string.error_name_required else null

    // Unit must not be empty or only spaces.
    @StringRes
    fun validateUnit(unit: String): Int? =
        if (unit.isBlank()) R.string.error_unit_required else null

    // Rent comes from a text box as a String, so convert it to a number to check it.
    @StringRes
    fun validateRent(rentText: String): Int? {
        val text = rentText.trim()
        if (text.isEmpty()) return R.string.error_rent_required
        val rent = text.toIntOrNull()            // null if it isn't a whole number
        return if (rent == null || rent <= 0) R.string.error_rent_invalid else null
    }

    // Spaces are allowed while typing ("0712 345 678"), so remove them before checking.
    @StringRes
    fun validatePhone(phone: String): Int? {
        val cleaned = phone.replace(" ", "")
        return when {
            cleaned.isEmpty() -> R.string.error_phone_required
            !PHONE_REGEX.matches(cleaned) -> R.string.error_phone_invalid
            else -> null
        }
    }

    // Email is OPTIONAL (many tenants have none), but if typed it must look like an email.
    @StringRes
    fun validateEmail(email: String): Int? {
        val text = email.trim()
        if (text.isEmpty()) return null
        return if (EMAIL_REGEX.matches(text)) null else R.string.error_email_invalid
    }

    // Due day is limited to 1-28 so it exists in every month, including February.
    @StringRes
    fun validateDueDay(dayText: String): Int? {
        val text = dayText.trim()
        if (text.isEmpty()) return R.string.error_due_day_required
        val day = text.toIntOrNull()
        return if (day == null || day !in 1..28) R.string.error_due_day_invalid else null
    }

    // Dates are Long? : null means "nothing picked yet".
    @StringRes
    fun validateLeaseStart(start: Long?): Int? =
        if (start == null) R.string.error_lease_start_required else null

    // End date is only required for FIXED-TERM leases. Month-to-month tenancies have none.
    // hasFixedEnd = false means the box "Month-to-month" is ticked, so nothing to check.
    @StringRes
    fun validateLeaseEnd(start: Long?, end: Long?, hasFixedEnd: Boolean): Int? = when {
        !hasFixedEnd -> null
        end == null -> R.string.error_lease_end_required
        start != null && end <= start -> R.string.error_lease_end_before_start
        else -> null
    }
}