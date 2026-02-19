package com.example.blinkituserclone.models

data class OrderedItems(
    val orderId : String? = null,
    val itemDate : String? = null,
    val itemStatus : Int? = null,
    val itemDetails : String? = null,
    val itemPrice : Int? = null
)