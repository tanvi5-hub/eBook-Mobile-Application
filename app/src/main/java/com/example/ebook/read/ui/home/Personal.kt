package com.example.ebook.read.ui.home


import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ebook.read.model.StoriesViewModel
import com.example.ebook.read.model.UserViewModel
import com.example.ebook.read.ui.search.SearchButton
import com.example.ebook.read.ui.theme.ReaderTheme
@Composable


fun MainScreen(navController: NavHostController, userViewModel: UserViewModel = viewModel(), storiesViewModel: StoriesViewModel = viewModel()) {
    val bookIds by userViewModel.getBooklist().observeAsState(listOf())

    LaunchedEffect(bookIds) {
        if (bookIds.isNotEmpty()) {
            Log.d("MainScreen", " book IDs available")
            Log.d("BookListScreen", "Current bookIds: $bookIds")
        } else {
            Log.d("MainScreen", "No book IDs available")
        }
    }

    Scaffold(
        topBar = {
            SearchButton(navController)
        },
        bottomBar = {
            HomeBottomAppBarExample(navController)
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).verticalScroll(rememberScrollState())) {
            if (bookIds.isEmpty()) {
                Text("Loading books...")
            } else {
                Log.d("MainScreen", "Displaying ${bookIds.size} books")
                bookIds.forEach { book ->
                    Log.d("MainScreen", "Rendering book: ${book.bookid}")

                    SearchResult(
                        navController = navController,
                        bookName = book.name,
                        bookDescription = book.pagetext,
                        imageUrl = book.coverUrl,
                        onClick = { navController.navigate("Book/${book.bookid}") }
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}
@Preview
@Composable
fun MainPreview() {
    ReaderTheme {
        // 确保传递一个有效的NavHostController实例
        val navController = rememberNavController()
        MainScreen(navController)
    }
}