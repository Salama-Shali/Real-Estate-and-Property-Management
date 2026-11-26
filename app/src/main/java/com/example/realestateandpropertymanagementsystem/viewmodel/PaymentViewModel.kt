package com.example.realestateandpropertymanagementsystem.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.realestateandpropertymanagementsystem.data.AppDatabase
import com.example.realestateandpropertymanagementsystem.data.Payment
import com.example.realestateandpropertymanagementsystem.repository.PaymentRepository
import kotlinx.coroutines.launch

// Connects the payment screens to the payment repository.
class PaymentViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PaymentRepository

    init {
        val dao = AppDatabase.getDatabase(application).paymentDao()
        repository = PaymentRepository(dao)
    }

    // The UI observes this to show one tenant's payment history.
    fun getPayments(tenantId: Int): LiveData<List<Payment>> =
        repository.getPaymentsForTenant(tenantId)

    fun insert(payment: Payment) = viewModelScope.launch {
        repository.insertPayment(payment)
    }

    fun delete(payment: Payment) = viewModelScope.launch {
        repository.deletePayment(payment)
    }
}