package com.example.ebook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.ebook.read.ui.navigation.AppNavigation
import com.example.ebook.read.ui.theme.ReaderTheme
import com.example.ebook.read.ui.theme.AppTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.example.ebook.read.model.UserViewModel
class ReaderHomeActivity : AppCompatActivity() {

    private val userViewModel: UserViewModel by viewModels()

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

// 获取当前登录的用户信息

        setContent {

                Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
                    AppNavigation()
                }

        }
    }
}
