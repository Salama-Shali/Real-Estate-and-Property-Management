package com.example.realestateandpropertymanagementsystem.data
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Room
import android.content.Context

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// Registers database tables and handles database versioning & migrations.
@Database(
    entities = [Tenant::class, Payment::class, Property::class, MaintenanceRequest::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun tenantDao(): TenantDao
    abstract fun paymentDao(): PaymentDao
    abstract fun propertyDao(): PropertyDao
    abstract fun maintenanceRequestDao(): MaintenanceRequestDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `properties` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `address` TEXT NOT NULL
                    )
                """.trimIndent())

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `maintenance_requests` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `tenantId` INTEGER NOT NULL,
                        `description` TEXT NOT NULL,
                        `status` TEXT NOT NULL,
                        `dateReported` INTEGER NOT NULL,
                        FOREIGN KEY(`tenantId`) REFERENCES `tenants`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_maintenance_requests_tenantId` ON `maintenance_requests` (`tenantId`)")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "real_estate_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}