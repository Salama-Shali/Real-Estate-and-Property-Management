package com.example.realestateandpropertymanagementsystem.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.realestateandpropertymanagementsystem.data.AppDatabase
import com.example.realestateandpropertymanagementsystem.data.Property
import com.example.realestateandpropertymanagementsystem.repository.PropertyRepository
import kotlinx.coroutines.launch

class PropertyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PropertyRepository
    val allProperties: LiveData<List<Property>>

    init {
        val dao = AppDatabase.getDatabase(application).propertyDao()
        repository = PropertyRepository(dao)
        allProperties = repository.allProperties
    }

    fun insert(property: Property) = viewModelScope.launch {
        repository.insertProperty(property)
    }

    fun delete(property: Property) = viewModelScope.launch {
        repository.deleteProperty(property)
    }
}
