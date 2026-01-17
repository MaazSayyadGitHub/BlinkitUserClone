package com.example.blinkituserclone.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.blinkituserclone.R
import com.example.blinkituserclone.Utils
import com.example.blinkituserclone.adapters.AdapterCartProducts
import com.example.blinkituserclone.databinding.ActivityOrderPlaceBinding
import com.example.blinkituserclone.databinding.AddressLayoutBinding
import com.example.blinkituserclone.models.Users
import com.example.blinkituserclone.viewmodels.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrderPlaceActivity : AppCompatActivity() {

    private lateinit var binding : ActivityOrderPlaceBinding
    private val viewModel: UserViewModel by viewModels()
    private lateinit var adapterCartProducts : AdapterCartProducts

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setStatusBarColor()
        onNavigationBackClick()
        getAllCartsItemFromRoomDb()

        onOrderPlaceClicked()

    }

    private fun onOrderPlaceClicked() {
        binding.btnOrderNext.setOnClickListener {
            viewModel.getAddressStatusFromSharedPref().observe(this) {// true / false
                if (it) { // true - address already entered, now goto payment

                } else { // false - need to pass address

                    val addressLayoutBinding = AddressLayoutBinding.inflate(LayoutInflater.from(this))

                    val addressDialog = AlertDialog.Builder(this)
                        .setView(addressLayoutBinding.root)
                        .create()

                    addressDialog.show()

                    addressLayoutBinding.btnAdd.setOnClickListener {
                        saveAddress(addressDialog, addressLayoutBinding)
                    }
                }
            }
        }
    }

    private fun saveAddress(addressDialog: AlertDialog, addressLayoutBinding: AddressLayoutBinding) {

        Utils.showDialog(this, "Processing..")

        val pinCode = addressLayoutBinding.etPinCode.text.toString()
        val phoneNo = addressLayoutBinding.etPhoneNo.text.toString()
        val state = addressLayoutBinding.etState.text.toString()
        val district = addressLayoutBinding.etDistrict.text.toString()
        val address = addressLayoutBinding.etAddress.text.toString()

        val fullAddress = "$pinCode, $district($state), $address, $phoneNo" // make one line address

        val user = Users(
            uid = Utils.getUserID(),
            userPhoneNumber = phoneNo,
            userAddress = fullAddress
        )

        // do this work in coroutine background
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.saveUserAddressInFirebase(user) // save in FireBase
            viewModel.saveAddressStatusInSharedPref()
        }

        Utils.showToast(this, "Saved Address Successfully")

        addressDialog.dismiss()
        Utils.hideDialog()
    }

    private fun onNavigationBackClick() {
        binding.tbOrder.setNavigationOnClickListener {
            startActivity(Intent(this@OrderPlaceActivity, UsersMainActivity::class.java))
            finish()
        }
    }

    private fun getAllCartsItemFromRoomDb() {
        viewModel.getAllCartProducts().observe(this) { cartProductList -> // this is (it<List<CartProducts>>)

            adapterCartProducts = AdapterCartProducts()
            binding.rvOrderCartProductItems.adapter = adapterCartProducts
            adapterCartProducts.differ.submitList(cartProductList)

            var totalPrice = 0

            for (product in cartProductList) {
                val price = product.productPrice?.substring(1)?.toInt() // ₹14 = here ₹ = 0 and 14 = 1, so we are not getting ₹ sign here.
                val itemCount = product.productCount!!

                totalPrice += (price?.times(itemCount)!!) // totalPrice += price * itemCount
            }

            binding.tvSubTotal.text = totalPrice.toString()

            val deliveryCharges = "₹50"

            if (totalPrice < 200) {
                binding.tvDelCharge.text = "₹15"
            } else {
                binding.tvDelCharge.text = deliveryCharges
            }

            val grandTotal = (totalPrice + deliveryCharges.substring(1).toInt()) // will get 15 or 50 only not ₹ sign
            binding.tvGrandTotal.text = grandTotal.toString()

        }
    }

    private fun setStatusBarColor(){
        window?.apply {
            val statusBarColors = ContextCompat.getColor(this@OrderPlaceActivity, R.color.orange)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }
}