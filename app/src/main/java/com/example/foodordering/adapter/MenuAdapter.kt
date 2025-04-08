package com.example.foodordering.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodordering.DetailsActivity
import com.example.foodordering.databinding.MenuItemBinding
import com.example.foodordering.model.MenuItem

class MenuAdapter(
    private val menuList : List<MenuItem>,
    private val requireContext: Context
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {
    private val itemClickListener: OnClickListener? = null

    inner class MenuViewHolder(private val binding: MenuItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener?.onItemClick(position)
                }
                val intent = Intent(requireContext, DetailsActivity::class.java)
                intent.putExtra("MenuItemName", menuList[position].foodName)
                intent.putExtra("MenuItemImage", menuList[position].foodImage)
                intent.putExtra("MenuItemDescription", menuList[position].foodDescription)
                intent.putExtra("MenuItemIngredient", menuList[position].foodIngredient)
                intent.putExtra("MenuItemPrice", menuList[position].foodPrice)
                requireContext.startActivity(intent)
            }
        }

        fun bind(position: Int) {
            binding.apply {
                tvFoodName.text = menuList[position].foodName
                tvPrice.text = menuList[position].foodPrice
                Glide.with(requireContext).load(Uri.parse(menuList[position].foodImage)).into(imgPopular)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        return holder.bind(position)
    }
    interface OnClickListener{
        fun onItemClick(position: Int)
    }
}
