// Purpose: Connects the app's logic to tenant database operations.
package com.example.realestateandpropertymanagementsystem.repository

import androidx.lifecycle.LiveData
import com.example.realestateandpropertymanagementsystem.data.Tenant
import com.example.realestateandpropertymanagementsystem.data.TenantDao

// Provides a single place for the app to access tenant data.
class TenantRepository(private val tenantDao: TenantDao) {

    // Gets the list of tenants from the database.
    val allTenants: LiveData<List<Tenant>> =
        tenantDao.getAllTenants()

    // Sends a new tenant to the database for storage.
    suspend fun insertTenant(tenant: Tenant) {
        tenantDao.insertTenant(tenant)
    }

    // Sends changes to an existing tenant to the database.
    suspend fun updateTenant(tenant: Tenant) {
        tenantDao.updateTenant(tenant)
    }

    // Request removal of a tenant from the database.
    suspend fun deleteTenant(tenant: Tenant) {
        tenantDao.deleteTenant(tenant)
    }

    // Gets one tenant by id.
    fun getTenantById(id: Int): LiveData<Tenant?> = tenantDao.getTenantById(id)

    // True if an ACTIVE tenant already lives in this unit.
    // excludeId = the tenant being edited (0 when adding a new one).
    suspend fun isUnitOccupied(unit: String, excludeId: Int = 0): Boolean =
        tenantDao.countActiveTenantsInUnit(unit.trim(), excludeId) > 0
}