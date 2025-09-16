package com.example.ebook.read.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.ebook.read.ui.search.SearchButton


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.ebook.read.model.StoriesViewModel
import com.example.ebook.read.ui.theme.ReaderTheme


@Composable
fun FilterOptionScreen(navController: NavHostController,storiesViewModel: StoriesViewModel) {
    Scaffold(
        topBar = {
            Column {
                SearchButton(navController)
                com.example.ebook.read.ui.liberary.FilterButton(navController,storiesViewModel)
            }
        },
        content = { padding ->
            Column(modifier = Modifier.padding(padding)) {
                com.google.accompanist.flowlayout.FlowRow(
                    mainAxisSpacing = 16.dp,  // 主轴（水平）间距
                    crossAxisSpacing = 8.dp,  // 交叉轴（垂直）间距
                    modifier = Modifier
                        .background(Color.White).fillMaxWidth()

                ) {
                    FilterButton("Popular Books", "book", navController)
                    FilterButton("New Releases", "book", navController)

                    FilterButton("Free Books", "book", navController)
                }
            }
        }
    )
}
@Composable
fun FilterButton(filterName: String, route: String, navController: NavHostController) {
    Button(
        onClick = { navController.navigate(route) },
        modifier = Modifier.padding(horizontal = 0.dp)
    ) {
        Text(filterName)
    }
}
