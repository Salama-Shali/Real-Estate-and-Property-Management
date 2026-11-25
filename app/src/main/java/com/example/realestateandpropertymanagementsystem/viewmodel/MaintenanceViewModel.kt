package com.example.realestateandpropertymanagementsystem.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.realestateandpropertymanagementsystem.data.AppDatabase
import com.example.realestateandpropertymanagementsystem.data.MaintenanceRequest
import com.example.realestateandpropertymanagementsystem.repository.MaintenanceRepository
import kotlinx.coroutines.launch

class MaintenanceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MaintenanceRepository
    val allRequests: LiveData<List<MaintenanceRequest>>

    init {
        val dao = AppDatabase.getDatabase(application).maintenanceRequestDao()
        repository = MaintenanceRepository(dao)
        allRequests = repository.allRequests
    }

    fun getRequestsForTenant(tenantId: Int): LiveData<List<MaintenanceRequest>> =
        repository.getRequestsForTenant(tenantId)

    fun insert(request: MaintenanceRequest) = viewModelScope.launch {
        repository.insert(request)
    }

    fun update(request: MaintenanceRequest) = viewModelScope.launch {
        repository.update(request)
    }

    fun delete(request: MaintenanceRequest) = viewModelScope.launch {
        repository.delete(request)
    }
}
