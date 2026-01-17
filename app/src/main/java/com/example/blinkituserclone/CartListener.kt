package com.example.blinkituserclone

interface CartListener {

    fun showCartLayout(itemCount : Int)

    fun savingCartItemIntoSharedPref(itemCount: Int)
}