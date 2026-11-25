package com.example.realestateandpropertymanagementsystem

import com.example.realestateandpropertymanagementsystem.util.MPesaParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class MPesaParserTest {

    @Test
    fun parseStandardReceivedSms_returnsCodeAndAmount() {
        val sms = "QBC123XYZ1 Confirmed. Ksh5,000.00 received from JANE DOE 0712345678 on 24/2/25 at 10:30 AM."
        val result = MPesaParser.parse(sms)

        assertNotNull(result)
        assertEquals("QBC123XYZ1", result?.code)
        assertEquals(5000, result?.amount)
        assertEquals("M-Pesa QBC123XYZ1", result?.formattedNote)
    }

    @Test
    fun parseSpaceSeparatedAmount_returnsCorrectAmount() {
        val sms = "RKD987ABC2 Confirmed. Ksh 15,500.00 received from JOHN SMITH."
        val result = MPesaParser.parse(sms)

        assertNotNull(result)
        assertEquals("RKD987ABC2", result?.code)
        assertEquals(15500, result?.amount)
    }

    @Test
    fun parseEmptySms_returnsNull() {
        assertNull(MPesaParser.parse(""))
    }
}
