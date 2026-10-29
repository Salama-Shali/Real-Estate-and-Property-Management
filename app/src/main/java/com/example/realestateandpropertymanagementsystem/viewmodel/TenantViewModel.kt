package com.example.realestateandpropertymanagementsystem.viewmodel
//connects UI to tenant repo
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.realestateandpropertymanagementsystem.data.AppDatabase
import com.example.realestateandpropertymanagementsystem.data.Tenant
import com.example.realestateandpropertymanagementsystem.repository.TenantRepository
import kotlinx.coroutines.launch


class TenantViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TenantRepository

    // LiveData updates automatically whenever the table changes.
    val allTenants: LiveData<List<Tenant>>

    init {
        // Get the DAO from the database and pass it to the repository.
        val dao = AppDatabase.getDatabase(application).tenantDao()
        repository = TenantRepository(dao)
        allTenants = repository.allTenants //list of tenants for ui to observe
    }

    // Save a tenant without blocking the UI thread.
    fun insert(tenant: Tenant) = viewModelScope.launch {
        repository.insertTenant(tenant)
    }
    // Request an update through the repo
    fun update(tenant: Tenant) = viewModelScope.launch {
        repository.updateTenant(tenant)
    }
    // Request deletion through the repo
    fun delete(tenant: Tenant) = viewModelScope.launch {
        repository.deleteTenant(tenant)
    }
}