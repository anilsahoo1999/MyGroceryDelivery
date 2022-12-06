package com.aks.mygrocery.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.aks.mygrocery.R
import com.aks.mygrocery.models.PricePerKgModel
import com.aks.mygrocery.models.ProductModel
import com.bumptech.glide.Glide

class SearchAdapter : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    private val difUtilCallBack = object : DiffUtil.ItemCallback<ProductModel>(){
        override fun areItemsTheSame(oldItem: ProductModel, newItem: ProductModel): Boolean {
            return oldItem.productID == newItem.productID
        }

        override fun areContentsTheSame(
            oldItem: ProductModel,
            newItem: ProductModel
        ): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,difUtilCallBack)

    var onItemClickCallBack : ((ProductModel, Int)->Unit)?=null

    fun onItemClickListener(callBack : ((ProductModel,Int)->Unit)){
        this.onItemClickCallBack = callBack
    }

    inner class SearchViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        private val txtPrice : TextView = itemView.findViewById(R.id.productName)
        private val productImage : ImageView = itemView.findViewById(R.id.imgSearch)
        fun bind(productModel: ProductModel, position: Int){
            txtPrice.text = productModel.name
            itemView.setOnClickListener {
                onItemClickCallBack?.invoke(productModel,position)
            }

            Glide.with(productImage.context).load(productModel.imageUrl).centerCrop().placeholder(R.drawable.image).into(productImage)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.search_item,parent,false))
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(differ.currentList[position],position)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}