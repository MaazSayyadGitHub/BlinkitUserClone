package com.example.blinkituserclone.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blinkituserclone.databinding.ItemViewCartProductBinding
import com.example.blinkituserclone.models.Product
import com.example.blinkituserclone.roomdb.CartProducts

class AdapterCartProducts() : RecyclerView.Adapter<AdapterCartProducts.CartViewHolder>() {


    // DiffUtil
    private val diffUtil = object : DiffUtil.ItemCallback<CartProducts>(){
        override fun areItemsTheSame(oldItem: CartProducts, newItem: CartProducts): Boolean {
            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(oldItem: CartProducts, newItem: CartProducts): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        return CartViewHolder(ItemViewCartProductBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {

        val product = differ.currentList[position]

        holder.binding.apply {
            // set image
            Glide.with(holder.itemView)
                .load(product.productImage)
                .into(ivCartProductImage)

            tvCartProductTitle.text = product.productTitle // set title
            tvCartProductQuantity.text = product.productQuantity // set quantity
            tvCartProductPrice.text = product.productPrice // set price

            tvCartProductCount.text = product.productCount.toString()

        }
    }

    class CartViewHolder(val binding : ItemViewCartProductBinding) : RecyclerView.ViewHolder(binding.root)

}