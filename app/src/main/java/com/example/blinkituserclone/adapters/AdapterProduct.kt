package com.example.blinkituserclone.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.models.SlideModel
import com.example.blinkituserclone.databinding.ItemViewBinding
import com.example.blinkituserclone.models.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AdapterProduct() : RecyclerView.Adapter<AdapterProduct.ViewHolder>() , Filterable {


    class ViewHolder(val binding: ItemViewBinding) : RecyclerView.ViewHolder(binding.root)

    // enhanced version of recyclerView - if item added or removed then recyclerView or list will not fully load again
    private val diffUtil = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.productRandomId == newItem.productRandomId
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    var originalList : List<Product> = emptyList()
    var filteredList : List<Product> = emptyList()

    // doing search feature with diffUtil list
    fun submitListOfFilter(list : List<Product>) {
        originalList = list
        filteredList = list

        differ.submitList(filteredList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = differ.currentList[position]

        holder.binding.apply {
            val imageList = ArrayList<SlideModel>()
            val productImages = product.productImageUris

            for (i in 0 until  productImages?.size!!){
                imageList.add(SlideModel(productImages[i]))
            }

            ivImageSlider.setImageList(imageList)
            tvProductTitle.text = product.productTitle
            tvProductQuantity.text = "${product.productQuantity}${product.productUnit}"
            tvProductPrice.text = "₹${product.productPrice}" // ₹ for symbol use control + alt + 4
        }

        // here we are giving this fun a product for each position
        // means on which position user has clicked, that product we are sending
//        holder.binding.tvEdit.setOnClickListener{
//            onEditButtonClickListener(product)
//        }

    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence : CharSequence?): FilterResults {
                val query = charSequence.toString().toLowerCase() ?: ""

                // this result variable hold filter list
                filteredList = if (query.isEmpty()){
                    originalList
                } else {
                    originalList.filter { product ->
                        product.productTitle?.lowercase()?.contains(query) == true ||
                        product.productCategory?.lowercase()?.contains(query) == true ||
                        product.productPrice?.toString()?.lowercase()?.contains(query) == true
                    }
                }

                // see all this 3 line code is in one line below
//                val filterResult = FilterResults()
//                filterResult.values = filteredList
//                return filterResult
                return FilterResults().apply { values = filteredList }
            }

            override fun publishResults(charSequence: CharSequence?, result: FilterResults?) {
                differ.submitList(filteredList)
            }

        }
    }


}