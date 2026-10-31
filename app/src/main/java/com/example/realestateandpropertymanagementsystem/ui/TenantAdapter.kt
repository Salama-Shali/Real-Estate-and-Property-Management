package com.example.realestateandpropertymanagementsystem.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.realestateandpropertymanagementsystem.R
import com.example.realestateandpropertymanagementsystem.data.Tenant
import com.example.realestateandpropertymanagementsystem.databinding.ItemTenantBinding

class TenantAdapter(
    private val onTenantClick: (Tenant) -> Unit,
    private val onDeleteClick: (Tenant) -> Unit
) : ListAdapter<Tenant, TenantAdapter.TenantViewHolder>(TenantDiffCallback()) {

    /**
     * ViewHolder = holds the views of ONE row so they aren't looked up again and again.
     * ItemTenantBinding is generated automatically from item_tenant.xml (View Binding),
     * so binding.tvTenantName is the TextView with id tvTenantName.
     */
    inner class TenantViewHolder(private val binding: ItemTenantBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Fills this row with the data of one tenant.
        fun bind(tenant: Tenant) {
            val context = binding.root.context

            binding.tvTenantName.text = tenant.name
            // getString(id, value) fills the %1$s / %1$d placeholder in strings.xml
            binding.tvUnit.text = context.getString(R.string.tenant_unit_format, tenant.unit)
            binding.tvRent.text = context.getString(R.string.tenant_rent_format, tenant.rent)
            binding.tvPhone.text = context.getString(R.string.tenant_phone_format, tenant.phone)
            binding.tvStatus.text = context.getString(
                if (tenant.isActive) R.string.status_active else R.string.status_moved_out
            )

            // Tell the Fragment which tenant was tapped.
            binding.root.setOnClickListener { onTenantClick(tenant) }
            binding.btnDelete.setOnClickListener { onDeleteClick(tenant) }
        }
    }

    // Called when RecyclerView needs a NEW row: inflate item_tenant.xml into a view.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TenantViewHolder {
        val binding = ItemTenantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TenantViewHolder(binding)
    }

    // Called when a row (new or recycled) must show the tenant at this position.
    override fun onBindViewHolder(holder: TenantViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}



/**
 * Tells ListAdapter how to compare the old and new lists so it updates only what changed.
 */
class TenantDiffCallback : DiffUtil.ItemCallback<Tenant>() {
    // Same tenant? Compare the unique id.
    override fun areItemsTheSame(oldItem: Tenant, newItem: Tenant) = oldItem.id == newItem.id

    // Same content? A data class compares all its fields with ==.
    override fun areContentsTheSame(oldItem: Tenant, newItem: Tenant) = oldItem == newItem
}





/**
 * TenantAdapter = the bridge between a LIST OF Tenant objects and the ROWS on screen.
 *
 * RecyclerView only creates enough rows to fill the screen and reuses them as you
 * scroll ("recycling"), so a list of 1000 tenants stays fast.
 *
 * ListAdapter is a ready-made adapter that, whenever it receives a new list,
 * works out what changed (using DiffUtil below) and animates only those rows.
 *
 * The two lambdas are "what to do when a row/delete icon is tapped". The adapter
 * doesn't decide that itself; the Fragment passes the behaviour in. This keeps
 * the adapter reusable and free of business logic.
 */