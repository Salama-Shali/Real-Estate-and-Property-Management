package com.example.realestateandpropertymanagementsystem

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.realestateandpropertymanagementsystem.worker.ReminderScheduler

class MainActivity : AppCompatActivity() {

    // Controls moving between screens; the toolbar's Up arrow uses it.
    private lateinit var navController: NavController

    // Handles the system's "Allow notifications?" dialog. We don't need to react to the
    // answer: if denied, the worker simply shows no notification.
    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Use our MaterialToolbar as the screen's action bar.
        setSupportActionBar(findViewById(R.id.toolbar))

        // Link the toolbar to navigation: it shows each screen's title and an Up arrow
        // on every screen except the start (Dashboard).
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)

        askForNotificationPermission()
        // Schedule the daily background reminder (does nothing if already scheduled).
        ReminderScheduler.schedule(this)
    }

    // Called when the Up arrow is tapped: go back one screen.
    override fun onSupportNavigateUp(): Boolean =
        navController.navigateUp() || super.onSupportNavigateUp()

    // Android 13+ (API 33) requires the user's permission before showing notifications.
    private fun askForNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}