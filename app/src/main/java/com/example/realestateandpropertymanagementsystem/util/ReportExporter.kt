package com.example.realestateandpropertymanagementsystem.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.realestateandpropertymanagementsystem.data.Payment
import com.example.realestateandpropertymanagementsystem.data.Tenant
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ReportExporter {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    fun generateCsvContent(tenantsMap: Map<Int, Tenant>, payments: List<Payment>): String {
        val sb = StringBuilder()
        sb.append("Payment ID,Tenant Name,Unit,Amount (KES),Payment Date,Note\n")

        for (payment in payments) {
            val tenant = tenantsMap[payment.tenantId]
            val tenantName = tenant?.name ?: "Unknown"
            val unit = tenant?.unit ?: "N/A"
            val dateStr = dateFormat.format(Date(payment.paymentDate))
            val note = payment.note.replace(",", " ")

            sb.append("${payment.id},\"$tenantName\",\"$unit\",${payment.amount},\"$dateStr\",\"$note\"\n")
        }
        return sb.toString()
    }

    fun shareCsvReport(context: Context, tenantsMap: Map<Int, Tenant>, payments: List<Payment>) {
        val csvText = generateCsvContent(tenantsMap, payments)
        val file = File(context.cacheDir, "renttrack_payments_report.csv")
        file.writeText(csvText)

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_SUBJECT, "RentTrack Payments Report")
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(shareIntent, "Share Rent Track Report"))
    }
}
