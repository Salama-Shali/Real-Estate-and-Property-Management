package com.example.realestateandpropertymanagementsystem.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.realestateandpropertymanagementsystem.R
import com.example.realestateandpropertymanagementsystem.data.Payment
import com.example.realestateandpropertymanagementsystem.data.Tenant
import com.example.realestateandpropertymanagementsystem.databinding.DialogAddPaymentBinding
import com.example.realestateandpropertymanagementsystem.databinding.FragmentTenantDetailsBinding
import com.example.realestateandpropertymanagementsystem.viewmodel.PaymentViewModel
import com.example.realestateandpropertymanagementsystem.viewmodel.TenantViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Shows one tenant's details and payment history.
class TenantDetailsFragment : Fragment() {

    private var _binding: FragmentTenantDetailsBinding? = null
    private val binding get() = _binding!!

    private val tenantViewModel: TenantViewModel by viewModels()
    private val paymentViewModel: PaymentViewModel by viewModels()

    private var tenantId = -1
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTenantDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // The list screen sends the tenant's id through navigation.
        tenantId = arguments?.getInt("tenantId", -1) ?: -1

        val adapter = PaymentAdapter()
        binding.rvPayments.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPayments.adapter = adapter

        // Tenant info: updates by itself if the tenant is edited.
        tenantViewModel.getTenantById(tenantId).observe(viewLifecycleOwner) { tenant ->
            if (tenant != null) showTenant(tenant)
        }

        // Payment history for this tenant only.
        paymentViewModel.getPayments(tenantId).observe(viewLifecycleOwner) { payments ->
            adapter.submitList(payments)
            binding.tvNoPayments.visibility = if (payments.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.btnEdit.setOnClickListener {
            findNavController().navigate(
                R.id.action_tenantDetailsFragment_to_addTenantFragment,
                bundleOf("tenantId" to tenantId)
            )
        }
        binding.btnRecordPayment.setOnClickListener { showPaymentDialog() }
    }

    private fun showTenant(tenant: Tenant) {
        binding.tvName.text = tenant.name
        binding.tvUnit.text = getString(R.string.tenant_unit_format, tenant.unit)
        binding.tvRent.text = getString(R.string.tenant_rent_format, tenant.rent)
        binding.tvDueDay.text = getString(R.string.rent_due_format, tenant.rentDueDay)
        binding.tvPhone.text = getString(R.string.tenant_phone_format, tenant.phone)
        binding.tvEmail.text = getString(R.string.tenant_email_format, tenant.email)
        binding.tvLease.text = getString(
            R.string.lease_format,
            dateFormat.format(Date(tenant.leaseStartDate)),
            dateFormat.format(Date(tenant.leaseEndDate))
        )
    }

    // Pop-up to record a payment. Stays open if the amount is invalid.
    private fun showPaymentDialog() {
        val dialogBinding = DialogAddPaymentBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.record_payment)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.save, null)   // set below so it doesn't auto-close
            .setNegativeButton(R.string.cancel, null)
            .create()
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val amount = dialogBinding.etAmount.text.toString().trim().toIntOrNull()
            if (amount == null || amount <= 0) {
                dialogBinding.tilAmount.error = getString(R.string.error_amount)
                return@setOnClickListener
            }
            paymentViewModel.insert(
                Payment(
                    tenantId = tenantId,
                    amount = amount,
                    paymentDate = System.currentTimeMillis(),   // today
                    note = dialogBinding.etNote.text.toString().trim()
                )
            )
            Toast.makeText(requireContext(), R.string.payment_saved, Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}