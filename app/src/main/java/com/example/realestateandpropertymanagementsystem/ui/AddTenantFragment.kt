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

// Collects tenant details, validates them, and sends a new Tenant to the ViewModel.
class AddTenantFragment : Fragment() {

    private var _binding: FragmentAddTenantBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TenantViewModel by viewModels()

    // Lease dates are stored as milliseconds (Long), matching the Tenant entity.
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
                binding.btnStartDate.text =
                    getString(R.string.start_date_format, dateFormat.format(Date(millis)))
            }
        }
        binding.btnEndDate.setOnClickListener {
            pickDate(getString(R.string.select_end_date)) { millis ->
                leaseEnd = millis
                binding.btnEndDate.text =
                    getString(R.string.end_date_format, dateFormat.format(Date(millis)))
            }
        }
        binding.btnSave.setOnClickListener { saveTenant() }
    }

    // Shows a calendar dialog and returns the chosen date through the callback.
    private fun pickDate(title: String, onPicked: (Long) -> Unit) {
        val picker = MaterialDatePicker.Builder.datePicker().setTitleText(title).build()
        picker.addOnPositiveButtonClickListener { onPicked(it) }
        picker.show(childFragmentManager, title)
    }

    // Validates every field. Only a fully valid form is sent to the database.
    private fun saveTenant() {
        val name = binding.etName.text.toString().trim()
        val unit = binding.etUnit.text.toString().trim()
        val rent = binding.etRent.text.toString().trim().toIntOrNull()
        val phone = binding.etPhone.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val dueDay = binding.etDueDay.text.toString().trim().toIntOrNull()
        val start = leaseStart
        val end = leaseEnd

        // Clear errors left over from a previous attempt.
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
        // Kenyan mobile numbers: 07xxxxxxxx, 01xxxxxxxx or +2547xxxxxxxx / +2541xxxxxxxx
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

        // The checks above guarantee these are not null.
        viewModel.insert(
            Tenant(
                name = name,
                unit = unit,
                rent = rent!!,
                phone = phone,
                email = email,
                rentDueDay = dueDay!!,
                leaseStartDate = start!!,
                leaseEndDate = end!!
            )
        )
        Toast.makeText(requireContext(), getString(R.string.tenant_saved, name), Toast.LENGTH_SHORT).show()
        findNavController().popBackStack() // back to the list, which refreshes by itself
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