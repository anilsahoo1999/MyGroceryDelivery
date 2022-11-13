package com.aks.mygrocery.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.aks.mygrocery.R
import com.aks.mygrocery.models.CategoryModel
import com.bumptech.glide.Glide

class CategoryAdapter(private val list: List<CategoryModel>,private val colorList : List<Int>) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {


    inner class CategoryViewHolder(itemView :View) : RecyclerView.ViewHolder(itemView){
        private val imageCategory :ImageView = itemView.findViewById(R.id.categoryImage)
        private val categoryName : TextView = itemView.findViewById(R.id.txtCategoryName)
        private val cardView : CardView = itemView.findViewById(R.id.cardView)
        fun bind(categoryModel: CategoryModel,position: Int){
            Glide.with(imageCategory.context).load(categoryModel.imageUrl).centerCrop().placeholder(R.drawable.image).into(imageCategory)
            categoryName.text = categoryModel.name
            cardView.setCardBackgroundColor(ContextCompat.getColor(cardView.context,colorList[position]))

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.category_item_layout,parent,false))
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(list[position],position)
    }

    override fun getItemCount(): Int {
        return 4;
    }
}