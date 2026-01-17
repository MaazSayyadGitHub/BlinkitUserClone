package com.example.blinkituserclone.roomdb

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.blinkituserclone.models.Product

@Entity(tableName = "CartProducts")
data class CartProducts(

    @PrimaryKey
    val productId: String = "Random", // cant apply nullability for primaryKey

    val productTitle : String ? = null,
    val productQuantity : String ? = null,
    val productPrice : String ? = null,
    val productCount : Int ? = null,
    val productStock : Int ? = null,
    val productImage : String ? = null,
    val productCategory : String ? = null,
    val adminUid : String ? = null,
)