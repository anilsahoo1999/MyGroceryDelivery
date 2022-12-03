package com.aks.mygrocery.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.aks.mygrocery.R
import com.aks.mygrocery.models.CartItemModel
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton

class SavedForLaterAdapter : RecyclerView.Adapter<SavedForLaterAdapter.CartViewHolder>() {


    private var callBackAdd : ((CartItemModel, Int)->Unit)?=null
    fun onBtnAddClickListener(callBack : ((CartItemModel,Int)->Unit)?=null){
        this.callBackAdd = callBack
    }

    private var callBackRemove : ((CartItemModel, Int)->Unit)?=null
    fun onBtnRemoveClickListener(callBack : ((CartItemModel,Int)->Unit)?=null){
        this.callBackRemove = callBack
    }

    private var callBackDelete : ((CartItemModel, Int)->Unit)?=null
    fun onBtnDeleteClickListener(callBack : ((CartItemModel,Int)->Unit)?=null){
        this.callBackDelete = callBack
    }

    private var callBackAddToCart : ((CartItemModel, Int)->Unit)?=null
    fun onBtnAddToCartClick(callBack : ((CartItemModel,Int)->Unit)?=null){
        this.callBackAddToCart = callBack
    }

    private var callBackError : ((String)->Unit)?=null
    fun onErrorMessageListener (callBackError : ((String)->Unit)?=null){
        this.callBackError = callBackError
    }

    inner class CartViewHolder(private val itemView : View):RecyclerView.ViewHolder(itemView){
        private val productImage : ImageView = itemView.findViewById(R.id.productImage)
        private val productName : TextView = itemView.findViewById(R.id.productName)
        private val productPrice : TextView = itemView.findViewById(R.id.productPrice)
        private val btnAdd : ImageButton = itemView.findViewById(R.id.btnAdd)
        private val btnRemove : ImageButton = itemView.findViewById(R.id.btnRemove)
        private val btnAddToCart : MaterialButton = itemView.findViewById(R.id.btnMoveToCart)
        private val btnDeleteProduct : MaterialButton = itemView.findViewById(R.id.btnRemoveProduct)
        private val txtQuantity : TextView = itemView.findViewById(R.id.txtQuantity)

        fun bind(cartItemModel: CartItemModel,position: Int){
            Glide.with(productImage.context).load(cartItemModel.productModel?.imageUrl)
                .centerCrop().placeholder(R.drawable.image).into(productImage)
            productName.text = cartItemModel.productModel?.name
            productPrice.text = "\u20B9 ${cartItemModel.totalPrice}"
            txtQuantity.text = cartItemModel.quantity.toString()
            btnAdd.setOnClickListener {
                cartItemModel.selectedPrice.let {
                    if (cartItemModel.quantity!! < 10) {
                        callBackAdd?.invoke(cartItemModel, position)
                    }else{
                        callBackError?.invoke("You can't add more than 10 item")
                    }
                }
            }

            btnRemove.setOnClickListener {
                cartItemModel.selectedPrice.let {
                    if (cartItemModel.quantity!! == 1){
                        callBackDelete?.invoke(cartItemModel, position)
                    }else{
                        callBackRemove?.invoke(cartItemModel, position)
                    }
                }
            }

            btnDeleteProduct.setOnClickListener {
                cartItemModel.selectedPrice.let {
                    callBackDelete?.invoke(cartItemModel, position)
                }
            }

            btnAddToCart.setOnClickListener {
                cartItemModel.let {
                    callBackAddToCart?.invoke(cartItemModel,position)
                }
            }
        }
    }

    private val  difUtils = object : DiffUtil.ItemCallback<CartItemModel>(){
        override fun areItemsTheSame(oldItem: CartItemModel, newItem: CartItemModel): Boolean {
            return  oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CartItemModel, newItem: CartItemModel): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this,difUtils)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        return CartViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cart_item_saved_later,parent,false))
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(differ.currentList[position],position)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}