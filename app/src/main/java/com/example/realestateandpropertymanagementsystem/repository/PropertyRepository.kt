package com.example.realestateandpropertymanagementsystem.repository

import androidx.lifecycle.LiveData
import com.example.realestateandpropertymanagementsystem.data.Property
import com.example.realestateandpropertymanagementsystem.data.PropertyDao

class PropertyRepository(private val propertyDao: PropertyDao) {

    val allProperties: LiveData<List<Property>> = propertyDao.getAllProperties()

    suspend fun insertProperty(property: Property): Long {
        return propertyDao.insertProperty(property)
    }

    suspend fun deleteProperty(property: Property) {
        propertyDao.deleteProperty(property)
    }
}
