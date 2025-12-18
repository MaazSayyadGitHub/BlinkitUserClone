package com.example.blinkituserclone.auth

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.blinkituserclone.R
import com.example.blinkituserclone.activity.UsersMainActivity
import com.example.blinkituserclone.databinding.FragmentSplashBinding
import com.example.blinkituserclone.viewmodels.AuthViewModel
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch


class SplashFragment : Fragment() {


    private lateinit var binding: FragmentSplashBinding
    val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        setStatusBarColor()

        Handler(Looper.getMainLooper()).postDelayed({

            lifecycleScope.launch {
                authViewModel.isCurrentUser
                    .collect {
                        if (it){
                            startActivity(Intent(requireContext(), UsersMainActivity::class.java))
                            requireActivity().finish()
                        } else {
                            findNavController().navigate(R.id.action_splashFragment_to_signInFragment3)
                        }
                }
            }
        }, 3000)


        return binding.root
    }

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