package com.aks.mygrocery.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.aks.mygrocery.R
import com.aks.mygrocery.adapter.CartAdapter
import com.aks.mygrocery.adapter.SavedForLaterAdapter
import com.aks.mygrocery.app.MyGroceryApp
import com.aks.mygrocery.base.BaseActivity
import com.aks.mygrocery.base.BaseFragment
import com.aks.mygrocery.databinding.FragmentCartBinding
import com.aks.mygrocery.models.CartItemModel
import com.aks.mygrocery.utils.Dialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartFragment : BaseFragment<FragmentCartBinding>() {

    private lateinit var cartAdapter: CartAdapter
    private lateinit var savedForLaterAdapter: SavedForLaterAdapter
    private var totalCartPrice : Int?=0
    override fun getFragmentId(): Int {
        return R.layout.fragment_cart
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as BaseActivity).binding.bottomNavView.visibility = View.GONE

        initializeObject()

        getCartItems()
        getSavedForLaterItems()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initializeObject() {
        cartAdapter = CartAdapter()
        binding.recyclerCart.adapter = cartAdapter

        savedForLaterAdapter = SavedForLaterAdapter()
        binding.recyclerSaveForLater.adapter = savedForLaterAdapter


        cartAdapter.onBtnAddClickListener { cartItemModel, i ->
            modifyCartItemAddRemove(i, cartItemModel,"Add")
        }

        cartAdapter.onErrorMessageListener {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }

        cartAdapter.onBtnRemoveClickListener { cartItemModel, i ->
            modifyCartItemAddRemove(i, cartItemModel,"Remove")
        }

        cartAdapter.onBtnDeleteClickListener { cartItemModel, i ->
            Dialog.messageDialogDoubleAction(
                true,
                requireActivity(),
                "Hey Folks!",
                "What do you want?",
                R.drawable.ic_cancel_close_svgrepo_com,
                false,
                "Delete",
                "Saved For Later"
            ){isClicked,whatClicked->
                if (isClicked){
                    when(whatClicked){
                        "Positive"->{
                            cartItemList.removeAt(i)
                            cartAdapter.differ.submitList(cartItemList)
                            cartAdapter.notifyDataSetChanged()
                            deleteCartItem(cartItemModel)
                            calculateCartValue()

                            if (cartItemList.size == 0){
                                binding.linear1.visibility = View.VISIBLE
                                binding.frameLayout.visibility = View.GONE
                                binding.txtTotalPrice.visibility = View.GONE
                            }else{
                                binding.linear1.visibility = View.GONE
                                binding.frameLayout.visibility = View.VISIBLE
                                binding.txtTotalPrice.visibility = View.GONE
                            }
                        }
                        "Negative"->{
                            //saved for later
                            cartItemList.removeAt(i)
                            cartAdapter.differ.submitList(cartItemList)
                            cartAdapter.notifyDataSetChanged()


                            savedForLaterList.add(cartItemModel)
                            savedForLaterAdapter.differ.submitList(savedForLaterList)
                            savedForLaterAdapter.notifyDataSetChanged()

                            if (cartItemList.size == 0){
                                binding.linear1.visibility = View.VISIBLE
                                binding.frameLayout.visibility = View.GONE
                                binding.txtTotalPrice.visibility = View.GONE
                            }else{
                                binding.linear1.visibility = View.GONE
                                binding.frameLayout.visibility = View.VISIBLE
                                binding.txtTotalPrice.visibility = View.VISIBLE
                            }

                            if (savedForLaterList.size == 0){
                                binding.linear2.visibility = View.GONE
                                binding.cardView2.visibility = View.GONE
                            }else{
                                binding.linear2.visibility = View.VISIBLE
                                binding.cardView2.visibility = View.VISIBLE
                            }

                            calculateSavedForLaterItemCount()
                            calculateCartValue()
                            deleteCartItem(cartItemModel)
                            savedForLater(cartItemModel)
                        }
                    }
                }

            }
        }

        savedForLaterAdapter.onBtnDeleteClickListener { cartItemModel, i ->
            modifySavedForLaterItem(i, cartItemModel)
        }

        savedForLaterAdapter.onBtnAddToCartClick { cartItemModel, i ->
            modifySavedForLaterItem(i, cartItemModel)



            cartItemList.add(cartItemModel)
            cartAdapter.differ.submitList(cartItemList)
            cartAdapter.notifyDataSetChanged()
            updateCartItem(cartItemModel)
            calculateCartValue()
        }

    }

    private fun modifyCartItemAddRemove(
        i: Int,
        cartItemModel: CartItemModel,
        type : String
    ) {
        val changedCartItemModel = cartItemList[i]
        when(type){
            "Add"->{
                changedCartItemModel.quantity = cartItemModel.quantity?.plus(1)
                changedCartItemModel.totalPrice = ((cartItemModel.selectedPrice?.price?.replace(
                    " Rs",
                    ""
                ))!!.toInt() * changedCartItemModel.quantity!!).toString()
            }
            "Remove"->{
                changedCartItemModel.quantity = cartItemModel.quantity?.minus(1)
                changedCartItemModel.totalPrice = ((cartItemModel.selectedPrice?.price?.replace(
                    " Rs",
                    ""
                ))!!.toInt() * changedCartItemModel.quantity!!).toString()
            }
        }
        cartItemList.removeAt(i)
        cartItemList.add(i, changedCartItemModel)
        cartAdapter.differ.submitList(cartItemList)
        cartAdapter.notifyItemChanged(i)
        updateCartItem(changedCartItemModel)
        calculateCartValue()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun modifySavedForLaterItem(
        i: Int,
        cartItemModel: CartItemModel
    ) {
        if (cartItemList.size == 0){
            binding.linear1.visibility = View.VISIBLE
            binding.frameLayout.visibility = View.GONE
            binding.txtTotalPrice.visibility = View.GONE
        }else{
            binding.linear1.visibility = View.GONE
            binding.frameLayout.visibility = View.VISIBLE
            binding.txtTotalPrice.visibility = View.VISIBLE
        }

        savedForLaterList.removeAt(i)
        savedForLaterAdapter.differ.submitList(savedForLaterList)
        savedForLaterAdapter.notifyDataSetChanged()
        deleteSavedForLaterItem(cartItemModel)
        calculateSavedForLaterItemCount()
    }

    private fun calculateCartValue(){
        totalCartPrice = 0
        for (i in cartItemList){
            totalCartPrice = totalCartPrice?.plus((i.totalPrice)?.toInt()!!)
        }
        binding.txtTotalPrice.text = "\u20B9 ${totalCartPrice.toString()}"
    }

    private fun calculateSavedForLaterItemCount(){
        binding.txtSavedForLaterCount.text = "(${savedForLaterList.size})"
    }

    private fun updateCartItem(cartItemModel: CartItemModel) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val firebaseDb = MyGroceryApp.instance.firebaseFirestore
                val currentUser = MyGroceryApp.instance.firebaseAuth.currentUser!!.uid
                CoroutineScope(Dispatchers.IO).launch {
                    cartItemModel.id?.let {
                        firebaseDb.collection("UserMasterDetails")
                            .document(currentUser).collection("CartList")
                            .document(it)
                            .set(cartItemModel).addOnSuccessListener {
                                Log.e("TAG", "updateCartItem: Updated successfully")
                            }.addOnFailureListener {
                                Log.e("TAG", "updateCartItem: Failed")
                            }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun deleteCartItem(cartItemModel: CartItemModel) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val firebaseDb = MyGroceryApp.instance.firebaseFirestore
                val currentUser = MyGroceryApp.instance.firebaseAuth.currentUser!!.uid
                CoroutineScope(Dispatchers.IO).launch {
                    cartItemModel.id?.let {
                        firebaseDb.collection("UserMasterDetails")
                            .document(currentUser).collection("CartList")
                            .document(it)
                            .delete().addOnSuccessListener {
                                Log.e("TAG", "updateCartItem: Updated successfully")
                            }.addOnFailureListener {
                                Log.e("TAG", "updateCartItem: Failed")
                            }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun deleteSavedForLaterItem(cartItemModel: CartItemModel) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val firebaseDb = MyGroceryApp.instance.firebaseFirestore
                val currentUser = MyGroceryApp.instance.firebaseAuth.currentUser!!.uid
                CoroutineScope(Dispatchers.IO).launch {
                    cartItemModel.id?.let {
                        firebaseDb.collection("UserMasterDetails")
                            .document(currentUser).collection("SavedForLaterList")
                            .document(it)
                            .delete().addOnSuccessListener {
                                Log.e("TAG", "updateCartItem: Updated successfully")
                            }.addOnFailureListener {
                                Log.e("TAG", "updateCartItem: Failed")
                            }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getCartItems() {
        binding.cartItemLoader.startShimmer()
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val firebaseDb = MyGroceryApp.instance.firebaseFirestore
                val currentUser = MyGroceryApp.instance.firebaseAuth.currentUser!!.uid

                firebaseDb.collection("UserMasterDetails")
                    .document(currentUser).collection("CartList")
                    .get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot != null && !documentSnapshot.isEmpty) {
                            CoroutineScope(Dispatchers.IO).launch {
                                cartItemList =
                                    documentSnapshot.toObjects(CartItemModel::class.java) as ArrayList<CartItemModel> /* = java.util.ArrayList<com.aks.mygrocery.models.CartItemModel> */
                                withContext(Dispatchers.Main) {
                                    binding.cartItemLoader.stopShimmer()
                                    if (cartItemList.size != 0) {
                                        calculateCartValue()
                                        binding.cartItemLoader.visibility = View.GONE
                                        binding.recyclerCart.visibility = View.VISIBLE
                                        binding.linear1.visibility = View.GONE
                                        binding.frameLayout.visibility = View.VISIBLE
                                        binding.txtTotalPrice.visibility = View.VISIBLE
                                        cartAdapter.differ.submitList(cartItemList)
                                    } else {
                                        binding.cartItemLoader.visibility = View.GONE
                                        binding.linear1.visibility = View.VISIBLE
                                        binding.frameLayout.visibility = View.GONE
                                    }
                                }
                            }
                        }else{
                            binding.cartItemLoader.stopShimmer()
                            binding.cartItemLoader.visibility = View.GONE
                            binding.linear1.visibility = View.VISIBLE
                            binding.frameLayout.visibility = View.GONE
                        }
                    }
                    .addOnFailureListener { e: Exception? ->
                        Toast.makeText(
                            requireContext(),
                            "An error has occurred while fetching the data from our server",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to get Cart items", Toast.LENGTH_SHORT)
                    .show()
            }


        }
    }

    private fun getSavedForLaterItems(){
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val firebaseDb = MyGroceryApp.instance.firebaseFirestore
                val currentUser = MyGroceryApp.instance.firebaseAuth.currentUser!!.uid

                firebaseDb.collection("UserMasterDetails")
                    .document(currentUser).collection("SavedForLaterList")
                    .get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot != null && !documentSnapshot.isEmpty) {
                            CoroutineScope(Dispatchers.IO).launch {
                                savedForLaterList =
                                    documentSnapshot.toObjects(CartItemModel::class.java) as ArrayList<CartItemModel> /* = java.util.ArrayList<com.aks.mygrocery.models.CartItemModel> */
                                withContext(Dispatchers.Main) {
                                    if (savedForLaterList.size != 0) {
                                        calculateSavedForLaterItemCount()
                                        binding.linear2.visibility = View.VISIBLE
                                        binding.cardView2.visibility = View.VISIBLE
                                        savedForLaterAdapter.differ.submitList(savedForLaterList)
                                    } else {
                                        binding.linear2.visibility = View.GONE
                                        binding.cardView2.visibility = View.GONE
                                    }
                                }
                            }
                        }else{
                            binding.linear2.visibility = View.GONE
                            binding.cardView2.visibility = View.GONE
                        }
                    }
                    .addOnFailureListener { e: Exception? ->
                        Toast.makeText(
                            requireContext(),
                            "An error has occurred while fetching the data from our server",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to get Cart items", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun savedForLater(cartItemModel: CartItemModel) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val firebaseDb = MyGroceryApp.instance.firebaseFirestore
                val currentUser = MyGroceryApp.instance.firebaseAuth.currentUser!!.uid
                CoroutineScope(Dispatchers.IO).launch {
                    cartItemModel.id?.let {
                        firebaseDb.collection("UserMasterDetails")
                            .document(currentUser).collection("SavedForLaterList")
                            .document(it)
                            .set(cartItemModel).addOnSuccessListener {
                                Log.e("TAG", "updateCartItem: Updated successfully")
                            }.addOnFailureListener {
                                Log.e("TAG", "updateCartItem: Failed")
                            }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    companion object {
        var cartItemList = arrayListOf<CartItemModel>()
        var savedForLaterList = arrayListOf<CartItemModel>()
    }


}
