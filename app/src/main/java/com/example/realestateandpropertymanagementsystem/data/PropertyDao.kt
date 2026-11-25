package com.example.realestateandpropertymanagementsystem.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PropertyDao {

    @Insert
    suspend fun insertProperty(property: Property): Long

    @Query("SELECT * FROM properties ORDER BY name ASC")
    fun getAllProperties(): LiveData<List<Property>>

    @Delete
    suspend fun deleteProperty(property: Property)
}
