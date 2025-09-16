package com.example.ebook.read.ui.bestwriter



import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ebook.read.model.UserViewModel
import com.example.ebook.read.ui.theme.ReaderTheme


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