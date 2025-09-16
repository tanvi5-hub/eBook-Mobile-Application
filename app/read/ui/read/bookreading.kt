package com.example.ebook.read.ui.read

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.unit.dp
import com.example.ebook.read.model.BookViewModel

import com.example.ebook.read.model.StoriesViewModel
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.BottomAppBar
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ebook.read.ui.components.TopAppBar
import com.google.accompanist.flowlayout.FlowRow
@Composable

fun StoryScreen(bookId: String, navController: NavHostController, storiesViewModel: StoriesViewModel = viewModel()) {
    val bookDetail by storiesViewModel.bookDetail.observeAsState()
    val currentPage by storiesViewModel.currentPage.observeAsState()
    val completePath by storiesViewModel.completePath.observeAsState()
    // 在 StoryScreen 组合函数中观察 completePath 状态
    val currentCompletePath by storiesViewModel.completePath.observeAsState()
    val showComponents = remember { mutableStateOf(false) }
// 在 StoryScreen 组合函数中使用 remember 或 rememberSaveable 来保留 completePath 状态

// 或者

    LaunchedEffect(bookId) {
        try {
            storiesViewModel.loadBook(bookId)


        } catch (e: Exception) {
            Log.e("BookScreen", "Error loading book", e)
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .clickable { showComponents.value = !showComponents.value }) {

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {
            Text(text = bookDetail?.name ?: "Loading book...", style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = currentPage?.text ?: "Loading page...", style = MaterialTheme.typography.body1)
            Spacer(modifier = Modifier.weight(1f))
            if (currentPage?.choices.isNullOrEmpty()) {

                Text(text = completePath ?: " ", style = MaterialTheme.typography.body2)
                Button(onClick = { storiesViewModel.loadFirstPage(bookId) }) {
                    Text("Back to First Page")
                }
            } else {
                // 使用 FlowRow 来实现按钮自动换行
                FlowRow(
                    mainAxisSpacing = 8.dp, // 水平间距
                    crossAxisSpacing = 8.dp // 垂直间距
                ) {
                    currentPage?.choices?.forEach { (choiceText, nextPageId) ->
                        Button(onClick = {
                            storiesViewModel.loadPage(bookId, nextPageId)
                            storiesViewModel.updateReadingPath(bookId, nextPageId, choiceText)
                            storiesViewModel.dateReadingPath(bookId, nextPageId)
                        }) {
                            Text(choiceText)
                        }
                    }
                }
            }
        }

        if (showComponents.value) {
            TopAppBar {
                Clickback(
                    onBackClick = { navController.popBackStack() },
                    onDetailsClick = { navController.navigate("bookDetails/${bookId}")  }
                )
            }
        }
    }
}