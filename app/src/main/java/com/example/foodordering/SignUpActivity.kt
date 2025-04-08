package com.example.foodordering

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.foodordering.databinding.ActivitySignBinding
import com.example.foodordering.model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var userName: String
    private lateinit var database: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient

    private val binding: ActivitySignBinding by lazy { ActivitySignBinding.inflate(layoutInflater) }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                if (task.isSuccessful) {
                    val account = task.result
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    auth.signInWithCredential(credential).addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            Toast.makeText(
                                this,
                                "Successful sign in with google",
                                Toast.LENGTH_SHORT
                            ).show()
                            updateUI()
                        } else {
                            Toast.makeText(this, "Google sign in fail", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Google sign in fail", Toast.LENGTH_SHORT).show()
                }
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = Firebase.auth
        database = Firebase.database.reference

        val googleSignInOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("669789534298-nh0ku9t18b7u067nvnnfqk3vpugc3d7e.apps.googleusercontent.com")   //type = 3
            .requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOption)

        binding.tvAlreadyHaveAcc.setOnClickListener{
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
        binding.btnCreate.setOnClickListener{
            userName = binding.edtName.text.toString().trim()
            email = binding.edtEmail.text.toString().trim()
            password = binding.edtPassword.text.toString().trim()

            if (userName.isBlank() || email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show()
            } else {
                createAccount(email, password)
            }

//            val intent = Intent(this, ChooseLocationActivity::class.java)
//            startActivity(intent)
        }

        binding.btnGoogle.setOnClickListener {
            val signIntent = googleSignInClient.signInIntent
            Log.e("TAG", "aaa onCreate: signIntent $signIntent")
            launcher.launch(signIntent)
        }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Account create successfully", Toast.LENGTH_SHORT).show()
                saveUserData()
                updateUI()
            } else {
                Toast.makeText(this, "Account create fail", Toast.LENGTH_SHORT).show()
                Log.d("TAG", "createAccount: fail  ${it.exception}")
            }
        }
    }

    private fun saveUserData() {
        val user = User(userName, email, password)
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            database.child("user").child(userId).setValue(user)
        }
    }

    private fun updateUI() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}