package com.example.realestateandpropertymanagementsystem.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface MaintenanceRequestDao {

    @Insert
    suspend fun insert(request: MaintenanceRequest)

    @Update
    suspend fun update(request: MaintenanceRequest)

    @Delete
    suspend fun delete(request: MaintenanceRequest)

    @Query("SELECT * FROM maintenance_requests ORDER BY dateReported DESC")
    fun getAllRequests(): LiveData<List<MaintenanceRequest>>

    @Query("SELECT * FROM maintenance_requests WHERE tenantId = :tenantId ORDER BY dateReported DESC")
    fun getRequestsForTenant(tenantId: Int): LiveData<List<MaintenanceRequest>>
}
