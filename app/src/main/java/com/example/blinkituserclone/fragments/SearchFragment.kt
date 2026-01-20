package com.example.blinkituserclone.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.blinkituserclone.CartListener
import com.example.blinkituserclone.R
import com.example.blinkituserclone.Utils
import com.example.blinkituserclone.adapters.AdapterProduct
import com.example.blinkituserclone.databinding.FragmentSearchBinding
import com.example.blinkituserclone.databinding.ItemViewBinding
import com.example.blinkituserclone.models.Product
import com.example.blinkituserclone.roomdb.CartProducts
import com.example.blinkituserclone.viewmodels.UserViewModel
import kotlinx.coroutines.launch


class SearchFragment : Fragment() {

    lateinit var binding: FragmentSearchBinding
    lateinit var adapterProduct : AdapterProduct
    private val viewModel : UserViewModel by viewModels()
    private var cartListener: CartListener ? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(layoutInflater)


        getAllProductsFromDb() // get All categories data
        goBackToHome()

        return binding.root
    }


    private fun goBackToHome() {
        binding.backBtn.setOnClickListener{
            findNavController().navigate(R.id.action_searchFragment_to_homeFragment)
        }
    }

    private fun onCategoryItemAddButtonClicked(product: Product, productCategoryBinding: ItemViewBinding) {
        productCategoryBinding.tvAdd.visibility = View.GONE
        productCategoryBinding.llProductCount.visibility = View.VISIBLE

        // Step 1 :- to set same cart item count on cart bottomSheet as in productCategoryBinding

        // as user click on add item, then this count will increase by 1
        var itemCount = productCategoryBinding.tvProductCount.text.toString().toInt()
        itemCount++
        productCategoryBinding.tvProductCount.text = itemCount.toString()

        cartListener?.showCartLayout(1)

        // Step 2 :- add this cart details into SharedPref
        cartListener?.savingCartItemIntoSharedPref(1)

        // Step 2 :- add this cart itemCount into sharedPref & whole cartProduct with itemCount into RoomDB
        product.itemCount = itemCount

        saveProductInRoomDb(product) // save In Room Db
        viewModel.updateItemCount(product, itemCount) // save in firebase

    }

    private fun onIncrementButtonClicked(product: Product, productBinding: ItemViewBinding){
        // as user click on increment btn, then this count will increase by 1
        var itemCount = productBinding.tvProductCount.text.toString().toInt()
        itemCount++

        if (product.productStock!! + 1 > itemCount){ // item should be in Stock
            productBinding.tvProductCount.text = itemCount.toString()

            cartListener?.showCartLayout(1)


            // Step 2 :- add this cart itemCount into sharedPref & cart Product into RoomDB
            product.itemCount = itemCount

            cartListener?.savingCartItemIntoSharedPref(1) // save in SharedPref
            saveProductInRoomDb(product) // save in Room Db
            viewModel.updateItemCount(product, itemCount) // save in Firebase
        } else {
            Utils.showToast(requireContext(), "Item OutOf Stock")
        }

    }

    private fun onDecrementButtonClicked(product: Product, productBinding: ItemViewBinding){

        // as user click on decrement btn, then this count will decrease by 1
        var itemCount = productBinding.tvProductCount.text.toString().toInt()


        if (itemCount > 0){
            itemCount--
            productBinding.tvProductCount.text = itemCount.toString()
            product.itemCount = itemCount

            if (itemCount == 0){
                productBinding.llProductCount.visibility = View.GONE
                productBinding.tvAdd.visibility = View.VISIBLE

                productBinding.tvProductCount.text = "0" // set it to default 0

                cartListener?.showCartLayout(-1) // -1 from cartLayout also
                cartListener?.savingCartItemIntoSharedPref(-1) // save in SharedPref

                // delete item/product from room db
                viewModel.deleteProductInRoomDb(product.productRandomId!!)
                viewModel.updateItemCount(product, itemCount) // save in firebase

                return // should be out off function so below code should not run
            }
        }

        cartListener?.showCartLayout(-1)

        // Step 2 :- add this cart itemCount into sharedPref & cart Product into RoomDB

        cartListener?.savingCartItemIntoSharedPref(-1) // save in SharedPref
        saveProductInRoomDb(product) // save in Room Db with itemCount
        viewModel.updateItemCount(product, itemCount) // save in firebase

    }

    private fun saveProductInRoomDb(product: Product) {
        val cartProducts = CartProducts(
            productId = product.productRandomId!!,
            productTitle = product.productTitle,
            productQuantity = product.productQuantity.toString() + product.productUnit.toString(),
            productPrice = "₹${product.productPrice}",
            productCount = product.itemCount,
            productStock = product.productStock,
            productImage = product.productImageUris?.get(0),
            productCategory = product.productCategory,
            adminUid = product.adminUid
        )

        viewModel.addProductInRoomDb(cartProducts)
    }

    private fun getAllProductsFromDb() { // get category wise data

        binding.shimmerViewContainer.visibility = View.VISIBLE

        lifecycleScope.launch {
            viewModel.fetchAllProductsFromDB().collect{
                if (it.isEmpty()){
                    // empty then show text not RCV
                    binding.rvProducts.visibility = View.GONE
                    binding.tvText.visibility = View.VISIBLE
                } else {
                    // if not empty then show RCV and not text
                    binding.rvProducts.visibility = View.VISIBLE
                    binding.tvText.visibility = View.GONE
                }

                adapterProduct = AdapterProduct(
                    ::onCategoryItemAddButtonClicked,
                    ::onIncrementButtonClicked,
                    ::onDecrementButtonClicked
                ) // passing HOF, to check button click
                binding.rvProducts.adapter = adapterProduct
                adapterProduct.differ.submitList(it) // got 'it' from collect.

                adapterProduct.submitListOfFilter(it) // adapter fun for search filter

                binding.etSearch.addTextChangedListener { text ->
                    adapterProduct.filter.filter(text)
                }

                binding.shimmerViewContainer.visibility = View.GONE

            }
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CartListener){
            cartListener = context
        } else {
            throw ClassCastException("Please Implement cart listener")
        }
    }


}