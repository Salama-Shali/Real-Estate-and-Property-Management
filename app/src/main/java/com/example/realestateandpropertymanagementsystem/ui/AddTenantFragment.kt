package com.example.realestateandpropertymanagementsystem.ui

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.realestateandpropertymanagementsystem.R
import com.example.realestateandpropertymanagementsystem.data.Tenant
import com.example.realestateandpropertymanagementsystem.databinding.FragmentAddTenantBinding
import com.example.realestateandpropertymanagementsystem.viewmodel.TenantViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// One form for two jobs: add a new tenant, or edit an existing one.
class AddTenantFragment : Fragment() {

    private var _binding: FragmentAddTenantBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TenantViewModel by viewModels()

    // -1 = Add mode. Any other value = the id of the tenant being edited.
    private var tenantId = -1
    private var existingTenant: Tenant? = null

    private var leaseStart: Long? = null
    private var leaseEnd: Long? = null
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTenantBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnStartDate.setOnClickListener {
            pickDate(getString(R.string.select_start_date)) { millis ->
                leaseStart = millis
                showStartDate(millis)
            }
        }
        binding.btnEndDate.setOnClickListener {
            pickDate(getString(R.string.select_end_date)) { millis ->
                leaseEnd = millis
                showEndDate(millis)
            }
        }
        binding.btnSave.setOnClickListener { saveTenant() }

        // Edit mode: load the tenant and prefill the form (only once).
        tenantId = arguments?.getInt("tenantId", -1) ?: -1
        if (tenantId != -1) {
            binding.tvFormTitle.text = getString(R.string.edit_tenant)
            binding.btnSave.text = getString(R.string.save_changes)
            viewModel.getTenantById(tenantId).observe(viewLifecycleOwner) { tenant ->
                if (tenant != null && existingTenant == null) {
                    existingTenant = tenant
                    fillForm(tenant)
                }
            }
        }
    }

    // Puts the saved tenant's values into the form fields.
    private fun fillForm(tenant: Tenant) {
        binding.etName.setText(tenant.name)
        binding.etUnit.setText(tenant.unit)
        binding.etRent.setText(tenant.rent.toString())
        binding.etPhone.setText(tenant.phone)
        binding.etEmail.setText(tenant.email)
        binding.etDueDay.setText(tenant.rentDueDay.toString())
        leaseStart = tenant.leaseStartDate
        leaseEnd = tenant.leaseEndDate
        showStartDate(tenant.leaseStartDate)
        showEndDate(tenant.leaseEndDate)
    }

    private fun showStartDate(millis: Long) {
        binding.btnStartDate.text =
            getString(R.string.start_date_format, dateFormat.format(Date(millis)))
    }

    private fun showEndDate(millis: Long) {
        binding.btnEndDate.text =
            getString(R.string.end_date_format, dateFormat.format(Date(millis)))
    }

    private fun pickDate(title: String, onPicked: (Long) -> Unit) {
        val picker = MaterialDatePicker.Builder.datePicker().setTitleText(title).build()
        picker.addOnPositiveButtonClickListener { onPicked(it) }
        picker.show(childFragmentManager, title)
    }

    private fun saveTenant() {
        val name = binding.etName.text.toString().trim()
        val unit = binding.etUnit.text.toString().trim()
        val rent = binding.etRent.text.toString().trim().toIntOrNull()
        val phone = binding.etPhone.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val dueDay = binding.etDueDay.text.toString().trim().toIntOrNull()
        val start = leaseStart
        val end = leaseEnd

        binding.tilName.error = null
        binding.tilUnit.error = null
        binding.tilRent.error = null
        binding.tilPhone.error = null
        binding.tilEmail.error = null
        binding.tilDueDay.error = null
        binding.tvDateError.visibility = View.GONE

        var valid = true

        if (name.isEmpty()) {
            binding.tilName.error = getString(R.string.error_name); valid = false
        }
        if (unit.isEmpty()) {
            binding.tilUnit.error = getString(R.string.error_unit); valid = false
        }
        if (rent == null || rent <= 0) {
            binding.tilRent.error = getString(R.string.error_rent); valid = false
        }
        if (!phone.matches(Regex("^(\\+254|0)[17]\\d{8}$"))) {
            binding.tilPhone.error = getString(R.string.error_phone); valid = false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = getString(R.string.error_email); valid = false
        }
        if (dueDay == null || dueDay !in 1..31) {
            binding.tilDueDay.error = getString(R.string.error_due_day); valid = false
        }
        if (start == null || end == null) {
            showDateError(getString(R.string.error_dates_missing)); valid = false
        } else if (end <= start) {
            showDateError(getString(R.string.error_dates_order)); valid = false
        }

        if (!valid) return

        val current = existingTenant
        if (tenantId != -1) {
            // Edit mode: wait until the saved tenant has loaded.
            if (current == null) return
            // copy() keeps the id and isActive, and changes only the fields we pass.
            viewModel.update(
                current.copy(
                    name = name, unit = unit, rent = rent!!, phone = phone, email = email,
                    rentDueDay = dueDay!!, leaseStartDate = start!!, leaseEndDate = end!!
                )
            )
            Toast.makeText(requireContext(), getString(R.string.tenant_updated, name), Toast.LENGTH_SHORT).show()
        } else {
            // Add mode: insert a brand new tenant.
            viewModel.insert(
                Tenant(
                    name = name, unit = unit, rent = rent!!, phone = phone, email = email,
                    rentDueDay = dueDay!!, leaseStartDate = start!!, leaseEndDate = end!!
                )
            )
            Toast.makeText(requireContext(), getString(R.string.tenant_saved, name), Toast.LENGTH_SHORT).show()
        }
        findNavController().popBackStack()
    }

    private fun showDateError(message: String) {
        binding.tvDateError.text = message
        binding.tvDateError.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}