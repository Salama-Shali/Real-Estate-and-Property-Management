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

    // Removes a payment (for mistakes).
    @Delete
    suspend fun deletePayment(payment: Payment)
}