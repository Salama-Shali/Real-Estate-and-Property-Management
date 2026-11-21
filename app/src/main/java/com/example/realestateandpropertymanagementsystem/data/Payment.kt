package com.example.realestateandpropertymanagementsystem.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// One row = one rent payment made by one tenant.
// tenantId links each payment to its tenant (a foreign key).
// CASCADE means deleting a tenant also deletes that tenant's payments.
@Entity(
    tableName = "payments",
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
data class Payment(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tenantId: Int,
    val amount: Int,
    val paymentDate: Long,   // milliseconds, same as the lease dates
    val note: String = ""
)