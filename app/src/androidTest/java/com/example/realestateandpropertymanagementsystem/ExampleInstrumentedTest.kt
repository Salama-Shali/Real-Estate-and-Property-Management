package com.example.realestateandpropertymanagementsystem

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
import androidx.room.Room
import com.example.realestateandpropertymanagementsystem.data.AppDatabase
import com.example.realestateandpropertymanagementsystem.data.Payment
import com.example.realestateandpropertymanagementsystem.data.Tenant
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    private lateinit var database: AppDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun useAppContext() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.realestateandpropertymanagementsystem", appContext.packageName)
    }

    @Test
    fun insertAndReadTenantAndPayment() = runBlocking {
        val tenantDao = database.tenantDao()
        val paymentDao = database.paymentDao()

        val tenant = Tenant(
            name = "Jane Doe",
            unit = "A1",
            rent = 12000,
            phone = "0712345678",
            email = "jane@example.com",
            rentDueDay = 5,
            leaseStartDate = System.currentTimeMillis(),
            leaseEndDate = null
        )

        val id = tenantDao.insertTenant(tenant).toInt()
        assertTrue(id > 0)

        val payment = Payment(
            tenantId = id,
            amount = 12000,
            paymentDate = System.currentTimeMillis(),
            note = "M-Pesa QBC123XYZ"
        )
        paymentDao.insertPayment(payment)

        val paymentsThisMonth = paymentDao.getPaymentsForTenantSince(id, 0L)
        assertEquals(1, paymentsThisMonth.size)
        assertEquals(12000, paymentsThisMonth[0].amount)
    }
}