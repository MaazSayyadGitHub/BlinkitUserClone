package com.example.blinkituserclone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.blinkituserclone.adapters.AdapterProduct
import com.example.blinkituserclone.databinding.FragmentSearchBinding
import com.example.blinkituserclone.viewmodels.UserViewModel
import kotlinx.coroutines.launch


class SearchFragment : Fragment() {

    lateinit var binding: FragmentSearchBinding
    lateinit var adapterProduct : AdapterProduct
    private val viewModel : UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(layoutInflater)

        adapterProduct = AdapterProduct()

        getAllProductsFromDb() // get All categories data
        goBackToHome()

        return binding.root
    }


    private fun goBackToHome() {
        binding.backBtn.setOnClickListener{
            findNavController().navigate(R.id.action_searchFragment_to_homeFragment)
        }
    }

    private fun getAllProductsFromDb() { // get category wise data

        binding.shimmerViewContainer.visibility = View.VISIBLE

        lifecycleScope.launch {
            viewModel.fetchAllProductsFromDB().collect{
                if (it.isEmpty()){
                    // empty then show text not RCV
                    binding.rvProducts.visibility = View.GONE
                    binding.tvText.visibility = View.VISIBLE
                } else {
                    // if not empty then show RCV and not text
                    binding.rvProducts.visibility = View.VISIBLE
                    binding.tvText.visibility = View.GONE
                }

                adapterProduct = AdapterProduct() // passing HOF, to check button click
                binding.rvProducts.adapter = adapterProduct
                adapterProduct.differ.submitList(it) // got it from collect.

                adapterProduct.submitListOfFilter(it) // adapter fun

                binding.etSearch.addTextChangedListener { text ->
                    adapterProduct.filter.filter(text)
                }

                binding.shimmerViewContainer.visibility = View.GONE

            }
        }

    }


}