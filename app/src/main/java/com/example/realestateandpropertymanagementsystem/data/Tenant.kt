package com.example.realestateandpropertymanagementsystem.data
//  Defines the information stored for each tenant
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
    val rent: Int,
    val phone: String,
    val email: String,
    val rentDueDay: Int,
    val leaseStartDate: Long,
    val leaseEndDate: Long,
    val isActive: Boolean = true // false = tenant has moved out, but we keep their record and payment history
)