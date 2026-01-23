package com.example.blinkituserclone.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.blinkituserclone.Constant
import com.example.blinkituserclone.R
import com.example.blinkituserclone.Utils
import com.example.blinkituserclone.adapters.AdapterCartProducts
import com.example.blinkituserclone.databinding.ActivityOrderPlaceBinding
import com.example.blinkituserclone.databinding.AddressLayoutBinding
import com.example.blinkituserclone.models.Users
import com.example.blinkituserclone.viewmodels.UserViewModel
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.nio.charset.Charset

class OrderPlaceActivity : AppCompatActivity() , PaymentResultWithDataListener {

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

        // razorpay integration
        Checkout.preload(applicationContext)
        val checkout = Checkout()
        checkout.setKeyID(Constant.TEST_API_KEY)

    }


    private fun initRazorpayPayment() { //  will Open Checkout Screen of Razorpay

        /*
        *  You need to pass the current activity to let Razorpay create CheckoutActivity
        * */
        val activity: Activity = this
        val co = Checkout()

        try {
            val options = JSONObject()
            options.put("name","BlinkItClone")
            options.put("description","Food App")
            //You can omit the image option to fetch the image from the Dashboard
            options.put("image","http://example.com/image/rzp.jpg")
            options.put("theme.color", Color.YELLOW);
            options.put("currency","INR");
//            options.put("order_id", "order_DBJOWzybf0sJbb"); // optional
            options.put("amount","5000")//pass amount in currency subunits - 50rs

            // user can retry
            val retryObj = JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4); // how much time can retry
            options.put("retry", retryObj);

            // contact
            val prefill = JSONObject()
            prefill.put("email","maazsayyad2206@gmail.com")
            prefill.put("contact","+919130304812")
            options.put("prefill",prefill)

            co.open(activity,options)

        }catch (e: Exception){
            Toast.makeText(activity,"Error in payment: "+ e.message,Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    // Payment success
    override fun onPaymentSuccess(paymentId: String?, p1: PaymentData?) {
        Toast.makeText(this@OrderPlaceActivity, "Payment Success", Toast.LENGTH_LONG).show()
    }

    // payment fail
    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        Toast.makeText(this@OrderPlaceActivity, "Payment Failed \n Error : ${p1.toString()}", Toast.LENGTH_LONG).show()
    }


    private fun onOrderPlaceClicked() {
        binding.btnOrderNext.setOnClickListener {
            viewModel.getAddressStatusFromSharedPref().observe(this) {// true / false
                if (it) { // true - address already entered, now goto payment
                    initRazorpayPayment()

                } else { // false - need to pass address

                    val addressLayoutBinding = AddressLayoutBinding.inflate(LayoutInflater.from(this))

                    val addressDialog = AlertDialog.Builder(this)
                        .setView(addressLayoutBinding.root)
                        .create()

                    addressDialog.show()

                    addressLayoutBinding.btnAdd.setOnClickListener {
                        saveAddress(addressDialog, addressLayoutBinding)
                        initRazorpayPayment() // goto payment screen
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