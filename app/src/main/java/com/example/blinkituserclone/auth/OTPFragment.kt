package com.example.blinkituserclone.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.blinkituserclone.R
import com.example.blinkituserclone.Utils
import com.example.blinkituserclone.activity.UsersMainActivity
import com.example.blinkituserclone.databinding.FragmentOTPBinding
import com.example.blinkituserclone.models.Users
import com.example.blinkituserclone.viewmodels.AuthViewModel
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.storage.internal.Util
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

class OTPFragment : Fragment() {

    private lateinit var binding : FragmentOTPBinding
    private lateinit var phoneNumber : String
    private val viewModel : AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOTPBinding.inflate(inflater, container, false)

        getUserNumber()
        customizingOtpFocus()
        onBackBtnClicked()
        sendOtp()
        onLoginButtonClicked()

        return binding.root
    }

    private fun onLoginButtonClicked() {
        binding.loginBtn.setOnClickListener {
            Utils.showDialog(requireContext(), "Signing you...")
            // it is a array of all textInputEdittext
            val editTexts = arrayOf(binding.etOtp1, binding.etOtp2, binding.etOtp3, binding.etOtp4, binding.etOtp5, binding.etOtp6)

            // here we are getting otp which is entered on textInputEdittext and making that to string and storing into variable
            val otp = editTexts.joinToString("") {// extension function
                it.text.toString()
            }

            if (otp.length < editTexts.size){
                Utils.showToast(requireContext(), "Please Enter Right OTP")
                Utils.hideDialog()
            } else {
                // clear all otp edittext
                editTexts.forEach {
                    it.text?.clear()
                    it.clearFocus()
                }
                // verify otp
                verifyOTP(otp)
            }
        }
    }

    private fun verifyOTP(otp: String) {

        val user = Users(uid = Utils.getUserID(), userPhoneNumber = phoneNumber, userAddress = null)

        viewModel.signInWithPhoneAuthCredential(otp, phoneNumber, user)

        lifecycleScope.launch {
            viewModel.isSignedInSuccessfully
                .drop(1) // it will not check first default value which is false set
                // (till verify otp and return true it is giving us false which is set initial,
                // so that's why both if and else are running) so we have dropped first response
                .collect{
                if (it){
                    Utils.hideDialog()
                    Utils.showToast(requireContext(), "Logged In Successful")
                    Log.d("IT OF OTP", it.toString())
                    Log.d("SUCCESS OTP", otp)
                    // here we will switch between activities that's why Intent
                    startActivity(Intent(requireContext(), UsersMainActivity::class.java))
                    requireActivity().finish()

                } else {
                    Utils.hideDialog()
                    Log.d("FAIL OTP", otp)
                    Utils.showToast(requireContext(), "OTP not matched")
                }
            }
        }

    }

    private fun sendOtp() {
        Utils.showDialog(requireContext(), "Sending OTP Please wait...") // dialog
        viewModel.apply {
            sendOTP(phoneNumber, requireActivity())

            lifecycleScope.launch {
                otpSent.collect{ // collect is suspend fun
                    if (it){ // if true
                        Log.d("TAG", "Boolean value - $it")
                        Utils.hideDialog() // hide dialog
                        Utils.showToast(requireContext(), "Otp sent Successfully")
                    }
                }
            }
        }
    }

    private fun onBackBtnClicked() {
        binding.tbOtpFrament.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_OTPFragment2_to_signInFragment)
        }
    }

    private fun customizingOtpFocus() {
        val editTexts = arrayOf(binding.etOtp1, binding.etOtp2, binding.etOtp3, binding.etOtp4, binding.etOtp5, binding.etOtp6)
        for (i in editTexts.indices){

            // it will check from 0,1,2,3,4,5 indexes
            editTexts[i].addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (text?.length == 1){
                        if (i < editTexts.size - 1){ // means if its not last one
                            editTexts[i + 1].requestFocus() // then do focus forward, till last one
                        }
                    } else if (text?.length == 0){ // if there is nothing in edittext/ if removed/delete in edittext
                        if (i > 0){ // if its not first one
                            editTexts[i - 1].requestFocus() // then do focus backward
                        }
                    }
                }

                // after text change we will check
                override fun afterTextChanged(text: Editable?) {

                }

            })
        }
    }

    private fun getUserNumber() {
        val bundle = arguments
        phoneNumber = bundle?.getString("number").toString()
        Log.d("TAG", "$phoneNumber")
        binding.etUserNumber.text = phoneNumber
    }


}