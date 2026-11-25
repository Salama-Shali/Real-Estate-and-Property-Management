package com.example.realestateandpropertymanagementsystem.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.realestateandpropertymanagementsystem.R
import com.example.realestateandpropertymanagementsystem.data.Tenant
import com.example.realestateandpropertymanagementsystem.databinding.FragmentDashboardBinding
import com.example.realestateandpropertymanagementsystem.util.RentReminderLogic
import com.example.realestateandpropertymanagementsystem.viewmodel.TenantViewModel
import java.util.Calendar

/**
 * DashboardFragment = the home screen: a quick summary of the landlord's business.
 *
 * It reuses TenantViewModel (the same data as the tenant list) and only DISPLAYS
 * numbers worked out from that list. No database code lives here.
 */
class DashboardFragment : Fragment() {

    // View Binding, released in onDestroyView to avoid memory leaks.
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TenantViewModel by viewModels()
    private val paymentViewModel: com.example.realestateandpropertymanagementsystem.viewmodel.PaymentViewModel by viewModels()

    private var currentTenants: List<Tenant> = emptyList()
    private var currentPayments: List<com.example.realestateandpropertymanagementsystem.data.Payment> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showGreeting()

        // Navigation: each button moves to another screen defined in nav_graph.xml.
        binding.btnViewTenants.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_tenantListFragment)
        }
        binding.btnAddTenantDashboard.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_addTenantFragment)
        }

        binding.btnExportCsv.setOnClickListener {
            val tenantsMap = currentTenants.associateBy { it.id }
            com.example.realestateandpropertymanagementsystem.util.ReportExporter.shareCsvReport(
                requireContext(),
                tenantsMap,
                currentPayments
            )
        }

        // Runs now and again every time the tenants table changes,
        // so the numbers update by themselves.
        viewModel.allTenants.observe(viewLifecycleOwner) { tenants ->
            currentTenants = tenants
            showStats(tenants)
        }

        paymentViewModel.allPayments.observe(viewLifecycleOwner) { payments ->
            currentPayments = payments
        }
    }
    // Sets the greeting from the phone's clock
    private fun showGreeting() {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greetingRes = when (hour) {
            in 0..11 -> R.string.greeting_morning
            in 12..16 -> R.string.greeting_afternoon
            else -> R.string.greeting_evening
        }
        binding.tvGreeting.text = getString(greetingRes)
    }

    private fun showStats(tenants: List<Tenant>) {
        // Only tenants still living in the unit count towards income and reminders.
        val active = tenants.filter { it.isActive }

        // Total rent the landlord expects each month.
        val totalRent = active.sumOf { it.rent }

        // Same rule the background reminder uses (RentReminderLogic, unit tested).
        val today = Calendar.getInstance()
        val dayOfMonth = today.get(Calendar.DAY_OF_MONTH)
        val daysInMonth = today.getActualMaximum(Calendar.DAY_OF_MONTH)
        val dueSoon = active.count {
            RentReminderLogic.isDueSoon(it.rentDueDay, dayOfMonth, daysInMonth)
        }

        binding.tvActiveTenants.text = active.size.toString()
        binding.tvMonthlyRent.text = getString(R.string.dashboard_rent_value, totalRent)
        binding.tvDueSoon.text = dueSoon.toString()

        // Empty state: hint to add a first tenant.
        binding.tvDashboardEmpty.visibility = if (active.isEmpty()) View.VISIBLE else View.GONE

        // Loading state ends: hide the spinner, reveal the cards.
        binding.progressDashboard.visibility = View.GONE
        binding.statsContainer.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}