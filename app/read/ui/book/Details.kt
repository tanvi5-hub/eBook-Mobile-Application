package com.example.ebook.read.ui.book

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardReturn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.ebook.R
import com.example.ebook.read.model.Book
import com.example.ebook.read.model.StoriesViewModel

import com.example.ebook.read.model.UserViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable

fun NovelDetailsPage(bookid: String, navController: NavHostController, viewModel: UserViewModel= viewModel(),storiesViewModel: StoriesViewModel = viewModel()) {
    val bookDetails by storiesViewModel.getBookDetails(bookid).observeAsState()
    val isFollowing by viewModel.isFollowing.observeAsState(false)
    LaunchedEffect(bookid) {
        viewModel.watchBooklist(bookid)

    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(bookDetails?.name ?: "Loading...") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Filled.KeyboardReturn,
                            contentDescription = "Return",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(backgroundColor = Color.LightGray) {
                Button(modifier = Modifier.weight(1f),onClick = {
                    viewModel.toggleBooklist(bookid, isFollowing)
                }) {
                    Text(if (isFollowing) "In bookstore" else "Add to bookstore")
                }
                Button(modifier = Modifier.weight(1f), onClick = { navController.navigate("Book/${bookid}")} ){
                    Text("Read it")
                }


            }
        }
    ) {
        NovelContent(book = bookDetails,navController)
    }
}

@Composable
fun NovelContent(book: Book?,navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = book?.coverUrl ?: "Loading...",
            contentDescription = "Book Image",
            modifier = Modifier.size(360.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(book?.name ?: "Loading...", style = MaterialTheme.typography.h5)
        book?.let { MediumBottomwritername(it.authorName,onClick = { navController.navigate("writerDetails/${book?.authorId}")}) }
        Spacer(modifier = Modifier.height(16.dp))
        Text(book?.description ?: "Loading...", textAlign = TextAlign.Justify)
        Spacer(modifier = Modifier.height(16.dp))
        GenreTag(genre = book?.category ?: "Loading...")
    }
}
@Composable
fun MediumBottomwritername(name: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth() // 使Row充满屏幕宽度
            .padding(0.dp) // 添加一些内边距
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally // 将Column中的内容水平居中
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.h5,
                fontSize = 32.sp // 明确指定字体大小
            )
        }
    }
}
@Composable
fun GenreTag(genre: String) {
    Surface(
        color = Color.White, // 设置外层框的背景颜色为浅灰色
        shape = RoundedCornerShape(4.dp), // 设置圆角
        modifier = Modifier
            .fillMaxWidth() // 确保宽度与页面宽度一致
            .border(1.dp, Color.Black, RoundedCornerShape(4.dp)) // 设置黑色边框
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = "Types of the novel:",
                color = Color.Black,
                modifier = Modifier.padding(bottom = 4.dp),
                style = MaterialTheme.typography.body2
            )

            // 使用 Box 来控制内部 Text 的边界
            Box(
                contentAlignment = Alignment.CenterStart, // 文本对齐方式
                modifier = Modifier
                    .wrapContentWidth() // 使包装框宽度与内容宽度一致
            ) {
                Surface(
                    color = Color.LightGray, // 设置内层框的背景颜色为黄色
                    shape = RoundedCornerShape(4.dp), // 设置圆角
                    modifier = Modifier
                        .border(1.dp, Color.Black, RoundedCornerShape(4.dp)) // 设置黑色边框
                ) {
                    Text(
                        text = genre,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.body2
                    )
                }
            }
        }
    }
}
