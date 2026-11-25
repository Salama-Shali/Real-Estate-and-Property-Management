package com.example.realestateandpropertymanagementsystem.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PaymentDao {

    // Saves a new payment.
    @Insert
    suspend fun insertPayment(payment: Payment)

    // All payments of ONE tenant, newest first.
    @Query("SELECT * FROM payments WHERE tenantId = :tenantId ORDER BY paymentDate DESC")
    fun getPaymentsForTenant(tenantId: Int): LiveData<List<Payment>>

    // Payments for ONE tenant made since a specific timestamp (e.g., start of month)
    @Query("SELECT * FROM payments WHERE tenantId = :tenantId AND paymentDate >= :startOfMonth ORDER BY paymentDate DESC")
    suspend fun getPaymentsForTenantSince(tenantId: Int, startOfMonth: Long): List<Payment>

    // All payments made by any tenant since a specific timestamp
    @Query("SELECT * FROM payments WHERE paymentDate >= :startOfMonth")
    suspend fun getAllPaymentsSince(startOfMonth: Long): List<Payment>

    // All payments across all tenants, newest first.
    @Query("SELECT * FROM payments ORDER BY paymentDate DESC")
    fun getAllPayments(): LiveData<List<Payment>>

    // Removes a payment (for mistakes).
    @Delete
    suspend fun deletePayment(payment: Payment)
}