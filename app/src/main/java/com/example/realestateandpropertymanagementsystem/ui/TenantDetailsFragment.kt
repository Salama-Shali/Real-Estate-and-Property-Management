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
import com.example.realestateandpropertymanagementsystem.util.LeaseStatus
// Shows one tenant's details and payment history.
class TenantDetailsFragment : Fragment() {

    private var _binding: FragmentTenantDetailsBinding? = null
    private val binding get() = _binding!!

    private val tenantViewModel: TenantViewModel by viewModels()
    private val paymentViewModel: PaymentViewModel by viewModels()
    private val maintenanceViewModel: com.example.realestateandpropertymanagementsystem.viewmodel.MaintenanceViewModel by viewModels()

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
        binding.btnReportIssue.setOnClickListener { showReportIssueDialog() }
    }

    private fun showTenant(tenant: Tenant) {
        binding.tvName.text = tenant.name
        binding.tvUnit.text = getString(R.string.tenant_unit_format, tenant.unit)
        binding.tvRent.text = getString(R.string.tenant_rent_format, tenant.rent)
        binding.tvDueDay.text = getString(R.string.rent_due_format, tenant.rentDueDay)
        binding.tvPhone.text = getString(R.string.tenant_phone_format, tenant.phone)
        binding.tvEmail.text = getString(R.string.tenant_email_format, tenant.email)
        // End date is null for month-to-month tenancies.
        val end = tenant.leaseEndDate
        binding.tvLease.text = if (end != null) {
            getString(
                R.string.lease_format,
                dateFormat.format(Date(tenant.leaseStartDate)),
                dateFormat.format(Date(end))
            )
        } else {
            getString(R.string.lease_format_open, dateFormat.format(Date(tenant.leaseStartDate)))
        }
        binding.tvLeaseStatus.text = LeaseStatus.label(requireContext(), tenant.leaseEndDate)
        binding.tvLeaseStatus.setTextColor(LeaseStatus.color(requireContext(), tenant.leaseEndDate))

        setupCommunicationActions(tenant)
    }

    private fun setupCommunicationActions(tenant: Tenant) {
        val phoneNumber = tenant.phone.trim()
        val reminderMessage = getString(
            R.string.sms_reminder_template,
            tenant.name,
            tenant.rent,
            tenant.unit,
            tenant.rentDueDay
        )

        binding.btnCall.setOnClickListener {
            val intent = android.content.Intent(android.content.Intent.ACTION_DIAL, android.net.Uri.parse("tel:$phoneNumber"))
            startActivity(intent)
        }

        binding.btnSms.setOnClickListener {
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("sms:$phoneNumber")).apply {
                putExtra("sms_body", reminderMessage)
            }
            startActivity(intent)
        }

        binding.btnWhatsApp.setOnClickListener {
            try {
                val cleanPhone = phoneNumber.replace("+", "").replace(" ", "").replace("-", "")
                val formattedPhone = if (cleanPhone.startsWith("0")) "254" + cleanPhone.substring(1) else cleanPhone
                val url = "https://api.whatsapp.com/send?phone=$formattedPhone&text=${android.net.Uri.encode(reminderMessage)}"
                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "WhatsApp is not installed", Toast.LENGTH_SHORT).show()
            }
        }
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

        dialogBinding.btnParseMPesa.setOnClickListener {
            val text = dialogBinding.etMPesaSms.text.toString()
            val parsed = com.example.realestateandpropertymanagementsystem.util.MPesaParser.parse(text)
            if (parsed != null) {
                if (parsed.amount != null) dialogBinding.etAmount.setText(parsed.amount.toString())
                if (parsed.formattedNote.isNotEmpty()) dialogBinding.etNote.setText(parsed.formattedNote)
                Toast.makeText(requireContext(), R.string.mpesa_parsed_success, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), R.string.mpesa_parse_error, Toast.LENGTH_SHORT).show()
            }
        }

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

    private fun showReportIssueDialog() {
        val input = android.widget.EditText(requireContext()).apply {
            hint = getString(R.string.hint_maintenance_desc)
            setPadding(32, 16, 32, 16)
        }
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.add_maintenance)
            .setView(input)
            .setPositiveButton(R.string.save) { _, _ ->
                val desc = input.text.toString().trim()
                if (desc.isNotEmpty()) {
                    maintenanceViewModel.insert(
                        com.example.realestateandpropertymanagementsystem.data.MaintenanceRequest(
                            tenantId = tenantId,
                            description = desc
                        )
                    )
                    Toast.makeText(requireContext(), "Maintenance issue logged", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}