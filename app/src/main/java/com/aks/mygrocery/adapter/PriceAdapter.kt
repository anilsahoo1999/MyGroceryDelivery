package com.aks.mygrocery.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.aks.mygrocery.R
import com.aks.mygrocery.models.PricePerKgModel

class PriceAdapter : RecyclerView.Adapter<PriceAdapter.PriceViewHolder>() {

    var checkedPosition = -1
    private val difUtilCallBack = object : DiffUtil.ItemCallback<PricePerKgModel>(){
        override fun areItemsTheSame(oldItem: PricePerKgModel, newItem: PricePerKgModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: PricePerKgModel,
            newItem: PricePerKgModel
        ): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,difUtilCallBack)

    var onItemClickCallBack : ((PricePerKgModel, Int)->Unit)?=null

    fun onItemClickListener(callBack : ((PricePerKgModel,Int)->Unit)){
        this.onItemClickCallBack = callBack
    }

    inner class PriceViewHolder(private val itemView: View):RecyclerView.ViewHolder(itemView){
        private val txtPrice : TextView = itemView.findViewById(R.id.txtPrice)
        val relativeLayout : RelativeLayout = itemView.findViewById(R.id.relativeLayout)
        fun bind(pricePerKgModel: PricePerKgModel, position: Int){
            txtPrice.text = pricePerKgModel.price
            itemView.setOnClickListener {
                relativeLayout.setBackgroundResource(R.drawable.rectangular_border_outside)
                if(checkedPosition != position){
                    notifyItemChanged(checkedPosition)
                    checkedPosition = position
                }

                onItemClickCallBack?.invoke(pricePerKgModel,position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PriceViewHolder {
        return PriceViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_price_view,parent,false))
    }

    override fun onBindViewHolder(holder: PriceViewHolder, position: Int) {
        holder.bind(differ.currentList[position],position)
        if (checkedPosition == position){
            holder.relativeLayout.setBackgroundResource(R.drawable.rectangular_border_outside)
        }else{
            holder.relativeLayout.setBackgroundResource(0)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}