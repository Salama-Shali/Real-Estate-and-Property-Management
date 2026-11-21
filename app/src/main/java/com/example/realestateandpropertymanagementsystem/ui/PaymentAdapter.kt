package com.example.realestateandpropertymanagementsystem.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.realestateandpropertymanagementsystem.R
import com.example.realestateandpropertymanagementsystem.data.Payment
import com.example.realestateandpropertymanagementsystem.databinding.ItemPaymentBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Shows one tenant's payments as a list of cards.
class PaymentAdapter : ListAdapter<Payment, PaymentAdapter.PaymentViewHolder>(DIFF) {

    class PaymentViewHolder(val binding: ItemPaymentBinding) : RecyclerView.ViewHolder(binding.root)

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val binding = ItemPaymentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PaymentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        val payment = getItem(position)
        val context = holder.itemView.context
        holder.binding.tvAmount.text = context.getString(R.string.payment_amount_format, payment.amount)
        holder.binding.tvDate.text = dateFormat.format(Date(payment.paymentDate))
        holder.binding.tvNote.text = payment.note
        // Hide the note line when there is no note.
        holder.binding.tvNote.visibility = if (payment.note.isBlank()) View.GONE else View.VISIBLE
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Payment>() {
            override fun areItemsTheSame(old: Payment, new: Payment) = old.id == new.id
            override fun areContentsTheSame(old: Payment, new: Payment) = old == new
        }
    }
}