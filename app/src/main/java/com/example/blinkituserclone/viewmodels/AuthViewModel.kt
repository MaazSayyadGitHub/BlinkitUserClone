package com.example.blinkituserclone.viewmodels

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.blinkituserclone.Utils
import com.example.blinkituserclone.models.Users
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.TimeUnit

class AuthViewModel : ViewModel() {

    private val _verificationID = MutableStateFlow<String?>(null)
    private val _otpSent = MutableStateFlow(false) // to check otp is sent or not
    val otpSent = _otpSent // we will use this outside viewModel

    private val _isSignedInSuccessfully = MutableStateFlow(false)
    val isSignedInSuccessfully = _isSignedInSuccessfully // we will use it in another classes

    // to check already login or not
    private val _isCurrentUser = MutableStateFlow(false)
    val isCurrentUser = _isCurrentUser

    init {
        Utils.getFirebaseAuthInstance().currentUser?.let {
            isCurrentUser.value = true
        }
    }

    fun sendOTP(userPhoneNumber : String, activity: Activity) {

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                otpSent.value = true
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.e("OTP_ERROR", "Firebase OTP failed: ${e.message}", e)
                otpSent.value = false
            }

            override fun onCodeSent( // on otp sent
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                _verificationID.value = verificationId
                _otpSent.value = true // otp sent successfully flag
            }
        }

        val options = PhoneAuthOptions.newBuilder(Utils.getFirebaseAuthInstance())
            .setPhoneNumber("+91$userPhoneNumber") // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun signInWithPhoneAuthCredential(otp : String, userPhoneNumber : String, user : Users) {
        // here passing otp to cross check in auth and give us response success or error
        val credential = PhoneAuthProvider.getCredential(_verificationID.value.toString(), otp)

        Utils.getFirebaseAuthInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // if success then we will add user details to firebase DB
                    FirebaseDatabase.getInstance().getReference("All Users")
                        .child("Users")
                        .child(user.uid!!)
                        .setValue(user)

                    _isSignedInSuccessfully.value = true
                } else {
                    _isSignedInSuccessfully.value = false
                }
            }
    }
}