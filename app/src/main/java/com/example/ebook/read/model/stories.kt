package com.example.ebook.read.model

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.values
import com.google.firebase.database.values
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StoriesViewModel : ViewModel() {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val storiesLiveData = MutableLiveData<List<Book>>()
    private val bookDetailsLiveData = MutableLiveData<Book>()
    private val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private val userId: String = currentUser?.uid ?: throw IllegalStateException("User not logged in")
    private val _stories = MutableStateFlow<List<Book>>(emptyList())
    val stories: StateFlow<List<Book>> = _stories.asStateFlow()

    private val _filter = MutableStateFlow("all")
    val filter: StateFlow<String> = _filter.asStateFlow()
    private val db = Firebase.database.reference
    private val auth = Firebase.auth



    private val _currentBook = MutableLiveData<Book>()
    val currentBook: LiveData<Book> = _currentBook

    private val _userId = MutableLiveData<String?>()
    val userid: LiveData<String?> = _userId
    init {
        observeStories()
        fetchStories()
        _userId.value = auth.currentUser?.uid
    }
    private fun fetchStories() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("stories")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val books = mutableListOf<Book>()
                snapshot.children.forEach { bookSnapshot ->
                    val book = bookSnapshot.getValue(Book::class.java)
                    book?.let { books.add(it) }
                }
                _stories.value = books
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("StoriesViewModel", "Failed to read value.", error.toException())
            }
        })
    }

    fun setFilter(filter: String) {
        _filter.value = filter
    }

    val filteredStories = filter.combine(_stories) { filter, stories ->
        if (filter == "all") {
            stories
        } else {
            stories.filter { it.category == filter }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    private fun observeStories() {
        val booksRef = database.getReference("stories")
        booksRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val books = mutableListOf<Book>()
                snapshot.children.forEach { childSnapshot ->
                    val book = childSnapshot.getValue(Book::class.java)?.apply {
                        bookid = childSnapshot.key ?: ""
                        Log.d("MainScreen1", "Rendering book: ${bookid}")
                    }
                    book?.let { books.add(it) }
                }
                storiesLiveData.postValue(books)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("StoriesViewModel", "Failed to read stories: ${error.message}")
            }
        })
    }
    fun getBookDetails(bookId: String): LiveData<Book> {
        viewModelScope.launch {
            fetchBookDetails(bookId)
        }
        return bookDetailsLiveData
    }

    @SuppressLint("NullSafeMutableLiveData")
    private suspend fun fetchBookDetails(bookId: String) = withContext(Dispatchers.IO) {
        val bookRef = database.getReference("stories").child(bookId)
        Log.d("StoriesViewModel", "Book loaded: ID=${bookId}")
        try {
            val snapshot = bookRef.get().await()
            val book = snapshot.getValue(Book::class.java)?.apply {
                bookid = snapshot.key ?: ""
            }
            book?.let {
                it.authorName = fetchAuthorName(it.authorId)?: "Unknown Author"
                bookDetailsLiveData.postValue(it)
                Log.d("StoriesViewModel", "Book loaded: ID=${it.bookid}, Author=${it.authorName}, Name=${it.name}")
            }
        } catch (e: Exception) {
            Log.e("StoriesViewModel", "Error fetching book details for ID $bookId: ${e.message}", e)
            bookDetailsLiveData.postValue(null)
        }
    }


    fun getStories(): LiveData<List<Book>> {
        viewModelScope.launch {
            fetchStoriesDetails()
        }
        return storiesLiveData
    }
    fun getBooks(bookIds: List<String>): LiveData<List<Book>> {
        viewModelScope.launch {
            fetchBooksDetails(bookIds)
        }
        return storiesLiveData
    }
   //这里
    private suspend fun fetchStoriesDetails() = withContext(Dispatchers.IO) {
        val books = mutableListOf<Book>()
        val booksRef = database.getReference("stories")

        try {
            val snapshot = booksRef.get().await()
            snapshot.children.forEach { childSnapshot ->
                val book = childSnapshot.getValue(Book::class.java)?.apply {
                    bookid = childSnapshot.key ?: ""  // 确保bookid是从Firebase的键中设置的
                }
                if (book != null) {
                    books.add(book)
                    Log.d("StoriesViewModel", "Book loaded: ID=${book.bookid}, Name=${book.name}")
                } else {
                    Log.d("StoriesViewModel", "Failed to load book with ID: ${childSnapshot.key}")
                }
            }
            storiesLiveData.postValue(books)
        } catch (e: Exception) {
            Log.e("StoriesViewModel", "Error fetching books: ${e.message}", e)
            storiesLiveData.postValue(emptyList())
        }
    }
    private suspend fun fetchBooksDetails(storyIds: List<String>) = withContext(Dispatchers.IO) {
        val books = mutableListOf<Book>()
        val booksRef = database.getReference("stories")

        try {
            val snapshot = booksRef.get().await()
            val deferreds = snapshot.children.mapNotNull { childSnapshot ->
                async {
                    val book = childSnapshot.getValue(Book::class.java)?.apply {
                        bookid = childSnapshot.key ?: ""  // 从Firebase的键中设置bookid
                    }
                    // 只处理那些ID存在于storyIds列表中的书籍
                    if (book != null && storyIds.contains(book.bookid)) {
                        book.authorName = fetchAuthorName(book.authorId)!!
                        Log.d("BooksViewModel", "Book loaded: ID=${book.bookid}, Name=${book.name}")
                        book
                    } else {
                        null
                    }
                }
            }
            // 等待所有异步操作完成
            deferreds.forEach { deferred ->
                deferred.await()?.let { book ->
                    books.add(book)
                }
            }
            storiesLiveData.postValue(books)  // 更新LiveData，只包含查询到的书籍
        } catch (e: Exception) {
            Log.e("StoriesViewModel", "Error fetching books: ${e.message}", e)
            storiesLiveData.postValue(emptyList())  // 发生错误时，更新LiveData为一个空列表
        }
    }
    private suspend fun fetchAuthorName(authorId: String): String? = withContext(Dispatchers.IO) {
        Log.e("SureR", "No author name found for ID: $authorId")
        val authorRef = database.getReference("user").child(authorId).child("name")
        try {
            val snapshot = authorRef.get().await()

            val authorName = snapshot.value as? String
            if (authorName == null) {
                Log.e("StoriesViewModel", "No author name found for ID: $authorId")
            } else {
                Log.d("StoriesViewModel", "Fetched author name: $authorName")
            }
            return@withContext authorName
        } catch (e: Exception) {
            Log.e("StoriesViewModel", "Error fetching author name: ${e.message}", e)
            return@withContext null
        }
    }



    fun updateUserReadingPosition(userId: String, bookId: String, pageId: String) {
        val userPageRef = database.getReference("user/$userId/booklist/$bookId")
        userPageRef.setValue(pageId).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("ViewModel", "User reading position updated successfully.")
            } else {
                Log.e("ViewModel", "Failed to update user reading position.", it.exception)
            }
        }
    }
    @SuppressLint("NullSafeMutableLiveData")
    fun getUserReadingPosition(userId: String, bookId: String): LiveData<String> {
        val currentPageIdLiveData = MutableLiveData<String>()
        val userPageRef = database.getReference("user/$userId/booklist/$bookId")
        userPageRef.get().addOnSuccessListener { snapshot ->
            val currentPageId = snapshot.getValue(String::class.java)
            currentPageId?.let {
                currentPageIdLiveData.postValue(it)
            }
        }.addOnFailureListener {
            Log.e("ViewModel", "Failed to fetch user reading position.", it)
            currentPageIdLiveData.postValue(null)
        }
        return currentPageIdLiveData
    }
    fun updateUserReadingPosition(bookId: String, pageId: String) {
        if (userId == null) return  // 确保用户已登录

        val userPageRef = database.getReference("user/$userId/booklist/$bookId")
        userPageRef.setValue(pageId).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("ViewModel", "User reading position updated successfully.")
            } else {
                Log.e("ViewModel", "Failed to update user reading position.", it.exception)
            }
        }
    }
    @SuppressLint("NullSafeMutableLiveData")
    fun getUserReadingPosition(bookId: String): LiveData<String> {
        val currentPageIdLiveData = MutableLiveData<String>()
        if (userId == null) {
            currentPageIdLiveData.postValue(null)
            return currentPageIdLiveData  // 确保用户已登录
        }

        val userPageRef = database.getReference("user/$userId/booklist/$bookId")
        userPageRef.get().addOnSuccessListener { snapshot ->
            val currentPageId = snapshot.getValue(String::class.java)
            currentPageId?.let {
                currentPageIdLiveData.postValue(it)
            }
        }.addOnFailureListener {
            Log.e("ViewModel", "Failed to fetch user reading position.", it)
            currentPageIdLiveData.postValue(null)
        }
        return currentPageIdLiveData
    }




    private val _bookDetail = MutableLiveData<Book>()
    val bookDetail: LiveData<Book> = _bookDetail

    private val _currentPage = MutableLiveData<Page>()
    val currentPage: LiveData<Page> = _currentPage
    private val _completePath = MutableLiveData<String>()
    val completePath: LiveData<String> = _completePath
    @SuppressLint("NullSafeMutableLiveData")
    fun loadBook(bookId: String) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid
                if (userId == null) {
                    Log.e("ViewModel", "User not logged in")
                    return@launch
                }

                // 尝试获取阅读记录
                val readingPathRef = db.child("user").child(userId).child("booklist").child(bookId).child("record")
                val readingPathSnapshot = withContext(Dispatchers.IO) {
                    readingPathRef.get().await()
                }
                val lastReadPageId = readingPathSnapshot.getValue<String>()

                // 获取书籍详情
                val bookSnapshot = withContext(Dispatchers.IO) {
                    db.child("stories").child(bookId).get().await()
                }
                val book = bookSnapshot.getValue<Book>()
                if (book != null) {
                    _bookDetail.postValue(book)
                    _bookDetail.value = _bookDetail.value // 强制刷新
                    Log.d("ViewModel", "Book posted to LiveData: $book")

                    // 根据是否有阅读记录加载页面
                    if (lastReadPageId != null && book.pages.containsKey(lastReadPageId)) {
                        Log.d("MainScreen", "Resuming from page $lastReadPageId")
                        loadPage(bookId, lastReadPageId)
                    } else {
                        book.pages.values.firstOrNull { it.firstPage }?.let {
                            Log.d("MainScreen", "Displaying first page ${it.id}")
                            loadPage(bookId, it.id)
                        }
                    }
                } else {
                    Log.d("ViewModel", "Book not found")
                }
            } catch (e: Exception) {
                Log.e("StoryViewModel", "Failed to load book: ${e.message}")
            }
        }
    }

    @SuppressLint("NullSafeMutableLiveData")
    fun loadPage(bookId: String, pageId: String) {
        viewModelScope.launch {
            try {
                val snapshot = withContext(Dispatchers.IO) {
                    db.child("stories").child(bookId).child("pages").child(pageId).get().await()
                }
                val page = snapshot.getValue<Page>()
                _currentPage.postValue(page)
                Log.d("ViewModel1", "Page posted to LiveData: $page")
            } catch (e: Exception) {
                Log.e("StoryViewModel", "Failed to load page: ${e.message}")
            }
        }
    }

    fun dateReadingPath(bookId: String, pageId: String) {
        val userId = auth.currentUser?.uid ?: return
        val ref = db.child("user").child(userId).child("booklist").child(bookId).child("record")
        ref.setValue(pageId)
            .addOnSuccessListener {
                Log.d("StoryViewModel", "Reading path updated successfully.")
            }
            .addOnFailureListener {
                Log.e("StoryViewModel", "Failed to update reading path: ${it.message}")
            }
    }
    suspend fun updateReadingPathWithCoroutine(bookId: String, pageId: String, choiceText: String) {
        try {
            updateReadingPath(bookId, pageId, choiceText)
        } catch (e: Exception) {
            Log.e("StoriesViewModel", "Failed to update reading path with coroutine: ${e.message}")
        }
    }
    fun updateReadingPath(bookId: String, pageId: String, choiceText: String) {
        val userId = auth.currentUser?.uid ?: return
        val pathRef = db.child("user").child(userId).child("booklist").child(bookId).child("path")
        val choicesRef = db.child("user").child(userId).child("booklist").child(bookId).child("choices")
        val percentRef = db.child("user").child(userId).child("booklist").child(bookId).child("percent")

        // 获取当前 path 以确定下一个 key
        pathRef.get().addOnSuccessListener { dataSnapshot ->
            val pathSize = dataSnapshot.childrenCount
            val nextKey = pathSize.toString()  // 将数字转换为字符串作为 key

            // 更新 path 为当前选择的页面
            pathRef.child(nextKey).setValue(choiceText)
                .addOnSuccessListener {
                    Log.d("StoryViewModel", "Path updated successfully with choice: $choiceText at key $nextKey.")
                    updateCompletePath(pathRef)
                }
                .addOnFailureListener {
                    Log.e("StoryViewModel", "Failed to update path: ${it.message}")
                }

            // 获取 choices 路径下的键的数量
            choicesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val choicesCount = snapshot.childrenCount

                    // 获取特定页面的 choices 路径下的键的数量
                    val pageChoicesRef = db.child("stories").child(bookId).child("pages")
                    pageChoicesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val pageChoicesCount = snapshot.childrenCount

                            // 计算百分比
                            val percentage = (choicesCount.toDouble() / pageChoicesCount.toDouble()) * 100

                            // 将百分比值存储到数据库中
                            percentRef.setValue(percentage)
                                .addOnSuccessListener {
                                    Log.d("StoryViewModel", "Percentage value stored successfully: $percentage")
                                }
                                .addOnFailureListener {
                                    Log.e("StoryViewModel", "Failed to store percentage value: ${it.message}")
                                }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Log.e("StoryViewModel", "Database error: ${databaseError.message}")
                        }
                    })
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("StoryViewModel", "Database error: ${databaseError.message}")
                }
            })
        }.addOnFailureListener {
            Log.e("StoryViewModel", "Failed to retrieve path: ${it.message}")
        }
    }

    private fun updateCompletePath(pathRef: DatabaseReference) {
        pathRef.orderByKey().addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val pathList = mutableListOf<String>()
                dataSnapshot.children.forEach { snapshot ->
                    snapshot.getValue(String::class.java)?.let {
                        pathList.add(it)
                    }
                }
                val completePathString = pathList.joinToString(" -> ")
                _completePath.postValue(completePathString)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("StoryViewModel", "Database error: ${databaseError.message}")
            }
        })

    }
    fun loadFirstPage(bookId: String) {
        viewModelScope.launch {
            try {
                val bookSnapshot = withContext(Dispatchers.IO) {
                    db.child("stories").child(bookId).get().await()
                }
                val book = bookSnapshot.getValue<Book>()
                if (book != null) {
                    book.pages.values.firstOrNull { it.firstPage }?.let {
                        Log.d("ViewModel", "Loading first page ${it.id}")
                        loadPage(bookId, it.id)
                        dateReadingPath(bookId, it.id)

                        // 在加载第一页时重置 path
                        resetPath(bookId)
                    }
                } else {
                    Log.d("ViewModel", "Book not found")
                }
            } catch (e: Exception) {
                Log.e("ViewModel", "Failed to load first page: ${e.message}")
            }
        }
    }

    fun resetPath(bookId: String) {
        val userId = auth.currentUser?.uid ?: return
        val pathRef = db.child("user").child(userId).child("booklist").child(bookId).child("path")

        // 重置 path 为第一页，键为 "0"，值为 "firstPage"
        val firstPageMap = mapOf("0" to "firstPage")
        pathRef.setValue(firstPageMap)
            .addOnSuccessListener {
                Log.d("StoryViewModel", "Path reset successfully to first page.")
            }
            .addOnFailureListener {
                Log.e("StoryViewModel", "Failed to reset path: ${it.message}")
            }
    }
    fun updateChoicesIfNeeded(bookId: String, choiceText: String) {
        val userId = auth.currentUser?.uid ?: return
        val choicesRef = db.child("user").child(userId).child("booklist").child(bookId).child("choices")

        choicesRef.get().addOnSuccessListener { dataSnapshot ->
            val currentChoices = dataSnapshot.getValue<String>() ?: ""
            if (!currentChoices.contains(choiceText)) {
                val updatedChoices = if (currentChoices.isEmpty()) choiceText else "$currentChoices, $choiceText"
                choicesRef.setValue(updatedChoices)
                    .addOnSuccessListener {
                        Log.d("StoryViewModel", "Choices updated successfully.")
                    }
                    .addOnFailureListener {
                        Log.e("StoryViewModel", "Failed to update choices: ${it.message}")
                    }
            }
        }.addOnFailureListener {
            Log.e("StoryViewModel", "Failed to get current choices: ${it.message}")
        }
    }
}

data class Book(
    var bookid: String = "",
    val name: String = "",
    var authorId: String = "",
    var authorName: String = "",
    val description: String = "",
    val coverUrl: String = "",
    val imageUrl: String = "",
    var pages: Map<String, Page> = mapOf(),  // 确保pages是可变的，以允许设置pageid
    val category: String = "",
    var id:String="",
    var page:String="",
    var pagetext:String="",
)

data class Page(
    var pageid: String = "",
    var text: String = "",
    var choices: Map<String, String> = mapOf(),  // 存储 choice 文本和 destination 页面 ID 的映射
    var id:String="",
    var firstPage: Boolean=false
)

data class Choice(
    var choiceid: String = "",
)
