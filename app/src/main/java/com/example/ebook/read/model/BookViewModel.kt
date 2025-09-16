package com.example.ebook.read.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.StateFlow

class BookViewModel : ViewModel() {
    private val databaseReference = FirebaseDatabase.getInstance()
    private val _books = MutableStateFlow<List<Booklist>>(emptyList())
    val books: StateFlow<List<Booklist>> = _books.asStateFlow()

    private val _authors = MutableStateFlow<List<Author>>(emptyList())

    val authors: StateFlow<List<Author>> = _authors.asStateFlow()


    fun moveBook(fromIndex: Int, toIndex: Int) {
        val currentBooks = _books.value.toMutableList()
        val book = currentBooks.removeAt(fromIndex)
        currentBooks.add(toIndex, book)
        _books.value = currentBooks
    }

    fun moveAuthor(fromIndex: Int, toIndex: Int) {
        val currentAuthors = _authors.value.toMutableList()
        val author = currentAuthors.removeAt(fromIndex)
        currentAuthors.add(toIndex, author)
        _authors.value = currentAuthors
    }

    fun searchBooksAndAuthors(query: String) {
        Log.d("BooksViewModel", "Book loaded: Name=$query")
        // 搜索书籍
        viewModelScope.launch {
            val booksRef = databaseReference.getReference("stories")
            booksRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val filteredBooks = mutableListOf<Booklist>()
                    snapshot.children.forEach { bookSnapshot ->
                        // 直接从每个 bookid 下读取 name
                        val bookId = bookSnapshot.key ?: ""
                        val bookName = bookSnapshot.child("name").getValue(String::class.java) ?: ""
                        Log.d("BooksViewModel", "Book loaded: Name=$bookName")
                        if (bookName.contains(query, ignoreCase = true)) {
                            Log.d("BooksViewModel", "Book matches query: Name=$bookName")
                            filteredBooks.add(Booklist(bookName,bookId))
                        }
                    }
                    _books.value = filteredBooks
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("BooksViewModel", "Error loading books: ${error.message}")
                }
            })
        }

        // 搜索作者
        viewModelScope.launch {
            val usersRef = databaseReference.getReference("user")
            usersRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val filteredAuthors = mutableListOf<Author>()
                    snapshot.children.forEach { userSnapshot ->
                        val userId = userSnapshot.key ?: ""
                        val userType = userSnapshot.child("user_type").getValue(String::class.java)
                        if (userType == "Author") {
                            val authorName = userSnapshot.child("name").getValue(String::class.java) ?: ""
                            if (authorName.contains(query, ignoreCase = true)) {
                                filteredAuthors.add(Author(authorName,userId))
                            }
                        }
                    }
                    _authors.value = filteredAuthors
                }

                override fun onCancelled(error: DatabaseError) {
                    // 处理可能的错误
                }
            })
        }
    }

}

data class Booklist(val name: String, val bookId: String)
data class Author(val name: String, val userId: String)