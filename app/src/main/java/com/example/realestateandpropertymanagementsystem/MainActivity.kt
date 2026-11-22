package com.example.realestateandpropertymanagementsystem

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.navOptions
import com.example.realestateandpropertymanagementsystem.worker.ReminderScheduler
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.ui.navigateUp

class MainActivity : AppCompatActivity() {

    // Controls moving between screens; the toolbar and bottom bar both use it.
    private lateinit var navController: NavController

    // Says which screens are "top level" (reached from the bottom bar, no Up arrow).
    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var navHost: View

    // Height of the phone's own navigation bar (gesture area or buttons) at the bottom.
    private var bottomInset = 0

    // Handles the system's "Allow notifications?" dialog. We don't need to react to the
    // answer: if denied, the worker simply shows no notification.
    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // White status-bar icons, because the toolbar behind them is dark navy.
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.bottom_nav)
        navHost = findViewById(R.id.nav_host_fragment)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // The status-bar space sits behind the coloured toolbar.
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            // The bottom space goes under the bottom bar, or under the screen when the bar is hidden.
            bottomInset = systemBars.bottom
            applyBottomInset()
            insets
        }

        // Use our MaterialToolbar as the screen's action bar.
        setSupportActionBar(findViewById(R.id.toolbar))

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Dashboard and Tenants are the top-level screens: they get no Up arrow.
        appBarConfiguration =
            AppBarConfiguration(setOf(R.id.dashboardFragment, R.id.tenantListFragment))
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Tapping an item in the bottom bar. We navigate ourselves with two simple rules,
        // so the result is the same every time (no saved/restored screen state).
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.dashboardFragment -> {
                    // The Dashboard is always at the bottom of the back stack,
                    // so go back to it. If it somehow isn't there, open it.
                    if (!navController.popBackStack(R.id.dashboardFragment, false)) {
                        navController.navigate(R.id.dashboardFragment)
                    }
                    true
                }
                R.id.tenantListFragment -> {
                    // Open the tenant list once, on top of the Dashboard.
                    if (navController.currentDestination?.id != R.id.tenantListFragment) {
                        navController.navigate(
                            R.id.tenantListFragment,
                            null,
                            navOptions {
                                popUpTo(R.id.dashboardFragment)
                                launchSingleTop = true
                            }
                        )
                    }
                    true
                }
                else -> false
            }
        }
        // Tapping the item you're already on does nothing.
        bottomNav.setOnItemReselectedListener { }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Show the bar only on the top-level screens (hidden on the form and details).
            bottomNav.isVisible = destination.id in appBarConfiguration.topLevelDestinations
            // Keep the highlighted item in step with the screen, however we got there
            // (a bar tap, the Dashboard's buttons, or the phone's back button).
            bottomNav.menu.findItem(destination.id)?.isChecked = true
            applyBottomInset()
        }
        askForNotificationPermission()
        // Schedule the daily background reminder (does nothing if already scheduled).
        ReminderScheduler.schedule(this)
    }

    // Puts the phone's bottom bar space under whichever view is at the bottom.
    private fun applyBottomInset() {
        if (bottomNav.isVisible) {
            bottomNav.setPadding(0, 0, 0, bottomInset)
            navHost.setPadding(0, 0, 0, 0)
        } else {
            navHost.setPadding(0, 0, 0, bottomInset)
        }
    }

    // Called when the Up arrow is tapped: go back one screen.
    override fun onSupportNavigateUp(): Boolean =
        navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()

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