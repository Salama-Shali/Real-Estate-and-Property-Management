package com.example.realestateandpropertymanagementsystem.repository

import androidx.lifecycle.LiveData
import com.example.realestateandpropertymanagementsystem.data.MaintenanceRequest
import com.example.realestateandpropertymanagementsystem.data.MaintenanceRequestDao

class MaintenanceRepository(private val dao: MaintenanceRequestDao) {

    val allRequests: LiveData<List<MaintenanceRequest>> = dao.getAllRequests()

    fun getRequestsForTenant(tenantId: Int): LiveData<List<MaintenanceRequest>> =
        dao.getRequestsForTenant(tenantId)

    suspend fun insert(request: MaintenanceRequest) {
        dao.insert(request)
    }

    suspend fun update(request: MaintenanceRequest) {
        dao.update(request)
    }

    suspend fun delete(request: MaintenanceRequest) {
        dao.delete(request)
    }
}
