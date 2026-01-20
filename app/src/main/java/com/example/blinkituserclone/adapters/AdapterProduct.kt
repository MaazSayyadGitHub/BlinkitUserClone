package com.example.blinkituserclone.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.models.SlideModel
import com.example.blinkituserclone.databinding.ItemViewBinding
import com.example.blinkituserclone.models.Product

class AdapterProduct(
    val onAddButtonClicked: (Product, ItemViewBinding) -> Unit,
    val onIncrementButtonClicked: (Product, ItemViewBinding) -> Unit,
    val onDecrementButtonClicked: (Product, ItemViewBinding) -> Unit
) :
    RecyclerView.Adapter<AdapterProduct.ViewHolder>() , Filterable {


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

            productImages?.let { list ->
                for (url in list) {
                    imageList.add(SlideModel(url))
                }
            }

//            for (i in 0 until  productImages?.size){
//                imageList.add(SlideModel(productImages?.get(i)))
//            }

            ivImageSlider.setImageList(imageList)
            tvProductTitle.text = product.productTitle
            tvProductQuantity.text = "${product.productQuantity}${product.productUnit}"
            tvProductPrice.text = "₹${product.productPrice}" // ₹ for symbol use control + alt + 4

            if (product.itemCount!! > 0){
                tvProductCount.text = product.itemCount.toString()
                tvAdd.visibility = View.GONE
                llProductCount.visibility = View.VISIBLE
            }

            // by this click listener we will know that which items button is clicked
            // and we will send that items (product, and all binding views)
            tvAdd.setOnClickListener {
                onAddButtonClicked(product, this) // passing current product & whole itemView
            }

            tvIncrementCount.setOnClickListener{
                onIncrementButtonClicked(product, this)
            }

            tvDecrementCount.setOnClickListener {
                onDecrementButtonClicked(product, this)
            }
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
                val query = charSequence.toString().lowercase() ?: ""

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