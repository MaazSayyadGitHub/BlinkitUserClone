package com.example.blinkituserclone.models

import com.example.blinkituserclone.roomdb.CartProducts

data class Orders(
    val orderId : String? = null,
    val orderList : List<CartProducts>? = null,
    val userAddress : String? = null,
    val orderStatus : Int? = null,
    val orderDate : String? = null,
    val orderingUserId : String? = null
)
