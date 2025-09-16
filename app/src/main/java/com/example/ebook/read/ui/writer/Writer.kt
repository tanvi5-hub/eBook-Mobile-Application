package com.example.ebook.read.ui.writer
import android.annotation.SuppressLint
import android.util.Log
import com.example.ebook.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardReturn
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.ebook.read.model.UserViewModel
import androidx.compose.material3.TextButton
import androidx.compose.ui.platform.LocalContext
import com.example.ebook.read.model.Book

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable

fun AuthorDetailsPage(authorId: String, navController: NavHostController, viewModel: UserViewModel = viewModel()) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Writer Introduction") },
                navigationIcon = {
                    IconButton(onClick = {
                        // Handle back navigation
                        navController.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.KeyboardReturn,
                            contentDescription = "Go back", // Localized description for accessibility
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) {
        AuthorContent(authorId, navController, viewModel)
    }
}

@Composable
fun AuthorContent(authorId: String, navController: NavHostController,viewModel: UserViewModel) {
    val context = LocalContext.current

    val books by viewModel.getwriterbooklist(authorId).observeAsState()
    val authorDetails by viewModel.getwriterlist(authorId).observeAsState()
    if (books != null) {
        if (books!!.isNotEmpty()) {
            Log.d("MainScreen", " book IDs available")
            Log.d("BookListScreen1", "Current bookIds: $books")
        } else {
            Log.d("UserViewModel3", "No book IDs available")
        }

    }
    else{
        Log.d("UserViewModel4", "No book IDs available")
    }
    authorDetails?.let {
        val imageUrl = it.imageUrl
        // 你可以在这里使用 imageUrl，例如加载和显示图像
    }
    val isFollowing by viewModel.isFollowing.observeAsState(false)

    // 监听关注状态
    LaunchedEffect(authorId) {
        viewModel.watchFollowingAuthor(authorId)
        viewModel.getwriterbooklist(authorId)
        viewModel.getwriterlist(authorId)
    }
    Log.d("Following", "$isFollowing")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        authorDetails?.let {
            val imageUrl = it.imageUrl
            AsyncImage(
                model = imageUrl,
                contentDescription = "作者头像",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(150.dp)
                    // 设置外部容器大小，这将是圆形的直径
                    .clip(CircleShape)
            )
            // 你可以在这里使用 imageUrl，例如加载和显示图像
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            authorDetails?.let {
                val name = it.name
                Text("$name", style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    viewModel.toggleFollowingAuthor(authorId, isFollowing)
                }) {
                    Text(if (isFollowing) "Following" else "Follow")
                }
            }

        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Book list", style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.height(8.dp))
        AuthorWorks(authorId, navController, viewModel,books)
    }
}
@Composable
fun AuthorWorks(authorId: String, navController: NavHostController, viewModel: UserViewModel,books: List<Book>?) {

    // 使用 LazyColumn 和 items 函数展示书籍列表
    LazyColumn {
        books?.let { bookList ->
            items(bookList) { book ->
                TextButton(
                    onClick = { navController.navigate("bookDetails/${book.bookid}") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        AsyncImage(
                            model = book.coverUrl,
                            contentDescription = "作品封面",
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(){
                            Text(
                                book.name,
                                textAlign = TextAlign.Left,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                book.description,
                                textAlign = TextAlign.Left,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                    }
                }
                Divider(color = Color.Black, thickness = 1.dp)
            }
        } ?: item {
            Text("No books available")
        }
    }
}



