package com.example.realestateandpropertymanagementsystem.data
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Room
import android.content.Context

// Registers the tenant table and defines the database version.
// Defines the Room database and provides access to the DAO.
@Database(
    entities = [Tenant::class, Payment::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // Provides access to the tenant database operations.
    abstract fun tenantDao(): TenantDao

    abstract fun paymentDao(): PaymentDao

    companion object {
        // Holds the database instance so we can reuse it.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Gets the existing database or creates it when needed.
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "real_estate_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}