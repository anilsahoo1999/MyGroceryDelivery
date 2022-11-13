package com.aks.mygrocery.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.aks.mygrocery.R
import com.aks.mygrocery.models.BannerModel
import com.bumptech.glide.Glide

class BannerAdapter(private var bannerList: ArrayList<BannerModel>) :
    RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    inner class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageBanner)
        fun bind(bannerModel: BannerModel) {
            Glide.with(imageView.context).load(bannerModel.imageUrl).into(imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        return BannerViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.banner_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.bind(bannerList[position])
    }

    override fun getItemCount(): Int {
        return bannerList.size
    }
}