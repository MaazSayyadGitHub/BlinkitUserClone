package com.example.blinkituserclone

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.blinkituserclone.databinding.ProgressDialogBinding
import com.google.firebase.auth.FirebaseAuth

object Utils {

    private var dialog : AlertDialog? = null

    fun showDialog(context: Context, message: String){
        val progress = ProgressDialogBinding.inflate(LayoutInflater.from(context))
        progress.tvMessage.text = message
        dialog = AlertDialog.Builder(context)
            .setView(progress.root)
            .setCancelable(false)
            .create()
        dialog!!.show()
    }

    fun hideDialog(){
        dialog?.hide()
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    private var instance : FirebaseAuth? = null
    fun getFirebaseAuthInstance() : FirebaseAuth {
        if (instance == null){
            instance = FirebaseAuth.getInstance()
        }

        return instance!!
    }

    fun getUserID() : String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }
}