package com.aks.mygrocery.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class CategoryAdapter(private val colorList : List<Int>) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {


    private var itemClickCallBack : ((CategoryModel, Int)->Unit)?=null

    fun onItemClickCallBackListener(callBack : ((CategoryModel,Int)->Unit)){
        itemClickCallBack = callBack
    }

    private val diffUtilsCallBack = object : DiffUtil.ItemCallback<CategoryModel>(){
        override fun areItemsTheSame(oldItem: CategoryModel, newItem: CategoryModel): Boolean {
            return oldItem.categoryID == newItem.categoryID
        }

        override fun areContentsTheSame(oldItem: CategoryModel, newItem: CategoryModel): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,diffUtilsCallBack)


    inner class CategoryViewHolder(itemView :View) : RecyclerView.ViewHolder(itemView){
        private val imageCategory :ImageView = itemView.findViewById(R.id.categoryImage)
        private val categoryName : TextView = itemView.findViewById(R.id.txtCategoryName)
        private val cardView : CardView = itemView.findViewById(R.id.cardView)
        fun bind(categoryModel: CategoryModel,position: Int){
            Glide.with(imageCategory.context).load(categoryModel.imageUrl).centerCrop().placeholder(R.drawable.image).into(imageCategory)
            categoryName.text = categoryModel.name
            cardView.setCardBackgroundColor(ContextCompat.getColor(cardView.context,colorList[position]))

            cardView.setOnClickListener {
                itemClickCallBack?.invoke(categoryModel,position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.category_item_layout,parent,false))
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(differ.currentList[position],position)
    }

    override fun getItemCount(): Int {
        return if(differ.currentList.size>4){
            10
        }else{
            differ.currentList.size
        }
    }
}