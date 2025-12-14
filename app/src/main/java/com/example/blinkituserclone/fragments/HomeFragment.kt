package com.example.blinkituserclone.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.blinkituserclone.Constant
import com.example.blinkituserclone.R
import com.example.blinkituserclone.adapters.AdapterCategory
import com.example.blinkituserclone.databinding.FragmentHomeBinding
import com.example.blinkituserclone.models.Category


class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)

        setStatusBarColor()
        setRecyclerView()
        navigateToSearchFragment()

        return binding.root
    }

    private fun navigateToSearchFragment() {
        binding.searchEt.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }

    private fun setRecyclerView() {

        val category = ArrayList<Category>()

        for (i in 0 until Constant.allProductsCategory.size){
            category.add(Category(Constant.allProductsCategory[i], Constant.allProductCategoryImages[i]))
        }

        binding.rvCategory.adapter = AdapterCategory(category)
    }

    private fun setStatusBarColor(){
        activity?.window?.apply {
            val statusBarColors = ContextCompat.getColor(requireContext(), R.color.orange)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

}