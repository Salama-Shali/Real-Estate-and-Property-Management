package com.example.realestateandpropertymanagementsystem.util

data class MPesaResult(
    val code: String,
    val amount: Int?,
    val sender: String?,
    val formattedNote: String
)

object MPesaParser {

    private val codeRegex = Regex("""^([A-Z0-9]{10})\s+Confirmed""", RegexOption.IGNORE_CASE)
    private val amountRegex = Regex("""Ksh\s*([\d,]+(?:\.\d{2})?)""", RegexOption.IGNORE_CASE)
    private val senderRegex = Regex("""received from\s+([A-Z\s]+(?:\d{10,12})?)""", RegexOption.IGNORE_CASE)

    fun parse(smsText: String): MPesaResult? {
        val trimmed = smsText.trim()
        if (trimmed.isEmpty()) return null

        val codeMatch = codeRegex.find(trimmed)
        val code = codeMatch?.groupValues?.get(1) ?: extractFirstWordAsCode(trimmed)

        val amountMatch = amountRegex.find(trimmed)
        val amountString = amountMatch?.groupValues?.get(1)?.replace(",", "")
        val amount = amountString?.toDoubleOrNull()?.toInt()

        val senderMatch = senderRegex.find(trimmed)
        val sender = senderMatch?.groupValues?.get(1)?.trim()

        if (code.isEmpty() && amount == null) return null

        val note = if (code.isNotEmpty()) "M-Pesa $code" else "M-Pesa Payment"
        return MPesaResult(
            code = code,
            amount = amount,
            sender = sender,
            formattedNote = note
        )
    }

    private fun extractFirstWordAsCode(text: String): String {
        val firstWord = text.split(Regex("""\s+""")).firstOrNull() ?: ""
        return if (firstWord.length >= 8 && firstWord.all { it.isLetterOrDigit() }) firstWord.uppercase() else ""
    }
}
