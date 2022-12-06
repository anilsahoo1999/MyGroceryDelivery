package com.aks.mygrocery.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.aks.mygrocery.R
import com.aks.mygrocery.adapter.PriceAdapter
import com.aks.mygrocery.adapter.ProductAdapter
import com.aks.mygrocery.app.MyGroceryApp
import com.aks.mygrocery.base.BaseActivity
import com.aks.mygrocery.base.BaseFragment
import com.aks.mygrocery.databinding.FragmentSeeAllProductBinding
import com.aks.mygrocery.models.CartItemModel
import com.aks.mygrocery.models.PricePerKgModel
import com.aks.mygrocery.models.ProductModel
import com.aks.mygrocery.utils.Constants
import com.aks.mygrocery.utils.Constants.cartList
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SeeAllProductFragment : BaseFragment<FragmentSeeAllProductBinding>() {

    private var categoryId: String? = null
    private var categoryName: String? = null
    private lateinit var productAdapter: ProductAdapter
    private var productList: ArrayList<ProductModel>? = null
    private lateinit var priceAdapter: PriceAdapter

    override fun getFragmentId(): Int {
        return R.layout.fragment_see_all_product
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).binding.bottomNavView.visibility = View.GONE

        categoryId = requireArguments().getString("CATEGORY_ID")
        categoryName = requireArguments().getString("CATEGORY_NAME")

        categoryName?.let {
            binding.title.text = categoryName
        }
        productAdapter = ProductAdapter()
        priceAdapter = PriceAdapter()
        binding.recyclerProduct.adapter = productAdapter


        if (categoryId != null) {
            categoryId?.let {
                getProductByCategoryId(it)
            }
        } else {
            binding.productShimmer.stopShimmer()
            binding.productShimmer.visibility = View.GONE
            Toast.makeText(requireContext(), "Not Found", Toast.LENGTH_SHORT).show()
        }

        binding.imageBack.setOnClickListener {
            findNavController().popBackStack()
        }

        productAdapter.onItemClickListener { productModel, i ->
            //open price list bottom sheet dialog
            var isPriceSelected = false
            var selectedPrice: PricePerKgModel? = null
            val bottomSheetDialog = BottomSheetDialog(requireActivity(), R.style.TransparentDialog)
            val view =
                LayoutInflater.from(requireContext()).inflate(R.layout.layout_add_to_cart, null)

            bottomSheetDialog.setContentView(view)
            bottomSheetDialog.setCancelable(false)
            bottomSheetDialog.setCanceledOnTouchOutside(true)
            val textProductName: TextView = view.findViewById(R.id.txtProductName)
            val recyclerView: RecyclerView = view.findViewById(R.id.recyclerPrice)
            val btnAdd: ImageButton = view.findViewById(R.id.btnAdd)
            val btnRemove: ImageButton = view.findViewById(R.id.btnRemove)
            val btnAddToCart: Button = view.findViewById(R.id.btnAddToCart)
            val txtQuantity: TextView = view.findViewById(R.id.txtQuantity)
            val txtPriceDescription: TextView = view.findViewById(R.id.txtPriceDescription)
            val progressBar: ProgressBar = view.findViewById(R.id.progressBar)

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
                            requireContext(),
                            R.color.md_green_500
                        )
                    )
                    btnRemove.setBackgroundResource(R.drawable.red_circle)
                } else {
                    Toast.makeText(
                        requireContext(),
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
                                requireContext(),
                                R.color.md_grey_400
                            )
                        )
                    }
                    txtQuantity.text = (txtQuantity.text.toString().toInt() - 1).toString()
                }
            }

            btnAddToCart.setOnClickListener {
                progressBar.visibility = View.GONE
                if (isPriceSelected && txtQuantity.text.toString().toInt() != 0) {
                    //add to cart
                    selectedPrice?.let { pricePerKgModel ->
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
                                requireContext(),
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
                                cartList.add(cartItemModel)
//                                binding.txtCartCount.text = HomeFragment.cartList.size.toString()
                                progressBar.visibility = View.GONE
                                btnAddToCart.isEnabled = true
                                bottomSheetDialog.dismiss()

                                Snackbar.make(
                                    binding.recyclerProduct,
                                    "Item added to Cart",
                                    Snackbar.LENGTH_SHORT
                                )
                                    .setAction("Go to cart") {
                                        findNavController().navigate(R.id.action_seeAllProductFragment_to_cartFragment)
                                    }
                                    .show()

                            }.addOnFailureListener {
                                progressBar.visibility = View.GONE
                                btnAddToCart.isEnabled = true
                                btnAddToCart.setBackgroundColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.md_green_500
                                    )
                                )
                                Toast.makeText(
                                    requireContext(),
                                    "Unable to add product to cart",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                } else {
                    Toast.makeText(requireContext(), "Please select one price", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            bottomSheetDialog.show()
        }

    }

    private fun getProductByCategoryId(categoryId: String) {
        try {
            binding.productShimmer.startShimmer()
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                val firebaseDb = MyGroceryApp.instance.firebaseFirestore
                firebaseDb.collection("AdminMasterDetails")
                    .document(Constants.adminId)
                    .collection("ProductList")
                    .whereEqualTo("categoryID", categoryId)
                    .get().addOnSuccessListener { documentSnapshot ->
                        CoroutineScope(Dispatchers.IO).launch {
                            productList = arrayListOf()
                            productList =
                                documentSnapshot.toObjects(ProductModel::class.java) as ArrayList<ProductModel> /* = java.util.ArrayList<com.aks.mygrocery.models.ProductModel> */
                            withContext(Dispatchers.Main) {
                                if (productList?.size!! > 0) {
                                    binding.productShimmer.stopShimmer()
                                    productAdapter.differ.submitList(productList)
                                    binding.productShimmer.visibility = View.GONE
                                    binding.recyclerProduct.visibility = View.VISIBLE
                                } else {
                                    binding.productShimmer.stopShimmer()
                                    binding.productShimmer.visibility = View.GONE
                                    binding.imageNotFound.visibility = View.VISIBLE
                                }
                            }
                        }
                    }.addOnFailureListener {
                        binding.productShimmer.stopShimmer()
                        binding.productShimmer.visibility = View.GONE
                        binding.imageNotFound.visibility = View.VISIBLE
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}