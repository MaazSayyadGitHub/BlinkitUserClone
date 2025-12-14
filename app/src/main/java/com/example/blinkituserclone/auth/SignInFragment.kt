package com.example.blinkituserclone.auth

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.blinkituserclone.R
import com.example.blinkituserclone.Utils
import com.example.blinkituserclone.databinding.FragmentSignInBinding

class SignInFragment : Fragment() {

    private lateinit var binding : FragmentSignInBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        setStatusBarColor()

        getUserNo()
        onContinueBtnClick()

        return binding.root
    }

    private fun onContinueBtnClick() {
        binding.btnContinue.setOnClickListener {
            val phoneNumber = binding.etUserNumber.text.toString()

            if (phoneNumber.isEmpty() || phoneNumber.length != 10){
                Utils.showToast(requireContext(), "Please Type 10 digit phone Number")
            } else {
                // use bundle to pass number to next otp fragment
                val bundle = Bundle()
                bundle.putString("number", phoneNumber)
                findNavController().navigate(R.id.action_signInFragment_to_OTPFragment2, bundle) // nav component use and passing bundle
            }
        }
    }

    // to change color of btn
    private fun getUserNo() {
        binding.etUserNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(number: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val length = number?.length
                if (length == 10){
                    binding.btnContinue.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
                } else {
                    binding.btnContinue.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.lightGray))
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }

    // status bar color change
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