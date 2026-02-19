package com.example.blinkituserclone.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.blinkituserclone.R
import com.example.blinkituserclone.R.color
import com.example.blinkituserclone.adapters.AdapterCartProducts
import com.example.blinkituserclone.databinding.FragmentOrderDetailsBinding
import com.example.blinkituserclone.roomdb.CartProducts
import com.example.blinkituserclone.viewmodels.UserViewModel
import kotlinx.coroutines.launch

class OrderDetailsFragment : Fragment() {

    private lateinit var binding : FragmentOrderDetailsBinding
    private var orderStatus : Int ? = null
    private var orderId : String = ""

    private val viewModel: UserViewModel by viewModels()
    private lateinit var adapterCartProducts: AdapterCartProducts

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderDetailsBinding.inflate(inflater)

        getOrderDetails()
        setStatusColors()
        getAllOrderById()
        onBackClicked()

        return binding.root
    }

    private fun onBackClicked() {
        binding.tbOrder.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun getAllOrderById() {
        lifecycleScope.launch {
            viewModel.getAllOrdersById(orderId).collect { cartList : List<CartProducts> ->

                adapterCartProducts = AdapterCartProducts()
                binding.orderRV.adapter = adapterCartProducts
                adapterCartProducts.differ.submitList(cartList)
            }
        }

    }

    private fun setStatusColors() {
        binding.apply {
            when (orderStatus) {
                0 -> {
                    img1.backgroundTintList = ContextCompat.getColorStateList(requireContext(), color.blue)
                    view1.backgroundTintList = ContextCompat.getColorStateList(requireContext(),color.blue)
                }
                1 -> {
                    img1.backgroundTintList = ContextCompat.getColorStateList(requireContext(), color.blue)
                    view1.backgroundTintList = ContextCompat.getColorStateList(requireContext(),color.blue)
                    img2.backgroundTintList = ContextCompat.getColorStateList(requireContext(),color.blue)
                    view2.backgroundTintList = ContextCompat.getColorStateList(requireContext(),color.blue)
                }
                2 -> {
                    img1.backgroundTintList = ContextCompat.getColorStateList(requireContext(), color.blue)
                    view1.backgroundTintList = ContextCompat.getColorStateList(requireContext(),color.blue)
                    img2.backgroundTintList = ContextCompat.getColorStateList(requireContext(),color.blue)
                    view2.backgroundTintList = ContextCompat.getColorStateList(requireContext(),color.blue)
                    img3.backgroundTintList = ContextCompat.getColorStateList(requireContext(),color.blue)
                    view3.backgroundTintList = ContextCompat.getColorStateList(requireContext(),color.blue)
                }
                3 -> {
                    img1.backgroundTintList = ContextCompat.getColorStateList(requireContext(), color.blue)
                    view1.backgroundTintList = ContextCompat.getColorStateList(requireContext(),color.blue)
                    img2.backgroundTintList = ContextCompat.getColorStateList(requireContext(),color.blue)
                    view2.backgroundTintList = ContextCompat.getColorStateList(requireContext(),color.blue)
                    img3.backgroundTintList = ContextCompat.getColorStateList(requireContext(),color.blue)
                    view3.backgroundTintList = ContextCompat.getColorStateList(requireContext(),color.blue)
                    img4.backgroundTintList = ContextCompat.getColorStateList(requireContext(),color.blue)
                }
            }
        }
    }

    private fun getOrderDetails() {
        val bundle = arguments
        orderStatus = bundle?.getInt("orderStatus")
        orderId = bundle?.getString("orderID") ?: " "

    }

}