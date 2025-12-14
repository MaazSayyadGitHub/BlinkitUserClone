package com.example.blinkituserclone.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.blinkituserclone.R
import com.example.blinkituserclone.databinding.ItemViewProductCategoryBinding
import com.example.blinkituserclone.models.Category

class AdapterCategory(val listOfCategory: ArrayList<Category>) :
    RecyclerView.Adapter<AdapterCategory.CategoryViewModel>() {


    class CategoryViewModel(val binding: ItemViewProductCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        val titleCategory = binding.titleCategory
        val imageViewCategory = binding.titleCategory
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewModel {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view_product_category, parent)
        val binding = ItemViewProductCategoryBinding.inflate(LayoutInflater.from(parent.context))
        return CategoryViewModel(binding)

    }

    override fun getItemCount(): Int {
        return listOfCategory.size
    }

    override fun onBindViewHolder(holder: CategoryViewModel, position: Int) {
        val list = listOfCategory[position]
//        holder.imageViewCategory.setBackgroundResource(list.image)
//        holder.titleCategory.text = list.title

        holder.binding.ivCategory.setImageResource(list.image)
        holder.binding.titleCategory.text = list.title
    }
}