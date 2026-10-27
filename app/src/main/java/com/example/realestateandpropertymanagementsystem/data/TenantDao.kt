package com.example.realestateandpropertymanagementsystem.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

// Tells Room that this interface defines database operations.
@Dao
interface TenantDao {

    // Saves a new tenant record.
    @Insert
    suspend fun insertTenant(tenant: Tenant)

    // Retrieves all tenants, ordered by name.
    @Query("SELECT * FROM tenants ORDER BY name ASC")
    fun getAllTenants(): LiveData<List<Tenant>>

    // Saves changes made to an existing tenant.
    @Update
    suspend fun updateTenant(tenant: Tenant)

    // Removes a tenant record from the database.
    @Delete
    suspend fun deleteTenant(tenant: Tenant)
}