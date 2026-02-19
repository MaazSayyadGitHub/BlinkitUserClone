package com.example.blinkituserclone.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blinkituserclone.R
import com.example.blinkituserclone.adapters.AdapterOrders
import com.example.blinkituserclone.databinding.FragmentOrderBinding
import com.example.blinkituserclone.models.OrderedItems
import com.example.blinkituserclone.models.Orders
import com.example.blinkituserclone.viewmodels.UserViewModel
import kotlinx.coroutines.launch

class OrderFragment : Fragment() {

    private lateinit var binding : FragmentOrderBinding
    private val viewModel : UserViewModel by viewModels()
    private lateinit var adapterOrders: AdapterOrders

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderBinding.inflate(layoutInflater)

        onBackButtonClicked()
        getAllOrdersFromFirebase()

        return binding.root
    }

    private fun getAllOrdersFromFirebase() {
        binding.orderShimmerContainer.visibility = View.VISIBLE

        lifecycleScope.launch {
            viewModel.getAllOrders().collect { it: List<Orders> ->
                binding.apply {

                    if (it.isNotEmpty()){

                        val orderList = ArrayList<OrderedItems>()
                        for (orders in it) {
                            val title = StringBuilder()
                            var totalPrice = 0

                            for (product in orders.orderList.orEmpty()) {
                                val price = product.productPrice?.substring(1)
                                    ?.toInt() // ₹14 = here ₹ = 0 index and 14 = 1 index, so we are not getting ₹ sign here.
                                val itemCount = product.productCount ?: 0

                                totalPrice += (price?.times(itemCount)!!) // totalPrice += price * itemCount

                                title.append("${product.productCategory}, ")
                            }

                            val orderedItems = OrderedItems(
                                orders.orderId,
                                orders.orderDate,
                                orders.orderStatus,
                                title.toString(),
                                totalPrice
                            )
                            orderList.add(orderedItems)
                        }

                        adapterOrders = AdapterOrders(::onOrderItemClicked)
                        adapterOrders.differ.submitList(orderList)
                        rvCheckOut.adapter = adapterOrders
                        rvCheckOut.layoutManager = LinearLayoutManager(requireContext())
                        binding.orderShimmerContainer.visibility = View.GONE
                    }
                }
            }
        }

    }

    fun onOrderItemClicked(order : OrderedItems) {

        val bundle = Bundle()
        bundle.putInt("orderStatus", order.itemStatus ?: -1)
        bundle.putString("orderID", order.orderId)
        findNavController().navigate(R.id.action_orderFragment_to_orderDetailsFragment, bundle)


    }

    private fun onBackButtonClicked() {
        binding.tbOrder.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_orderFragment_to_profileFragment)
        }
    }

}