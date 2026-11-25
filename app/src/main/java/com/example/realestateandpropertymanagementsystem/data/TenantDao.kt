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

    // Saves a new tenant record and returns the generated row ID.
    @Insert
    suspend fun insertTenant(tenant: Tenant): Long

    // Retrieves all tenants, ordered by name.
    @Query("SELECT * FROM tenants ORDER BY name ASC")
    fun getAllTenants(): LiveData<List<Tenant>>

    // Saves changes made to an existing tenant.
    @Update
    suspend fun updateTenant(tenant: Tenant)

    // Removes a tenant record from the database.
    @Delete
    suspend fun deleteTenant(tenant: Tenant)

    // Finds one tenant by id (used to prefill the edit form).
    @Query("SELECT * FROM tenants WHERE id = :id")
    fun getTenantById(id: Int): LiveData<Tenant?>

    // Counts ACTIVE tenants already living in this unit.
    // - COLLATE NOCASE makes "b4" and "B4" count as the same unit.
    // - excludeId skips one tenant: when EDITING, a tenant must not clash with themselves.
    //   (When adding a new tenant we pass 0, and no real tenant has id 0.)
    @Query("SELECT COUNT(*) FROM tenants WHERE unit = :unit COLLATE NOCASE AND isActive = 1 AND id != :excludeId")
    suspend fun countActiveTenantsInUnit(unit: String, excludeId: Int): Int

    // One-time read of all ACTIVE tenants (not LiveData).
    // Used by the background reminder worker, which just needs the list once and then finishes.
    @Query("SELECT * FROM tenants WHERE isActive = 1 ORDER BY rentDueDay ASC")
    suspend fun getActiveTenants(): List<Tenant>
}