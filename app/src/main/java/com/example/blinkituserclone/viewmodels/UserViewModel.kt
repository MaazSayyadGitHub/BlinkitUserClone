package com.example.blinkituserclone.viewmodels

import androidx.lifecycle.ViewModel
import com.example.blinkituserclone.models.Product
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class UserViewModel : ViewModel() {



    fun fetchAllProductsFromDB(): Flow<List<Product>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("Admin").child("AllProducts")

        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = ArrayList<Product>()

                for (product in snapshot.children){
                    val prod = product.getValue(Product::class.java) // add all value into Product class reference
                    products.add(prod!!) // add each entry of product to products class
                }

                trySend(products)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }

        db.addValueEventListener(eventListener)
        awaitClose { db.removeEventListener(eventListener) } // as done event then close/remove it
    }

}