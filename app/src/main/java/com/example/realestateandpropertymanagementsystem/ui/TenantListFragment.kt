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

    //connect the list and start observing the data
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Create the adapter and say what happens when a row or delete icon is tapped
        adapter = TenantAdapter(
            onTenantClick = { },
            onDeleteClick = { tenant -> confirmDelete(tenant) }
        )

        // A LayoutManager decides how rows are arranged. LinearLayoutManager = vertical list.
        binding.rvTenants.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTenants.adapter = adapter

        // Observe the LiveData
        viewModel.allTenants.observe(viewLifecycleOwner) { tenants ->
            adapter.submitList(tenants)
            //show the message only when there is nothing to list
            binding.tvEmpty.visibility = if (tenants.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    // Ask before deleting a tenant since deletion cant be undone
    private fun confirmDelete(tenant: Tenant) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_tenant_title)
            .setMessage(getString(R.string.delete_tenant_message, tenant.name))
            .setPositiveButton(R.string.delete) { _, _ ->
                viewModel.delete(tenant)
                // Feedback message so the user knows it worked.
                Snackbar.make(
                    binding.root,
                    getString(R.string.tenant_deleted, tenant.name),
                    Snackbar.LENGTH_SHORT
                ).show()
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
