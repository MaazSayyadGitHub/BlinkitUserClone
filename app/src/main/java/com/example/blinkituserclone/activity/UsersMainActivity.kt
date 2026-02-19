package com.example.blinkituserclone.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.blinkituserclone.CartListener
import com.example.blinkituserclone.adapters.AdapterCartProducts
import com.example.blinkituserclone.databinding.ActivityUsersMainBinding
import com.example.blinkituserclone.databinding.BsCartProductsBinding
import com.example.blinkituserclone.databinding.ItemViewBinding
import com.example.blinkituserclone.databinding.ItemViewCartProductBinding
import com.example.blinkituserclone.databinding.ItemViewProductCategoryBinding
import com.example.blinkituserclone.roomdb.CartProducts
import com.example.blinkituserclone.viewmodels.UserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class UsersMainActivity : AppCompatActivity() , CartListener {

    private lateinit var binding: ActivityUsersMainBinding
    private val viewModel : UserViewModel by viewModels()
    private lateinit var cartProductList : List<CartProducts>
    private lateinit var adapterCartProducts: AdapterCartProducts

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showCartBottomSheetItemCount() // show cart bottomSheet if there is item in cart on app open

        onBottomSheetCartClicked()
        getAllProductsIntoCartLayout()

        onButtonNextClicked()
    }

    private fun onButtonNextClicked() {
        binding.btnNext.setOnClickListener {
            startActivity(Intent(this, OrderPlaceActivity::class.java))
        }
    }

    private fun getAllProductsIntoCartLayout() {
        viewModel.getAllCartProducts().observe(this) {
            cartProductList = it
        }
    }

    private fun onBottomSheetCartClicked() {
        binding.llItemCart.setOnClickListener{

            val bottomSheetCartLayoutBinding =
                BsCartProductsBinding.inflate(LayoutInflater.from(this)) // binding of cartLayout

            // launching bottomSheet Dialog
            val bottomSheet = BottomSheetDialog(this)
            bottomSheet.setContentView(bottomSheetCartLayoutBinding.root)

            // set adapter on recyclerView
            adapterCartProducts = AdapterCartProducts()
            bottomSheetCartLayoutBinding.rvCartProductItems.adapter = adapterCartProducts
            adapterCartProducts.differ.submitList(cartProductList) // pass cartList to differ

            bottomSheetCartLayoutBinding.tvNumberOfProductCount.text = binding.tvNumberOfProductCount.text.toString()

            bottomSheetCartLayoutBinding.bottomSheetBtnNext.setOnClickListener {
                startActivity(Intent(this, OrderPlaceActivity::class.java))
            }

            bottomSheet.show()

        }
    }

    private fun showCartBottomSheetItemCount() {
        viewModel.fetchTotalItemCountFromSharedPref().observe(this){
            if (it > 0){
                binding.llCart.visibility = View.VISIBLE
                binding.tvNumberOfProductCount.text = it.toString()
            } else {
                binding.llCart.visibility = View.GONE
            }
        }
    }

    // this is the fun to show cart bottomSheet for first time when we add any itemProduct to cart
    override fun showCartLayout(itemCount: Int) {
        val previousCount = binding.tvNumberOfProductCount.text.toString().toInt()
        val updatedCount = previousCount + itemCount


        if (updatedCount > 0) {
            binding.llCart.visibility = View.VISIBLE
            binding.tvNumberOfProductCount.text = updatedCount.toString()
        } else {
            binding.llCart.visibility = View.GONE
            binding.tvNumberOfProductCount.text = "0"
        }
    }

    override fun savingCartItemIntoSharedPref(itemCount: Int) {
        // here we are fetching and observing what is the count is in SharedPref(it) right now
        // and then we are adding that count(it) with itemCount (incremented or decremented or added for any product)
        viewModel.fetchTotalItemCountFromSharedPref().observe(this){
            viewModel.savingCartItemIntoSharedPref(it + itemCount)
        }

    }

    override fun hideCartLayout() {
        binding.llCart.visibility = View.GONE
        binding.tvNumberOfProductCount.text = "0"
    }
}