package com.example.blinkituserclone.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.blinkituserclone.R
import com.example.blinkituserclone.databinding.ItemViewOrdersBinding
import com.example.blinkituserclone.models.OrderedItems

class AdapterOrders(val onOrderedItems: (OrderedItems) -> Unit) : RecyclerView.Adapter<AdapterOrders.ViewHolder>() {

    val diffUtil = object : DiffUtil.ItemCallback<OrderedItems>() {
        override fun areItemsTheSame(
            oldItem: OrderedItems,
            newItem: OrderedItems
        ): Boolean {
            return oldItem.orderId == newItem.orderId
        }

        override fun areContentsTheSame(
            oldItem: OrderedItems,
            newItem: OrderedItems
        ): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(  ItemViewOrdersBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val order = differ.currentList[position]
        holder.onBind(order)

        holder.itemView.setOnClickListener {
            onOrderedItems(order)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    class ViewHolder(val binding : ItemViewOrdersBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(order : OrderedItems) {
            binding.apply {
                tvOrderDate.text = order.itemDate
                tvOrderDetails.text = order.itemDetails
                tvOrderPrice.text = "₹"+order.itemPrice.toString()

                when(order.itemStatus) {
                    0 -> {
                        tvOrderStatus.text = "Ordered"
                        tvOrderStatus.backgroundTintList = ContextCompat.getColorStateList(itemView.context, R.color.yellow)
                    }
                    1 -> {
                        tvOrderStatus.text = "Received"
                        tvOrderStatus.backgroundTintList = ContextCompat.getColorStateList(itemView.context, R.color.blue)
                    }
                    2 -> {
                        tvOrderStatus.text = "Dispatched"
                        tvOrderStatus.backgroundTintList = ContextCompat.getColorStateList(itemView.context, R.color.green)
                    }
                    3 -> {
                        tvOrderStatus.text = "Delivered"
                        tvOrderStatus.backgroundTintList = ContextCompat.getColorStateList(itemView.context, R.color.orange)
                    }
                }
            }
        }
    }
}