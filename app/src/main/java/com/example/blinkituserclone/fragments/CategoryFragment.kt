package com.example.blinkituserclone.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.blinkituserclone.R
import com.example.blinkituserclone.adapters.AdapterCategory
import com.example.blinkituserclone.adapters.AdapterProduct
import com.example.blinkituserclone.databinding.FragmentCategoryBinding
import com.example.blinkituserclone.models.Product
import com.example.blinkituserclone.viewmodels.UserViewModel
import kotlinx.coroutines.launch


class CategoryFragment : Fragment() {

    private lateinit var binding: FragmentCategoryBinding
    private var categoryTitle : String? = null
    private val viewModel : UserViewModel by viewModels()
    private lateinit var adapterProduct: AdapterProduct

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCategoryBinding.inflate(inflater, container, false)

        setStatusBarColor()

        getProductCategory()
        setToolbarTitle()
        fetchCategoryProducts()
        onSearchMenuItemClick()

        backToHome() // navigationIconClicked

        return binding.root
    }

    private fun backToHome() {
        binding.tbCategoryFragment.setNavigationOnClickListener {
            // we are using navigation component that's why findNavController else BackPressed
            findNavController().popBackStack() // go back
//            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun onSearchMenuItemClick() {
        binding.tbCategoryFragment.setOnMenuItemClickListener{ menuItem ->
            when(menuItem.itemId) {
                R.id.searchMenu -> {
                    findNavController().navigate(R.id.action_categoryFragment_to_searchFragment)
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    private fun fetchCategoryProducts() {

        binding.shimmerLayout.visibility = View.VISIBLE

        lifecycleScope.launch {
            viewModel.getCategoryProduct(categoryTitle!!).collect{
                val products : ArrayList<List<Product>>
                if (it.isEmpty()){
                    binding.tvText.visibility = View.VISIBLE
                    binding.rvProducts.visibility = View.GONE
                } else {
                    binding.rvProducts.visibility = View.VISIBLE
                    binding.tvText.visibility = View.GONE
                }

                adapterProduct = AdapterProduct() // we are setting adapter of Products
                binding.rvProducts.adapter = adapterProduct
                adapterProduct.differ.submitList(it)
                binding.shimmerLayout.visibility = View.GONE
            }
        }

    }

    private fun setToolbarTitle() {
        binding.tbCategoryFragment.title = categoryTitle
    }

    private fun getProductCategory() {
        val bundle = arguments
        categoryTitle = bundle?.getString("categoryName")
    }

    private fun setStatusBarColor(){
        activity?.window?.apply {
            val statusBarColors = ContextCompat.getColor(requireContext(), R.color.yellow)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

}