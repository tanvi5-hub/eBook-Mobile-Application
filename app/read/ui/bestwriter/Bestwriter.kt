package com.example.ebook.read.ui.bestwriter


import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.ebook.read.ui.home.MainScreen
import com.example.ebook.read.ui.read.TxtFileContent
import com.example.ebook.read.ui.theme.ReaderTheme
import java.lang.reflect.Modifier


import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.ui.modifier.modifierLocalConsumer


import androidx.compose.ui.unit.dp



import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold

import androidx.compose.ui.unit.dp

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.*

import com.example.ebook.read.ui.search.SearchButton
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text2.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.ebook.read.ui.home.HomeBottomAppBarExample
import com.example.ebook.read.ui.home.SearchResult
import com.example.ebook.read.ui.navigation.AppNavigation
import com.example.ebook.read.ui.personal.PersonScreen
import androidx.compose.foundation.layout.padding
import com.example.ebook.read.model.UserViewModel


@Composable
fun BestwriterScreen(navController: NavHostController, viewModel: UserViewModel = viewModel()) {
    val authorDetailsList by viewModel.getBestAuthorDetails().observeAsState(initial = emptyList())
    Log.e("UserViewModel", "Failed to read value", )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favourite Authors") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                backgroundColor = Color.White,
                contentColor = Color.Black,
                elevation = 12.dp
            )
        }
    ) { innerPadding ->
        Column(modifier = androidx.compose.ui.Modifier.padding(innerPadding)
            .verticalScroll(rememberScrollState())) {
            authorDetailsList.forEach { authorDetails ->
                Log.e("UserViewModel", "Failed to read value${authorDetails.authorId}", )

                BestwriterButton(navController, authorDetails,onClick = { navController.navigate("writerDetails/${authorDetails.authorId}") })


            }

        }
    }
}
@Preview
@Composable
fun VersionContent() {
    ReaderTheme {
        // 确保传递一个有效的NavHostController实例
        val navController = rememberNavController()
        BestwriterScreen(navController)
    }
}