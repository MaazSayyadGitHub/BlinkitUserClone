package com.example.blinkituserclone.roomdb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.blinkituserclone.models.Product
import javax.annotation.Nonnull

@Dao
interface Dao {

    // we have to get this method for to not close DB each time if we close app every time
    // and if we not write this method then our DB will be closed each time we close app(and our data
    // will not be saved for next time )
    @Query("Select * from CartProducts")
    fun getAllCartProduct() : LiveData<List<CartProducts>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartProduct(products: CartProducts)

    @Update
    suspend fun updateCartProduct(products: CartProducts)

    @Query("delete from CartProducts where productId = :productId")
    suspend fun deleteCartProduct(productId : String)

}