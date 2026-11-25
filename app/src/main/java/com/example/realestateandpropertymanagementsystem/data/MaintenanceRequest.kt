package com.example.realestateandpropertymanagementsystem.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "maintenance_requests",
    foreignKeys = [
        ForeignKey(
            entity = Tenant::class,
            parentColumns = ["id"],
            childColumns = ["tenantId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("tenantId")]
)
data class MaintenanceRequest(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tenantId: Int,
    val description: String,
    val status: String = "Pending",
    val dateReported: Long = System.currentTimeMillis()
)
