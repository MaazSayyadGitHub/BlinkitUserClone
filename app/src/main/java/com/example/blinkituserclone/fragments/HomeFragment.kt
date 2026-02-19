package com.example.blinkituserclone.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.blinkituserclone.Constant
import com.example.blinkituserclone.R
import com.example.blinkituserclone.adapters.AdapterCategory
import com.example.blinkituserclone.databinding.FragmentHomeBinding
import com.example.blinkituserclone.models.Category
import com.example.blinkituserclone.viewmodels.UserViewModel
import kotlinx.coroutines.launch
import kotlin.math.log


class HomeFragment : Fragment() {

    val viewModel : UserViewModel by viewModels()
    private lateinit var binding : FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)

        setStatusBarColor()
        setRecyclerView()
        navigateToSearchFragment()

        getAllProductsFromRoomDB()
        onProfileClicked()

        return binding.root
    }

    private fun onProfileClicked() {
        binding.ivProfile.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }
    }

    private fun getAllProductsFromRoomDB() {
        viewModel.getAllCartProducts().observe(viewLifecycleOwner) {
            for (i in it) {
                Log.d("AllProductsFromRoomDB", "ProductTitle"+ i.productTitle.toString())
                Log.d("AllProductsFromRoomDB", "ProductCount" +i.productCount.toString())
            }
        }
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

        binding.rvCategory.adapter = AdapterCategory(category, ::onCategoryItemClicked) // this will know us which category is clicked
    }

    private fun onCategoryItemClicked(category: Category) {
        val bundle = Bundle()
        bundle.putString("categoryName", category.title)

        findNavController().navigate(R.id.action_homeFragment_to_categoryFragment, bundle)
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