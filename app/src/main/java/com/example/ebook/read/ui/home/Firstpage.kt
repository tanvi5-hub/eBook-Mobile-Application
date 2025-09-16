package com.example.ebook.read.ui.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.Modifier.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ebook.R
import androidx.compose.foundation.border

import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color


import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.material.TextButton
import androidx.compose.ui.text.font.FontWeight
import com.example.ebook.read.ui.personal.ImageButton
import com.example.ebook.read.model.SearchViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter

// [START android_compose_layout_basics_1]

@Composable

fun SearchResult(
    navController: NavController,
    bookName: String,
    bookDescription: String,
    imageUrl: String,
    onClick: () -> Unit
) {
    Log.d("SearchResult", "SearchResult is called")
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)

    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Book Image",
            modifier = Modifier.size(80.dp)

        )
        Spacer(Modifier.width(16.dp))

        Column {
            Text(
                bookName,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                bookDescription,
                fontSize = 16.sp
            )
        }
    }
}

