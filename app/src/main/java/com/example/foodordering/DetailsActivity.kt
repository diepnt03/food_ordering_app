package com.example.foodordering

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.foodordering.databinding.ActivityDetailsBinding
import com.example.foodordering.model.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val foodName = intent.getStringExtra("MenuItemName")
        val foodImage = intent.getStringExtra("MenuItemImage")
        val foodDescription = intent.getStringExtra("MenuItemDescription")
        val foodIngredient = intent.getStringExtra("MenuItemIngredient")
        val foodPrice = intent.getStringExtra("MenuItemPrice")


        binding.tvFoodName.text = foodName
        binding.tvDescription.text = foodDescription
        binding.tvIngredients.text = foodIngredient
        Glide.with(this).load(Uri.parse(foodImage)).into(binding.imgFood)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnAddToChart.setOnClickListener {
            addItemToCart(foodName, foodPrice, foodImage, foodDescription)
        }

    }

    private fun addItemToCart(
        foodName: String?,
        foodPrice: String?,
        foodImage: String?,
        foodDescription: String?
    ) {
        val database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid ?: ""

        val cartItem = CartItem(
            foodName,
            foodPrice,
            foodImage,
            foodDescription,
            1
        )
        database.child("user").child(userId).child("CartItems").push().setValue(cartItem)
            .addOnSuccessListener {
                Toast.makeText(this, "Item added into cart successfully", Toast.LENGTH_SHORT)
                    .show()
            }.addOnFailureListener {
                Toast.makeText(this, "Item added into cart fail", Toast.LENGTH_SHORT)
                    .show()
            }
    }

}