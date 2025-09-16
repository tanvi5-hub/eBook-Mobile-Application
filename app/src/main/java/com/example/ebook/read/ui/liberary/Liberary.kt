package com.example.ebook.read.ui.liberary

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ebook.read.model.StoriesViewModel
import com.example.ebook.read.model.UserViewModel
import com.example.ebook.read.ui.home.HomeBottomAppBarExample
import com.example.ebook.read.ui.home.SearchResult
import com.example.ebook.read.ui.search.SearchButton
import com.example.ebook.read.ui.theme.ReaderTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(navController: NavHostController, userViewModel: UserViewModel = viewModel(), storiesViewModel: StoriesViewModel = viewModel()) {
    val bookDetails by storiesViewModel.filteredStories.collectAsState()
    Scaffold(
        topBar = {
            Column {
                SearchButton(navController)
                FilterButton(navController,storiesViewModel)
            }
                 // 确保你有一个SearchButton组件的定义
        },
        bottomBar = {
            HomeBottomAppBarExample(navController) // 使用外部传入的navController
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).verticalScroll(rememberScrollState())) {
            if (bookDetails.isEmpty()) {
                Text("No book")
            } else {
                Log.d("MainScreen", "Displaying ${bookDetails.size} books")
                bookDetails.forEach { book ->
                    Log.d("MainScreen", "Rendering book: ${book.id}")
                    SearchResult(
                        navController = navController,
                        bookName = book.name,
                        bookDescription = book.description,
                        imageUrl = book.coverUrl,
                        onClick = { navController.navigate("bookDetails/${book.id}") }
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}



@Preview
@Composable
fun PreviewSearchResult() {
    ReaderTheme {
        // 确保传递一个有效的NavHostController实例
        val navController = rememberNavController()
        LibraryScreen(navController)
    }

}