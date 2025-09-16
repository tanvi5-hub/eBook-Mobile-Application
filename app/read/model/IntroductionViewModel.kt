package com.example.ebook.read.model

import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import android.annotation.SuppressLint
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
class BioViewModel : ViewModel() {
    private val database = Firebase.database
    private val userRef = database.getReference("user")

    fun updateUserBio(userId: String, bio: String) {
        userRef.child(userId).child("bio").setValue(bio)
    }

    fun getUserBio(userId: String, onSuccess: (String) -> Unit) {
        userRef.child(userId).child("bio").get().addOnSuccessListener {
            val bio = it.value as String
            onSuccess(bio)
        }
    }
}