package com.example.foodordering.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.foodordering.databinding.FragmentProfileBinding
import com.example.foodordering.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        setUserData()

        binding.btnSaveInfo.setOnClickListener {
            val name = binding.edtName.text.toString()
            val phone = binding.edtPhone.text.toString()
            val email = binding.edtEmail.text.toString()
            val address = binding.edtAddress.text.toString()

            updateUSerData(name, phone, email, address)
        }

        return binding.root
    }

    private fun updateUSerData(name: String, phone: String, email: String, address: String) {
        val userId = auth.currentUser?.uid
        userId?.let {
            val userReference = database.getReference("user").child(userId)
            val userData = hashMapOf(
                "name" to name,
                "phone" to phone,
                "email" to email,
                "address" to address,
            )
            userReference.setValue(userData).addOnSuccessListener {
                Toast.makeText(requireContext(), "Profile update successfully", Toast.LENGTH_SHORT)
                    .show()
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Profile update failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUserData() {
        val userId = auth.currentUser?.uid
        userId?.let {
            val userReference = database.getReference("user").child(userId)
            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userProfile = snapshot.getValue(User::class.java)
                        userProfile?.let {
                            binding.edtName.setText(userProfile.userName)
                            binding.edtPhone.setText(userProfile.phone)
                            binding.edtEmail.setText(userProfile.email)
                            binding.edtAddress.setText(userProfile.address)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("TAG", "onCancelled: ${error.message}")
                }
            })
        }

    }

}