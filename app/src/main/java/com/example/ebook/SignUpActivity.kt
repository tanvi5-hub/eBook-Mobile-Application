package com.example.ebook

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ebook.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        var userTypeName = ""
        val roleSpinner = findViewById<Spinner>(R.id.storyCategoryDropdown)
        val roles = arrayOf("Author", "Reader")
        val arrayAdapter = ArrayAdapter(this@SignUpActivity, android.R.layout.simple_spinner_dropdown_item, roles)
        roleSpinner.adapter = arrayAdapter
        binding.textView.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
        roleSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                userTypeName = roles[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val password = binding.passET.text.toString()
            val confirmPassword = binding.confirmPassEt.text.toString()
            val name = binding.nameEt.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password == confirmPassword) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val userId = firebaseAuth.currentUser?.uid
                            val userRef = database.reference.child("user").child(userId!!)
                            val userData = HashMap<String, Any>()
                            userData["email"] = email
                            userData["name"] = name
                            userData["password"] = password
                            userData["user_type"] = userTypeName
                            userData["bio"] = ""
                            userData["imageurl"] = ""
                            userRef.setValue(userData).addOnCompleteListener { dbTask ->
                                if(dbTask.isSuccessful) {
                                    Toast.makeText(this, "Registration Successful.", Toast.LENGTH_LONG).show()
                                    val intent = Intent(this, SignInActivity::class.java)
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(this, "Failed to save user data", Toast.LENGTH_LONG).show()
                                }
                            }
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}