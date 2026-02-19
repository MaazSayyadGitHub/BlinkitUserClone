package com.example.blinkituserclone.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.blinkituserclone.Utils
import com.example.blinkituserclone.models.OrderedItems
import com.example.blinkituserclone.models.Orders
import com.example.blinkituserclone.models.Product
import com.example.blinkituserclone.models.Users
import com.example.blinkituserclone.roomdb.CartProductDatabase
import com.example.blinkituserclone.roomdb.CartProducts
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences = application.getSharedPreferences("Pref", Context.MODE_PRIVATE)
    private lateinit var listOfProduct : LiveData<List<CartProducts>>

    // here we are creating instance of DB and by that we are creating dao reference variable
    // and by dao reference we will access insert/update/etc functions
    private val cartProductDao = CartProductDatabase.getDataBaseInstance(application).cartProductDao()

    // Room DB
    // get All cartProducts from Room Db
    fun getAllCartProducts() : LiveData<List<CartProducts>>{
        return cartProductDao.getAllCartProduct()
    }

    // Insert into DB
    fun addProductInRoomDb(product: CartProducts) {
        viewModelScope.launch(Dispatchers.IO){
            cartProductDao.insertCartProduct(product)
        }
    }

    // update into DB
    fun updatedProductInRoomDb(product: CartProducts) {
        viewModelScope.launch(Dispatchers.IO){
            cartProductDao.updateCartProduct(product)
        }
    }

    // delete into DB
    fun deleteProductInRoomDb(productId: String){
        viewModelScope.launch(Dispatchers.IO){
            cartProductDao.deleteCartProduct(productId)
        }
    }

    fun deleteAllCartProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            cartProductDao.deleteAllCartProducts()
        }
    }

    fun updateItemCount(product: Product, itemCount: Int) {
        FirebaseDatabase.getInstance().getReference("Admin").child("AllProducts/${product.productRandomId}").child("itemCount").setValue(itemCount)
        FirebaseDatabase.getInstance().getReference("Admin").child("ProductCategory/${product.productCategory}/${product.productRandomId}").child("itemCount").setValue(itemCount)
        FirebaseDatabase.getInstance().getReference("Admin").child("ProductType/${product.productType}/${product.productRandomId}").child("itemCount").setValue(itemCount)

        Log.d("UPDATE : ", "RandomID = "+product.productRandomId.toString() +" -- itemCount = "+itemCount.toString())
    }

    // update item Count to 0 & stock - itemCount
    fun saveProductAfterOrder(product : CartProducts, stock : Int){
        FirebaseDatabase.getInstance().getReference("Admin").child("AllProducts/${product.productId}").child("itemCount").setValue(0)
        FirebaseDatabase.getInstance().getReference("Admin").child("ProductCategory/${product.productCategory}/${product.productId}").child("itemCount").setValue(0)
        FirebaseDatabase.getInstance().getReference("Admin").child("ProductType/${product.productType}/${product.productId}").child("itemCount").setValue(0)

        FirebaseDatabase.getInstance().getReference("Admin").child("AllProducts/${product.productId}").child("productStock").setValue(stock)
        FirebaseDatabase.getInstance().getReference("Admin").child("ProductCategory/${product.productCategory}/${product.productId}").child("productStock").setValue(stock)
        FirebaseDatabase.getInstance().getReference("Admin").child("ProductType/${product.productType}/${product.productId}").child("productStock").setValue(stock)
    }


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

            }
        }

        db.addValueEventListener(eventListener)
        awaitClose { db.removeEventListener(eventListener) } // as done event then close/remove it
    }

    fun getAllOrders() : Flow<List<Orders>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("Admin").child("Orders").orderByChild("orderStatus")

        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listOfOrder = ArrayList<Orders>()
                for (orders in snapshot.children){
                    val order = orders.getValue(Orders::class.java) ?: Orders()
                    if (order.orderingUserId == Utils.getUserID()){
                        listOfOrder.add(order)
                    }
                }

                trySend(listOfOrder)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        db.addValueEventListener(eventListener)
        awaitClose { db.removeEventListener(eventListener) }
    }

    fun getAllOrdersById(orderId : String) : Flow<List<CartProducts>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("Admin").child("Orders").child(orderId)

        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                // we are doing this without for loop because there will be just single entry (which we have passed - orderId)
                val order = snapshot.getValue(Orders::class.java)
                // here orderList will be send/return from that order
                    trySend(order?.orderList ?: emptyList()) // if order/orderList null then we will send emptyList

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }

        db.addValueEventListener(eventListener)
        awaitClose { db.removeEventListener(eventListener) }
    }

    // categoryName passing
    fun getCategoryProduct(category: String) : Flow<List<Product>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("Admin")
            .child("ProductCategory/${category}") // path - Admin -> ProductCategory -> CategoryName -> fetch all

        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = ArrayList<Product>()

                for (product in snapshot.children){
                    val prod = product.getValue(Product::class.java)
                    products.add(prod!!)
                }

                trySend(products)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        }

        db.addValueEventListener(eventListener)
        awaitClose { db.removeEventListener(eventListener) } // as eventListener added into db then close or remove eventListener

    }

    fun savingCartItemIntoSharedPref(itemCount : Int){
        sharedPreferences.edit().putInt("itemCount", itemCount).apply()
    }

    // to get sharedPref itemCount and observe in activity to in cartBottomSheet
    fun fetchTotalItemCountFromSharedPref() : MutableLiveData<Int> {
        val sharedPrefItemCount = MutableLiveData<Int>()
        sharedPrefItemCount.value = sharedPreferences.getInt("itemCount", 0)
        return sharedPrefItemCount
    }

    fun saveAddressStatusInSharedPref(){
        sharedPreferences.edit().putBoolean("addressStatus", true).apply()
    }

    fun getAddressStatusFromSharedPref() : MutableLiveData<Boolean> {
        val status = MutableLiveData<Boolean>()
        status.value = sharedPreferences.getBoolean("addressStatus", false) // initially false
        return status
    }

    fun saveUserAddressInFirebase(user: Users) {
        FirebaseDatabase.getInstance().getReference("All Users")
            .child("Users")
            .child(Utils.getUserID().toString())
            .setValue(user)
    }

    fun getUserAddressFromFirebase(callback : (String?) -> Unit) {
        val db = FirebaseDatabase.getInstance().getReference("All Users")
            .child("Users")
            .child(Utils.getUserID().toString())

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val address = snapshot.getValue(Users::class.java)
                    callback(address?.userAddress)
                } else {
                    callback(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }

    fun saveOrderProductInFirebase(order : Orders) {
        FirebaseDatabase.getInstance().getReference("Admin")
            .child("Orders")
            .child(order.orderId.toString())
            .setValue(order)
            .addOnSuccessListener {
                println("address success")
            }
            .addOnFailureListener {
                println("address failed")
            }
    }

}