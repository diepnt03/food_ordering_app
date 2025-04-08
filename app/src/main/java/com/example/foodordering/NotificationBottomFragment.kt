package com.example.foodordering

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodordering.adapter.NotificationAdapter
import com.example.foodordering.databinding.FragmentNotifactionBottomBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class NotificationBottomFragment : BottomSheetDialogFragment() {
    private lateinit var binding : FragmentNotifactionBottomBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotifactionBottomBinding.inflate(layoutInflater,container,false)
        val notification = listOf("Your order has been Canceled Successfully","Order has been taken by the driver","Congrats Your Order Placed")
        val notificationImge = listOf(R.drawable.ic_sademoji,R.drawable.ic_driver,R.drawable.ic_done)
        val adapter = NotificationAdapter(
            ArrayList(notification),
            ArrayList(notificationImge)
        )
        binding.listNotify.layoutManager = LinearLayoutManager(requireContext())
        binding.listNotify.adapter = adapter
        return binding.root
    }

}