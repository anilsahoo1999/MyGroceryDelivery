package com.aks.mygrocery.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.aks.mygrocery.R
import com.aks.mygrocery.adapter.PriceAdapter
import com.aks.mygrocery.adapter.SearchAdapter
import com.aks.mygrocery.app.MyGroceryApp
import com.aks.mygrocery.databinding.ActivitySearchBinding
import com.aks.mygrocery.eventbus.MessageEvent
import com.aks.mygrocery.fragment.HomeFragment
import com.aks.mygrocery.models.CartItemModel
import com.aks.mygrocery.models.PricePerKgModel
import com.aks.mygrocery.models.ProductModel
import com.aks.mygrocery.utils.Constants
import com.aks.mygrocery.utils.Constants.allProductList
import com.aks.mygrocery.utils.Constants.cartList
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import java.lang.Runnable

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchAdapter: SearchAdapter
    private var mHandler = Handler(Looper.myLooper()!!)
    private lateinit var priceAdapter: PriceAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search)
        searchAdapter = SearchAdapter()
        priceAdapter = PriceAdapter()

        binding.recyclerRecentSearch.adapter = searchAdapter

        if (allProductList.size > 0) {

        } else {
            getAllProduct()
        }

        performClickEvent()
    }

    private fun getAllProduct() {
        CoroutineScope(Dispatchers.IO).launch {
            val firebaseDb = MyGroceryApp.instance.firebaseFirestore
            firebaseDb.collection("AdminMasterDetails")
                .document("bcI5ARwAoHMLLQGdIXlHILEnlZ63")
                .collection("ProductList")
                .get().addOnSuccessListener { documentSnapshot ->
                    allProductList =
                        documentSnapshot.toObjects(ProductModel::class.java) as ArrayList<ProductModel>
                }.addOnFailureListener {
                    Toast.makeText(this@SearchActivity, it.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }


    private fun performClickEvent() {
        val animationSlideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in)
        val animationSlideOut = AnimationUtils.loadAnimation(this, R.anim.slide_out)

        binding.searchBar.isIconifiedByDefault = true
        binding.searchBar.isIconified = false
        binding.searchBar.requestFocusFromTouch()

        performSearch()


        searchAdapter.onItemClickListener { productModel, i ->
            var isPriceSelected = false
            var selectedPrice: PricePerKgModel? = null
            binding.searchBar.setQuery(productModel.name, true)
            //open Bottom sheet dialog
            val bottomSheetDialog = BottomSheetDialog(this, R.style.TransparentDialog)
            val view =
                LayoutInflater.from(this).inflate(R.layout.single_product_layout, null)
            bottomSheetDialog.setContentView(view)
            bottomSheetDialog.setCancelable(false)
            bottomSheetDialog.setCanceledOnTouchOutside(true)
            val textProductName: TextView = view.findViewById(R.id.productName)
            val productImage: ImageView = view.findViewById(R.id.productImage)
            val recyclerView: RecyclerView = view.findViewById(R.id.recyclerPrice)
            val btnAdd: ImageButton = view.findViewById(R.id.btnAdd)
            val btnRemove: ImageButton = view.findViewById(R.id.btnRemove)
            val btnAddToCart: Button = view.findViewById(R.id.btnAddToCart)
            val txtQuantity: TextView = view.findViewById(R.id.txtQuantity)
            val txtPriceDescription: TextView = view.findViewById(R.id.txtPriceDescription)
            val progressBar: ProgressBar = view.findViewById(R.id.progressBar)

            Glide.with(productImage.context).load(productModel.imageUrl).centerCrop()
                .placeholder(R.drawable.image).into(productImage)

            textProductName.text = productModel.name
            recyclerView.adapter = priceAdapter
            priceAdapter.checkedPosition = -1
            priceAdapter.differ.submitList(productModel.priceList)


            priceAdapter.onItemClickListener { pricePerKgModel, i ->
                txtPriceDescription.visibility = View.VISIBLE
                isPriceSelected = true
                selectedPrice = pricePerKgModel
                if (pricePerKgModel.gram == "Unit") {
                    txtPriceDescription.text = "Per Unit Price"
                } else {
                    txtPriceDescription.text = "Quantity ${pricePerKgModel.gram} Price"
                }
            }

            btnAdd.setOnClickListener {
                if (txtQuantity.text.toString().toInt() <= 9) {
                    txtQuantity.text = (txtQuantity.text.toString().toInt() + 1).toString()
                    btnAddToCart.isEnabled = true
                    btnRemove.isEnabled = true
                    btnAddToCart.setBackgroundColor(
                        ContextCompat.getColor(
                            this,
                            R.color.md_green_500
                        )
                    )
                    btnRemove.setBackgroundResource(R.drawable.red_circle)
                } else {
                    Toast.makeText(
                        this,
                        "Maximum Item per order is 10",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            btnRemove.setOnClickListener {
                if (txtQuantity.text.toString().toInt() > 0) {
                    if (txtQuantity.text.toString().toInt() == 1) {
                        btnRemove.isEnabled = false
                        btnAddToCart.isEnabled = false
                        btnRemove.setBackgroundResource(R.drawable.grey_circle)
                        btnAddToCart.setBackgroundColor(
                            ContextCompat.getColor(
                                this,
                                R.color.md_grey_400
                            )
                        )
                    }
                    txtQuantity.text = (txtQuantity.text.toString().toInt() - 1).toString()
                }
            }

            btnAddToCart.setOnClickListener {
                progressBar.visibility = View.GONE
                var cartId: String? = null
                if (isPriceSelected && txtQuantity.text.toString().toInt() != 0) {
                    //add to cart
                    selectedPrice?.let { pricePerKgModel ->
                        cartId = productModel.productID + "_${pricePerKgModel.id}"
                        val cartItemModel = CartItemModel(
                            productModel.productID + "_${pricePerKgModel.id}",
                            txtQuantity.text.toString().toInt(),
                            (txtQuantity.text.toString()
                                .toInt() * pricePerKgModel.price!!.replace(" Rs", "")
                                .toInt()).toString(),
                            productModel,
                            pricePerKgModel
                        )
                        progressBar.visibility = View.VISIBLE
                        btnAddToCart.isEnabled = false
                        btnAddToCart.setBackgroundColor(
                            ContextCompat.getColor(
                                this,
                                R.color.md_grey_500
                            )
                        )
                        val db = MyGroceryApp.instance.firebaseFirestore
                        val currentUser = MyGroceryApp.instance.firebaseAuth.currentUser!!.uid
                        db.collection("UserMasterDetails")
                            .document(currentUser)
                            .collection("CartList")
                            .document(productModel.productID + "_${pricePerKgModel.id}")
                            .set(cartItemModel)
                            .addOnSuccessListener {

                                CoroutineScope(Dispatchers.IO).launch {
                                    val cartListModified = arrayListOf<CartItemModel>()
//                                    for(index in 0..cartList.size.minus(1)){
//                                        val cartItemModelNew = cartList[index]
//                                        if (cartItemModelNew.id == cartId){
//                                            cartList.removeAt(index)
//                                            cartList.add(index,cartItemModel)
//                                        }else{
//                                            cartListModified.add(cartItemModelNew)
//                                        }
//                                    }
                                    withContext(Dispatchers.Main) {
                                        EventBus.getDefault().post(MessageEvent(cartList.size))
                                    }
                                }
//                                binding.txtCartCount.text = HomeFragment.cartList.size.toString()
                                progressBar.visibility = View.GONE
                                btnAddToCart.isEnabled = true
                                bottomSheetDialog.dismiss()

                                Snackbar.make(
                                    binding.recyclerRecentSearch,
                                    "Item added to Cart",
                                    Snackbar.LENGTH_SHORT
                                )
                                    .setAction("Go to cart") {
                                        finish()
                                    }
                                    .show()

                            }.addOnFailureListener {
                                progressBar.visibility = View.GONE
                                btnAddToCart.isEnabled = true
                                btnAddToCart.setBackgroundColor(
                                    ContextCompat.getColor(
                                        this,
                                        R.color.md_green_500
                                    )
                                )
                                Toast.makeText(
                                    this,
                                    "Unable to add product to cart",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                } else {
                    Toast.makeText(this, "Please select one price", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            bottomSheetDialog.show()
        }

    }

    private fun performSearch() {

        val onQueryTextListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if (p0 != null) {
                    renderList(true, p0)
                }
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                if (binding.searchBar.query.isNotEmpty()) {
                    if (p0 != null) {
                        renderList(true, p0)
                    }
                }
                return false
            }

        }
        binding.searchBar.setOnQueryTextListener(onQueryTextListener)
    }

    private fun renderList(isRender: Boolean, searchText: String) {
        if (isRender) {
            mHandler.removeCallbacksAndMessages(null)
            mHandler.postDelayed({
                CoroutineScope(Dispatchers.Main).launch {
                    filterSearch(searchText)
                }
            }, 500)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private suspend fun filterSearch(searchText: String) {
        withContext(Dispatchers.Main) {
            checkString(searchText.trim()) {
                MainScope().launch(Dispatchers.Main) {
                    if (it) {
                        searchAdapter.differ.submitList(searchFilterList)
                        searchAdapter.notifyDataSetChanged()
                        binding.recyclerRecentSearch.visibility = View.VISIBLE
                        binding.imgNotFound.visibility = View.GONE
                    } else {
                        searchAdapter.differ.submitList(searchFilterList)
                        binding.imgNotFound.visibility = View.VISIBLE
                        binding.recyclerRecentSearch.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun checkString(searchedName: String, callBack: (Boolean) -> Unit) {
        MainScope().launch(Dispatchers.IO) {
            searchFilterList.clear()
            for (value in allProductList) {
                if (value.name?.contains(searchedName, ignoreCase = true) == true ||
                    value.brandName?.contains(searchedName, ignoreCase = true) == true ||
                    value.categoryName?.contains(searchedName, ignoreCase = true) == true
                ) {
                    searchFilterList.add(value)
                }
            }
            if (searchFilterList.size > 0) {
                callBack(true)
            } else {
                callBack(false)
            }
        }
    }


    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.push_out_to_bottom)
    }

    private var searchFilterList = arrayListOf<ProductModel>()
}