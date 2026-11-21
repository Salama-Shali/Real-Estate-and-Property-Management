package com.example.realestateandpropertymanagementsystem.repository

import androidx.lifecycle.LiveData
import com.example.realestateandpropertymanagementsystem.data.Payment
import com.example.realestateandpropertymanagementsystem.data.PaymentDao

// Single place the app gets payment data from.
class PaymentRepository(private val paymentDao: PaymentDao) {

    fun getPaymentsForTenant(tenantId: Int): LiveData<List<Payment>> =
        paymentDao.getPaymentsForTenant(tenantId)

    suspend fun insertPayment(payment: Payment) {
        paymentDao.insertPayment(payment)
    }

    suspend fun deletePayment(payment: Payment) {
        paymentDao.deletePayment(payment)
    }
}