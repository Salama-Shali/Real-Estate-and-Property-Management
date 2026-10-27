package com.example.realestateandpropertymanagementsystem.data
// Defines the Room database and provides access to the DAO.
import androidx.room.Database
import androidx.room.RoomDatabase

// Registers the tenant table and defines the database version.
@Database(
    entities = [Tenant::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // Provides access to the tenant database operations.
    abstract fun tenantDao(): TenantDao
}