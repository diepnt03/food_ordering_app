package com.example.foodordering.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodordering.DetailsActivity
import com.example.foodordering.databinding.PopularItemBinding
import com.example.foodordering.model.MenuItem

class PopularAdapter(
    private val menuList : ArrayList<MenuItem>,
    private val requireContext: Context
) : RecyclerView.Adapter<PopularAdapter.PopularViewHolder>() {
    inner class PopularViewHolder(private val binding: PopularItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String?, price: String?, image: String?) {
            Glide.with(requireContext).load(Uri.parse(image)).into(binding.imgPopular)
            binding.tvFoodName.text = item
            binding.tvPrice.text = price
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        return PopularViewHolder(
            PopularItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
        holder.bind(menuList[position].foodName,menuList[position].foodPrice,menuList[position].foodImage,)
        holder.itemView.setOnClickListener {
            val intent = Intent(requireContext, DetailsActivity::class.java)
            intent.putExtra("MenuItemName", menuList[position].foodName)
            intent.putExtra("MenuItemImage", menuList[position].foodImage)
            intent.putExtra("MenuItemDescription", menuList[position].foodDescription)
            intent.putExtra("MenuItemIngredient", menuList[position].foodIngredient)
            intent.putExtra("MenuItemPrice", menuList[position].foodPrice)
            requireContext.startActivity(intent)
        }
    }
}