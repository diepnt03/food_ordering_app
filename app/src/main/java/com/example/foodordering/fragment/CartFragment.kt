package com.example.foodordering.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodordering.PayOutActivity
import com.example.foodordering.adapter.CartAdapter
import com.example.foodordering.databinding.FragmentCartBinding
import com.example.foodordering.model.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var foodName: MutableList<String>
    private lateinit var foodPrice: MutableList<String>
    private lateinit var foodDescription: MutableList<String>
    private lateinit var foodImageUri: MutableList<String>
    private lateinit var foodIngredient: MutableList<String>
    private lateinit var quantity: MutableList<Int>
    private lateinit var userId: String

    private lateinit var adapter: CartAdapter
    private lateinit var binding: FragmentCartBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        retrieveCartItem()


        binding.btnProceed.setOnClickListener {
            getOrderItemsDetail()
        }

        return binding.root
    }

    private fun getOrderItemsDetail() {
        val orderIdReference = database.reference.child("user").child(userId).child("CartItems")
        val foodName = mutableListOf<String>()
        val foodPrice = mutableListOf<String>()
        val foodDescription = mutableListOf<String>()
        val foodImageUri = mutableListOf<String>()
        val foodIngredient = mutableListOf<String>()

        val foodQuantities = adapter.getUpdatedItemsQuantities()
        Log.e("TAG", "getOrderItemsDetail: foodQuantities $foodQuantities", )

        orderIdReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    val orderItem = foodSnapshot.getValue(CartItem::class.java)
                    orderItem?.let {
                        it.foodName?.let { it1 -> foodName.add(it1) }
                        it.foodPrice?.let { it1 -> foodPrice.add(it1) }
                        it.foodDescription?.let { it1 -> foodDescription.add(it1) }
                        it.foodImage?.let { it1 -> foodImageUri.add(it1) }
                        it.foodIngredient?.let { it1 -> foodIngredient.add(it1) }
                    }
                }
                orderNow(
                    foodName,
                    foodPrice,
                    foodDescription,
                    foodImageUri,
                    foodIngredient,
                    foodQuantities
                )
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG", "onCancelled: ${error.message}")
            }
        })
    }

    private fun orderNow(
        foodName: MutableList<String>,
        foodPrice: MutableList<String>,
        foodDescription: MutableList<String>,
        foodImageUri: MutableList<String>,
        foodIngredient: MutableList<String>,
        foodQuantities: MutableList<Int>
    ) {
        if(isAdded && context != null){
            val intent = Intent(requireContext(), PayOutActivity::class.java)
            intent.putExtra("FoodItemName", foodName as ArrayList<String>)
            intent.putExtra("FoodItemPrice", foodPrice as ArrayList<String>)
            intent.putExtra("FoodItemImage", foodImageUri as ArrayList<String>)
            intent.putExtra("FoodItemDescription", foodDescription as ArrayList<String>)
            intent.putExtra("FoodItemIngredient", foodIngredient as ArrayList<String>)
            intent.putExtra("FoodItemQuantities", foodQuantities as ArrayList<Int>)
            startActivity(intent)
        }
    }

    private fun retrieveCartItem() {
        database = FirebaseDatabase.getInstance()
        userId = auth.currentUser?.uid ?: ""

        val foodRef = database.reference.child("user").child(userId).child("CartItems")

        foodName = mutableListOf()
        foodPrice = mutableListOf()
        foodDescription = mutableListOf()
        foodImageUri = mutableListOf()
        foodIngredient = mutableListOf()
        quantity = mutableListOf()

        foodRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    val cartItem = foodSnapshot.getValue(CartItem::class.java)
                    cartItem?.let {
                        it.foodName?.let { it1 -> foodName.add(it1)
                            quantity.add(1)
                        }
                        it.foodPrice?.let { it1 -> foodPrice.add(it1) }
                        it.foodDescription?.let { it1 -> foodDescription.add(it1) }
                        it.foodImage?.let { it1 -> foodImageUri.add(it1) }
                        it.foodIngredient?.let { it1 -> foodIngredient.add(it1) }
                    }
                }
                setAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG", "onCancelled: ${error.message}")
            }
        })
    }

    private fun setAdapter() {
        adapter = CartAdapter(
            requireContext(),
            foodName,
            foodPrice,
            foodImageUri,
            foodDescription,
            foodIngredient,
            quantity
        )
        binding.rvCart.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCart.adapter = adapter
    }
}