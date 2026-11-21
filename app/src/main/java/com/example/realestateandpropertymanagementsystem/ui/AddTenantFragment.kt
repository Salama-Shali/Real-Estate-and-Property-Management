package com.example.realestateandpropertymanagementsystem.ui

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.lifecycle.lifecycleScope
import com.example.realestateandpropertymanagementsystem.util.TenantValidator
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
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
        // Month-to-month: ticking the box clears the end date and disables its button.
        binding.cbNoEndDate.setOnCheckedChangeListener { _, isChecked ->
            binding.btnEndDate.isEnabled = !isChecked
            if (isChecked) {
                leaseEnd = null
                binding.btnEndDate.text = getString(R.string.select_end_date)
                binding.tvDateError.visibility = View.GONE
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
        val savedEnd = tenant.leaseEndDate
        if (savedEnd != null) {
            showEndDate(savedEnd)
        } else {
            // No end date saved = month-to-month. Ticking the box also disables the end-date button.
            binding.cbNoEndDate.isChecked = true
        }
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

    // Called when "Save" is tapped: validate -> check duplicate unit -> save.
    private fun saveTenant() {
        // Read what the user typed.
        val name = binding.etName.text.toString().trim()
        val unit = binding.etUnit.text.toString().trim()
        val rentText = binding.etRent.text.toString()
        val phone = binding.etPhone.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val dueDayText = binding.etDueDay.text.toString()
        val start = leaseStart
        val end = leaseEnd

        // Run EVERY rule. Each returns null (valid) or the id of an error message.
        // All rules in one place: util/TenantValidator.kt
        val nameError = TenantValidator.validateName(name)
        val unitError = TenantValidator.validateUnit(unit)
        val rentError = TenantValidator.validateRent(rentText)
        val phoneError = TenantValidator.validatePhone(phone)
        val emailError = TenantValidator.validateEmail(email)
        val dueDayError = TenantValidator.validateDueDay(dueDayText)
        // Dates: report a missing start first, otherwise check the end.
        val hasFixedEnd = !binding.cbNoEndDate.isChecked
        val dateError = TenantValidator.validateLeaseStart(start)
            ?: TenantValidator.validateLeaseEnd(start, end, hasFixedEnd)

        // Show each message under its field (null clears any old message).
        setError(binding.tilName, nameError)
        setError(binding.tilUnit, unitError)
        setError(binding.tilRent, rentError)
        setError(binding.tilPhone, phoneError)
        setError(binding.tilEmail, emailError)
        setError(binding.tilDueDay, dueDayError)
        if (dateError != null) {
            showDateError(getString(dateError))
        } else {
            binding.tvDateError.visibility = View.GONE
        }

        // Stop here if anything is invalid.
        val hasErrors = listOf(
            nameError, unitError, rentError, phoneError, emailError, dueDayError, dateError
        ).any { it != null }
        if (hasErrors) return

        // Everything is valid, so the conversions below are safe.
        val rent = rentText.trim().toInt()
        val dueDay = dueDayText.trim().toInt()
        val startMillis = start ?: return


        // Duplicate check needs the database, which takes time, so it runs in a coroutine.
        // viewLifecycleOwner.lifecycleScope is cancelled automatically if the screen closes.
        viewLifecycleOwner.lifecycleScope.launch {
            // When editing, exclude this tenant so they don't clash with themselves.
            val excludeId = if (tenantId != -1) tenantId else 0
            if (viewModel.isUnitOccupied(unit, excludeId)) {
                setError(binding.tilUnit, R.string.error_unit_taken)
                return@launch
            }
            persistTenant(name, unit, rent, phone, email, dueDay, startMillis,if (hasFixedEnd) end else null)
        }
    }

    // Writes the tenant to the database (insert or update) and returns to the list.
    private fun persistTenant(
        name: String, unit: String, rent: Int, phone: String, email: String,
        dueDay: Int, start: Long, end: Long?
    ) {
        if (tenantId != -1) {
            // Edit mode: wait until the saved tenant has loaded.
            val current = existingTenant ?: return
            // copy() keeps the id and isActive, and changes only the fields we pass.
            viewModel.update(
                current.copy(
                    name = name, unit = unit, rent = rent, phone = phone, email = email,
                    rentDueDay = dueDay, leaseStartDate = start, leaseEndDate = end
                )
            )
            Toast.makeText(requireContext(), getString(R.string.tenant_updated, name), Toast.LENGTH_SHORT).show()
        } else {
            // Add mode: insert a brand new tenant.
            viewModel.insert(
                Tenant(
                    name = name, unit = unit, rent = rent, phone = phone, email = email,
                    rentDueDay = dueDay, leaseStartDate = start, leaseEndDate = end
                )
            )
            Toast.makeText(requireContext(), getString(R.string.tenant_saved, name), Toast.LENGTH_SHORT).show()
        }
        findNavController().popBackStack()
    }

    // Shows (or clears, when messageId is null) the error under one form field.
    private fun setError(layout: TextInputLayout, @StringRes messageId: Int?) {
        layout.error = messageId?.let { getString(it) }
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