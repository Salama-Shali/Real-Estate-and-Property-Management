package com.example.realestateandpropertymanagementsystem

import com.example.realestateandpropertymanagementsystem.data.Payment
import com.example.realestateandpropertymanagementsystem.data.Tenant
import com.example.realestateandpropertymanagementsystem.util.ReportExporter
import org.junit.Assert.assertTrue
import org.junit.Test

class ReportExporterTest {

    @Test
    fun generateCsvContent_containsHeadersAndPaymentDetails() {
        val tenant = Tenant(id = 1, name = "Alice Wanjiru", unit = "B2", rent = 10000, phone = "0711223344", email = "alice@example.com", rentDueDay = 5, leaseStartDate = 0L, leaseEndDate = null)
        val tenantsMap = mapOf(1 to tenant)
        val payment = Payment(id = 101, tenantId = 1, amount = 10000, paymentDate = 1700000000000L, note = "Rent Payment")

        val csv = ReportExporter.generateCsvContent(tenantsMap, listOf(payment))

        assertTrue(csv.contains("Payment ID,Tenant Name,Unit,Amount (KES),Payment Date,Note"))
        assertTrue(csv.contains("Alice Wanjiru"))
        assertTrue(csv.contains("B2"))
        assertTrue(csv.contains("10000"))
        assertTrue(csv.contains("Rent Payment"))
    }
}
