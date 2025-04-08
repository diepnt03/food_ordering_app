package com.example.foodordering.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.foodordering.databinding.ItemCartBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartAdapter(
    private val context : Context,
    private val cartItems: MutableList<String>,
    private val cartItemPrice: MutableList<String>,
    private val cartImage: MutableList<String>,
    private val cartDescription: MutableList<String>,
    private val foodIngredient: MutableList<String>,
    private val cartQuantity: MutableList<Int>,
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {
    private val auth = FirebaseAuth.getInstance()

    companion object{
        private var itemQualities = intArrayOf()
        private lateinit var cartItemReference: DatabaseReference
    }

    init{
        val database = FirebaseDatabase.getInstance()
        val userId = auth.currentUser?.uid?:""
        var cartItemNumber = cartItems.size
        itemQualities = IntArray(cartItemNumber){1}
        cartItemReference = database.reference.child("user").child(userId).child("CartItems")
    }

    inner class CartViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                val quality = itemQualities[position]
                cartQuantity[position] = itemQualities[position]
                tvFoodName.text = cartItems[position]
                tvPrice.text = cartItemPrice[position]
                Glide.with(context).load(Uri.parse(cartImage[position])).listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.d("TAG", "onLoadFailed:  Image loading failed")
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.d("TAG", "onResourceReady:  Image loading success")
                        return false
                    }

                }).into(imgPopular)
                tvQuantity.text = quality.toString()

                btnMinus.setOnClickListener {
                    deceaseQuantity(position)
                }
                btnPlus.setOnClickListener {
                    increaseQuantity(position)
                }
                btnDelete.setOnClickListener {
                    val itemPosition = adapterPosition
                    if (itemPosition != RecyclerView.NO_POSITION) {
                        deleteItem(itemPosition)
                    }
                }
            }
        }

        private fun deceaseQuantity(position: Int) {
            if (itemQualities[position] > 1) {
                itemQualities[position]--
                cartQuantity[position] = itemQualities[position]
                binding.tvQuantity.text = itemQualities[position].toString()
            }
        }

        private fun increaseQuantity(position: Int) {
            if (itemQualities[position] < 10) {
                itemQualities[position]++
                cartQuantity[position] = itemQualities[position]
                binding.tvQuantity.text = itemQualities[position].toString()
            }
        }

        private fun deleteItem(position: Int) {
            getUniqueKeyAtPosition(position){
                it?.let{
                    removeItem(position,it)
                }
            }
        }

        private fun removeItem(position: Int, uniqueKey: String){
            cartItemReference.child(uniqueKey).removeValue().addOnSuccessListener {
                cartItems.removeAt(position)
                cartImage.removeAt(position)
                cartDescription.removeAt(position)
                cartQuantity.removeAt(position)
                cartItemPrice.removeAt(position)
                foodIngredient.removeAt(position)
                Toast.makeText(context,"Item deleted",Toast.LENGTH_SHORT).show()

                itemQualities = itemQualities.filterIndexed { index, i ->
                    index != position
                }.toIntArray()
                notifyItemRemoved(position)
                notifyItemRangeChanged(position,cartItems.size)
            }.addOnFailureListener {
                Toast.makeText(context,"Fail to delete",Toast.LENGTH_SHORT).show()
            }
        }

        private fun getUniqueKeyAtPosition(position: Int, onComplete : (String?) -> Unit){
            cartItemReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                   var uniqueKey : String? = null
                    snapshot.children.forEachIndexed { index, dataSnapshot ->
                        if(index == position){
                            uniqueKey = dataSnapshot.key
                            return@forEachIndexed
                        }
                    }
                    onComplete(uniqueKey)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("TAG", "onCancelled: ${error.message}")
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(position)
    }

    fun getUpdatedItemsQuantities(): MutableList<Int> {
        val itemQuantity = mutableListOf<Int>()
        itemQuantity.addAll(cartQuantity)
        return itemQuantity
    }

}