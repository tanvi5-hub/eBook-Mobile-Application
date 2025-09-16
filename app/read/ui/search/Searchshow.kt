package com.example.ebook.read.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavController

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ebook.read.model.BookViewModel

@Composable
fun CustomListItem(bookName: String, onClick: () -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable(onClick = onClick)
        .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = bookName, style = MaterialTheme.typography.subtitle1)
    }
}
@Composable
fun SearchPage(navController: NavController, viewModel: BookViewModel = viewModel()) {
    val books by viewModel.books.collectAsState(initial = emptyList())
    val authors by viewModel.authors.collectAsState(initial = emptyList())
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        topBar = {
            TopAppBar(
                backgroundColor = Color.White,
                elevation = 0.dp,
                modifier = Modifier.height(100.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    RoundedSearchInputBox(viewModel)  // Pass viewModel here
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            Text("Authors", style = MaterialTheme.typography.h6, modifier = Modifier.padding(8.dp))
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(authors) { author ->
                    CustomListItem(bookName = author.name, onClick = {
                        navController.navigate("writerDetails/${author.userId}")
                    })
                    Divider()
                }
            }
            Text("Books", style = MaterialTheme.typography.h6, modifier = Modifier.padding(8.dp))
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(books) { book ->
                    CustomListItem(bookName = book.name, onClick = {
                        navController.navigate("bookDetails/${book.bookId}")
                    })
                    Divider()
                }
            }

        }
    }
}


    @Composable

    fun RoundedSearchInputBox(viewModel: BookViewModel) {
        val searchText = remember { mutableStateOf("") }

        OutlinedTextField(
            value = searchText.value,
            onValueChange = { searchText.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp),
            leadingIcon = {
                Icon(
                    Icons.Filled.Search,
                    contentDescription = "Search",
                    tint = Color.Gray
                )
            },
            label = { Text("Search") },
            singleLine = true,
            shape = RoundedCornerShape(50),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                viewModel.searchBooksAndAuthors(searchText.value)
            })
        )
    }

@Preview
@Composable
fun SearchPagePreview() {
    val navController = rememberNavController()
    SearchPage(navController)
}