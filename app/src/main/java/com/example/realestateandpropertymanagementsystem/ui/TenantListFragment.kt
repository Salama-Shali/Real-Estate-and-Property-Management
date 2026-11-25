package com.example.realestateandpropertymanagementsystem.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.realestateandpropertymanagementsystem.R
import com.example.realestateandpropertymanagementsystem.data.Tenant
import com.example.realestateandpropertymanagementsystem.databinding.FragmentTenantListBinding
import com.example.realestateandpropertymanagementsystem.viewmodel.TenantViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import androidx.navigation.fragment.findNavController
import androidx.core.os.bundleOf
import com.example.realestateandpropertymanagementsystem.worker.ReminderScheduler
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

// It only displays data and reacts to the users taps
class TenantListFragment : Fragment() {
    // View Binding for fragment_tenant_list.xml
    private var _binding: FragmentTenantListBinding? = null
    private val binding get() = _binding!!

    // "by viewModels()" gets (or creates) this screen's TenantViewModel.
    // It survives screen rotation, so the list isn't reloaded each time.
    private val viewModel: TenantViewModel by viewModels()
    private lateinit var adapter: TenantAdapter

    //  build the screen's layout
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTenantListBinding.inflate(inflater, container, false)
        return binding.root
    }

    private var fullTenantList: List<Tenant> = emptyList()

    //connect the list and start observing the data
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Create the adapter and say what happens when a row or delete icon is tapped
        adapter = TenantAdapter(
            onTenantClick = { tenant ->
                findNavController().navigate(
                    R.id.action_tenantListFragment_to_tenantDetailsFragment,
                    bundleOf("tenantId" to tenant.id)
                )
            },
            onDeleteClick = { tenant -> confirmDelete(tenant) }
        )

        // A LayoutManager decides how rows are arranged. LinearLayoutManager = vertical list.
        binding.rvTenants.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTenants.adapter = adapter

        // Observe the LiveData
        viewModel.allTenants.observe(viewLifecycleOwner) { tenants ->
            fullTenantList = tenants
            applyFilterAndSearch()
        }

        binding.etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                applyFilterAndSearch()
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        binding.chipGroupFilter.setOnCheckedStateChangeListener { _, _ ->
            applyFilterAndSearch()
        }

        // Open the Add Tenant form when the + button is tapped
        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_tenantListFragment_to_addTenantFragment)
        }
        // Run the reminder check right now, so the landlord doesn't wait a day.
        binding.btnCheckReminders.setOnClickListener {
            ReminderScheduler.checkNow(requireContext())
            Snackbar.make(binding.root, R.string.reminder_check_started, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun applyFilterAndSearch() {
        val query = binding.etSearch.text.toString().trim().lowercase()
        val checkedChipId = binding.chipGroupFilter.checkedChipId

        val today = java.util.Calendar.getInstance()
        val dayOfMonth = today.get(java.util.Calendar.DAY_OF_MONTH)
        val daysInMonth = today.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)

        val filtered = fullTenantList.filter { tenant ->
            val matchesSearch = query.isEmpty() ||
                    tenant.name.lowercase().contains(query) ||
                    tenant.unit.lowercase().contains(query)

            val matchesFilter = when (checkedChipId) {
                R.id.chipFilterDueSoon -> com.example.realestateandpropertymanagementsystem.util.RentReminderLogic.isDueSoon(
                    tenant.rentDueDay, dayOfMonth, daysInMonth
                )
                R.id.chipFilterMonthToMonth -> tenant.leaseEndDate == null
                else -> true
            }

            matchesSearch && matchesFilter
        }

        adapter.submitList(filtered)
        binding.tvEmpty.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }


    // Ask before deleting: a delete cannot be undone, so we confirm first.
    private fun confirmDelete(tenant: Tenant) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_tenant_title)
            .setMessage(getString(R.string.delete_tenant_message, tenant.name))
            .setPositiveButton(R.string.delete) { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    val deleted = viewModel.deleteSafely(tenant)
                    // The user may have left the screen while we waited.
                    if (_binding == null) return@launch
                    // Feedback: success message, or an error message if it failed.
                    val message = if (deleted) {
                        getString(R.string.tenant_deleted, tenant.name)
                    } else {
                        getString(R.string.error_delete_failed)
                    }
                    Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.cancel, null) // just close the dialog
            .show()
    }

    // Release the binding when the screen's views are destroyed.
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
