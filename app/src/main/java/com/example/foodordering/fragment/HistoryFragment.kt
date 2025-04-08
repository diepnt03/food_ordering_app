package com.example.foodordering.fragment

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.foodordering.R
import com.example.foodordering.adapter.BuyAgainAdapter
import com.example.foodordering.adapter.CartAdapter
import com.example.foodordering.databinding.FragmentHistoryBinding
import com.example.foodordering.model.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HistoryFragment : Fragment() {
    private lateinit var binding : FragmentHistoryBinding
    private lateinit var database : FirebaseDatabase
    private lateinit var auth : FirebaseAuth
    private lateinit var userID : String
    private var listOfOrderItem = mutableListOf<OrderDetails>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(layoutInflater,container,false)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        retrieveHistory()
        binding.recentBuyItem.setOnClickListener {
            seeItemRecentBuy()
        }

        binding.btnReceived.setOnClickListener{
            updateOrderStatus()
        }

        return binding.root
    }

    private fun updateOrderStatus() {
        val itemPushKey = listOfOrderItem[0].itemPushKey
        val completeOrderReference = database.reference.child("CompletedOrder").child(itemPushKey!!)
        completeOrderReference.child("paymentReceived").setValue(true)
    }

    private fun seeItemRecentBuy() {
        TODO("Not yet implemented")
    }

    private fun retrieveHistory() {
        binding.recentBuyItem.visibility = View.VISIBLE
        userID = auth.currentUser?.uid?:""
        val buyItemReference = database.reference.child("user").child(userID).child("BuyHistory")
        val shortingQuery = buyItemReference.orderByChild("currentTime")
        shortingQuery.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(buySnapshot in snapshot.children){
                    val buyHistoryItem = buySnapshot.getValue(OrderDetails::class.java)
                    buyHistoryItem?.let{
                        listOfOrderItem.add(it)
                    }
                }
                listOfOrderItem.reverse()
                if(listOfOrderItem.isNotEmpty()){
                    setDataInRecentBuyItem()
                    setPreviousBuyItemsRecycleView()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun setDataInRecentBuyItem() {
        binding.recentBuyItem.visibility = View.VISIBLE
        val recentOrderItem = listOfOrderItem.firstOrNull()
        recentOrderItem?.let{
            with(binding){
                tvFoodName.text = it.foodNames?.firstOrNull()?:""
                tvPrice.text = it.foodPrices?.firstOrNull()?:""
                val image = it.foodImages?.firstOrNull()?:""
                val uri = Uri.parse(image)
                Glide.with(requireContext()).load(uri).into(imgRecent)
            }
        }
    }

    private fun setPreviousBuyItemsRecycleView() {
        val againFoodName = mutableListOf<String>()
        val againItemPrice = mutableListOf<String>()
        val againImage = mutableListOf<String>()
        for(i in 0 until listOfOrderItem.size){
            listOfOrderItem[i].foodNames?.firstOrNull()?.let {
                againFoodName.add(it)
            }
            listOfOrderItem[i].foodPrices?.firstOrNull()?.let {
                againItemPrice.add(it)
            }
            listOfOrderItem[i].foodImages?.firstOrNull()?.let {
                againImage.add(it)
            }
        }
        binding.listPreviousBuy.layoutManager = LinearLayoutManager(requireContext())
        val adapter = BuyAgainAdapter(againFoodName,againItemPrice,againImage,requireContext())
        binding.listPreviousBuy.adapter = adapter
    }

}