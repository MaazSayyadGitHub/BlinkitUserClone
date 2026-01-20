package com.example.blinkituserclone.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CartProducts::class], version = 1, exportSchema = false) // if true then json file will be create
abstract class CartProductDatabase : RoomDatabase() {

    abstract fun cartProductDao() : Dao

    companion object {

        @Volatile
        var INSTANCE : CartProductDatabase ? = null

        fun getDataBaseInstance(context: Context) : CartProductDatabase {
            if (INSTANCE == null) {
                synchronized(this){
                    INSTANCE = Room.databaseBuilder(context.applicationContext, CartProductDatabase::class.java, "CartProductsDb")
                        .build()
                }
            }
            return INSTANCE!!
        }
    }
}