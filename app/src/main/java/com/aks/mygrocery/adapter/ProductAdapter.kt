package com.aks.mygrocery.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.aks.mygrocery.R
import com.aks.mygrocery.models.CategoryModel
import com.aks.mygrocery.models.ProductModel
import com.bumptech.glide.Glide

class ProductAdapter() : RecyclerView.Adapter<ProductAdapter.CategoryViewHolder>() {

    var callBackItemClick : ((ProductModel, Int)->Unit)?=null

    fun onItemClickListener(callBack : ((ProductModel,Int)->Unit)){
        callBackItemClick = callBack
    }
    private val diffUtilsCallBack = object : DiffUtil.ItemCallback<ProductModel>(){
        override fun areItemsTheSame(oldItem: ProductModel, newItem: ProductModel): Boolean {
            return oldItem.productID == newItem.productID
        }

        override fun areContentsTheSame(oldItem: ProductModel, newItem: ProductModel): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,diffUtilsCallBack)


    inner class CategoryViewHolder(itemView :View) : RecyclerView.ViewHolder(itemView){
        private val imageProduct :ImageView = itemView.findViewById(R.id.productImage)
        private val productName : TextView = itemView.findViewById(R.id.productName)
        private val productPrice : TextView = itemView.findViewById(R.id.productPrice)
        private val btnPlus : ImageButton = itemView.findViewById(R.id.btnPlus)
        private val productAvailability : TextView = itemView.findViewById(R.id.productAvailability)
        fun bind(productModel: ProductModel,position: Int){
            Glide.with(imageProduct.context).load(productModel.imageUrl).centerCrop().placeholder(R.drawable.image).into(imageProduct)
            productName.text = productModel.name

            if (productModel.isProductOutOfStock==true) {
                productAvailability.text = "Out of Stock"
                productAvailability.background = ContextCompat.getDrawable(productAvailability.context,R.drawable.background_rounded_red)
                productAvailability.setTextColor(ContextCompat.getColor(productAvailability.context,R.color.red))
                btnPlus.visibility = View.GONE
            }else{
                productAvailability.text = "In Stock"
                productAvailability.setTextColor(ContextCompat.getColor(productAvailability.context,R.color.green))
                productAvailability.background = ContextCompat.getDrawable(productAvailability.context,R.drawable.background_rounded_green)
            }

            if (productModel.priceList?.size!!>1){
                (productModel.priceList?.get(0)?.price+ "\n"+(productModel.priceList?.size!!-1)+" more").also { productPrice.text = it }
            }else{
                productPrice.text = productModel.priceList?.get(0)?.price
            }

            btnPlus.setOnClickListener {
                callBackItemClick?.invoke(productModel,position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.product_list_item,parent,false))
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(differ.currentList[position],position)
    }

    override fun getItemCount(): Int {
        return if (differ.currentList.size > 10) {
            10
        } else {
            differ.currentList.size
        }
    }
}