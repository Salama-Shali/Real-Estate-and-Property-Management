
//  Defines the information stored for each tenant.
package com.example.realestateandpropertymanagementsystem.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Room uses this class to define the tenants database table.
@Entity(tableName = "tenants")
data class Tenant(
    // Room automatically generates a unique ID for each new record.
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    //information about tenant
    val name: String,
    val unit: String,
    val rent: Int
)