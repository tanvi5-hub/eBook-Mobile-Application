package com.example.ebook.read.model


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserViewModel : ViewModel() {
    private var currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val booklistLiveData = MutableLiveData<List<Book>>()
    private val bestAuthorNameLiveData = MutableLiveData<String>()
    private val bestAuthorDetailsLiveData = MutableLiveData<List<AuthorDetails>>()
    private val AuthorDetailsLiveData = MutableLiveData<AuthorDetails>()
    private val AuthorbookDetailsLiveData = MutableLiveData<List<Book>>()
    private val userId = Firebase.auth.currentUser?.uid ?: ""
    private val userdatabase = Firebase.database.reference
    private val _isBookInBookstore = MutableLiveData<Boolean>()
    private val db = Firebase.database.reference
    private val _isFollowing = MutableLiveData<Boolean>()
    val isFollowing = MutableLiveData<Boolean>()
    private val _userDetails = MutableLiveData<userDetails>()
    val userDetails: LiveData<userDetails> = _userDetails
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    fun updateReadingPath(userId: String, bookId: String, path: String) {
        val ref = FirebaseDatabase.getInstance().getReference("user/$userId/booklist")
        ref.child(bookId).setValue(path)
            .addOnSuccessListener {
                println("Reading path updated successfully.")
            }
            .addOnFailureListener {
                println("Failed to update reading path: ${it.message}")
            }
    }

    fun fetchCurrentPath(userId: String, bookId: String) {
        val ref = FirebaseDatabase.getInstance().getReference("user/$userId/booklist/$bookId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentPath = snapshot.getValue(String::class.java)
                if (currentPath != null) {
                    println("Current reading path: $currentPath")
                    // 可以根据路径继续加载页面等操作
                } else {
                    println("No reading path found.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Failed to fetch reading path: ${error.message}")
            }
        })
    }

    fun uploadImageToFirebaseStorage(imageUri: Uri, userId: String) {
        val storageRef = Firebase.storage.reference.child("images/$userId-image.jpg")
        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    updateImageUrlInFirebase(userId, imageUrl)
                }
            }
    }


    private fun updateImageUrlInFirebase(userId: String, newImageUrl: String) {
        Firebase.database.reference
            .child("user/$userId/imageurl")
            .setValue(newImageUrl)
            .addOnSuccessListener {
                // Handle success
            }
    }

    private fun uploadImageToFirebase(imageUri: Uri) {
        val userId = Firebase.auth.currentUser?.uid
        if (userId == null) {
            Log.e("UploadImage", "User not logged in")
            return
        }

        // 创建一个指向 Firebase Storage 中特定位置的引用
        val storageReference =
            FirebaseStorage.getInstance().reference.child("userImages/$userId/profile.jpg")

        // 开始上传
        storageReference.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                // 上传成功
                taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    // 更新用户的图片 URL 到 Firebase Database
                    updateUserImageUrlInDatabase(imageUrl, userId)
                    Log.d("UploadImage", "Image uploaded and URL updated in database")
                }
            }
            .addOnFailureListener { e ->
                // 上传失败
                Log.e("UploadImage", "Failed to upload image", e)
            }
    }

    private fun updateUserImageUrlInDatabase(imageUrl: String, userId: String) {
        // 获取数据库引用
        val userRef = FirebaseDatabase.getInstance().getReference("user/$userId")

        // 更新数据库中的 imageUrl 字段
        userRef.updateChildren(mapOf("imageUrl" to imageUrl))
            .addOnSuccessListener {
                Log.d("DatabaseUpdate", "Image URL updated successfully in database")
            }
            .addOnFailureListener { e ->
                Log.e("DatabaseUpdate", "Failed to update image URL in database", e)
            }
    }

    fun getreaderDetails(): LiveData<userDetails> {
        loadUserDetails()
        return userDetails
    }


    private fun loadUserDetails() {
        currentUser?.uid?.let { userId ->
            val userRef = database.getReference("user/$userId")
            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = snapshot.child("name").value as? String
                    val imageUrl = snapshot.child("imageurl").value as? String
                    val userId = userId
                    _userDetails.postValue(userDetails(name, imageUrl, userId))
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("UserViewModel", "Failed to read user details", error.toException())
                }
            })
        }
    }

    fun pickImage(context: Context) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "选择图片")
        context.startActivity(chooser)
    }

    fun updateUserImage(imageUri: Uri) {
        val userId = currentUser?.uid
        val storageRef = Firebase.storage.reference.child("user_images/$userId.png")

        storageRef.putFile(imageUri).addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()
                updateUserImageUrl(imageUrl)
            }
        }.addOnFailureListener {
            Log.e("UserViewModel", "Failed to upload image", it)
        }
    }

    private fun updateUserImageUrl(imageUrl: String) {
        val userId = currentUser?.uid
        val imageRef = database.getReference("user/$userId/imageurl")
        imageRef.setValue(imageUrl)
    }

    fun getUser(): FirebaseUser? {
        return currentUser
    }

    fun watchBooklist(bookId: String) {
        currentUser?.uid?.let { userId ->
            val path = "user/$userId/booklist/$bookId"
            Log.d("UserViewModel4", "No book IDs available$/$userId/booklist/$bookId")
            database.getReference(path).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // 如果该节点存在，更新 LiveData 为 true，表示用户已关注该作者
                    isFollowing.value = snapshot.exists()
                    Log.d("UserViewModel4", "No book IDs available${isFollowing.value}")
                }

                override fun onCancelled(error: DatabaseError) {
                    // 在监听过程中发生错误，可以在这里处理
                    Log.e(
                        "UserViewModel",
                        "Failed to watch following status",
                        error.toException()
                    )
                }
            })
        }
    }

    //就是这里
    fun toggleBooklist(bookId: String, isFollowing: Boolean) {
        val userId = Firebase.auth.currentUser?.uid
        if (userId == null) {
            Log.e("UserViewModel", "User not logged in")
            return
        }

        val userBooklistPath = "user/$userId/booklist/$bookId"
        val booklistRef = Firebase.database.reference.child(userBooklistPath)
        val bookCountPath = "stories/$bookId/number"
        val bookCountRef = Firebase.database.reference.child(bookCountPath)

        if (isFollowing) {

            // 如果当前已关注，执行取消关注操作，即删除节点
            booklistRef.removeValue().addOnSuccessListener {
                Log.d("UserViewModel", "Unfollowed book successfully")
                // 取消关注时，收藏人数减1
                bookCountRef.runTransaction(object :
                    com.google.firebase.database.Transaction.Handler {
                    override fun doTransaction(mutableData: MutableData): com.google.firebase.database.Transaction.Result {
                        val currentCount = mutableData.getValue(Int::class.java)
                            ?: return com.google.firebase.database.Transaction.success(mutableData)
                        if (currentCount > 0) {
                            mutableData.value = currentCount - 1
                        }
                        return com.google.firebase.database.Transaction.success(mutableData)
                    }

                    override fun onComplete(
                        databaseError: DatabaseError?,
                        b: Boolean,
                        dataSnapshot: DataSnapshot?
                    ) {
                        Log.d("UserViewModel", "Updated followers count down")
                    }
                })
            }.addOnFailureListener {
                Log.e("UserViewModel", "Failed to unfollow book", it)
            }
        } else {
            // 关注操作
            val pagesPath = "stories/$bookId/pages"
            val pagesRef = Firebase.database.reference.child(pagesPath)
            pagesRef.orderByChild("firstPage").equalTo(true).limitToFirst(1)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val firstPageEntry = snapshot.children.firstOrNull()
                            val firstPageId = firstPageEntry?.key ?: "defaultPageId"

                            // 设置首个页面的记录和初始化选项节点
                            val updates = hashMapOf<String, Any>(
                                "record" to firstPageId,
                                "path" to hashMapOf<String, String>(
                                    "0" to "firstPage" // 在choices下一级存放名为"firstPage"的值
                                ),
                                "choices" to hashMapOf<String, String>(
                                    "0" to "firstPage" // 在choices下一级存放名为"firstPage"的值
                                )
                            )

                            booklistRef.updateChildren(updates).addOnSuccessListener {
                                Log.d(
                                    "UserViewModel",
                                    "Followed book and initialized record and choices successfully"
                                )
                                // 关注时，收藏人数加1
                                bookCountRef.runTransaction(object :
                                    com.google.firebase.database.Transaction.Handler {
                                    override fun doTransaction(mutableData: MutableData): com.google.firebase.database.Transaction.Result {
                                        val currentCount =
                                            mutableData.getValue(Int::class.java) ?: 0
                                        mutableData.value = currentCount + 1
                                        return com.google.firebase.database.Transaction.success(
                                            mutableData
                                        )
                                    }

                                    override fun onComplete(
                                        databaseError: DatabaseError?,
                                        b: Boolean,
                                        dataSnapshot: DataSnapshot?
                                    ) {
                                        Log.d("UserViewModel", "Updated followers count up")
                                    }
                                })
                            }.addOnFailureListener {
                                Log.e(
                                    "UserViewModel",
                                    "Failed to follow book and initialize record and choices",
                                    it
                                )
                            }
                        } else {
                            Log.e("UserViewModel", "No first page found, cannot follow book")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("UserViewModel", "Failed to retrieve pages: ${error.toException()}")
                    }
                })
        }
    }

    fun watchFollowingAuthor(authorId: String) {
        currentUser?.uid?.let { userId ->
            val path = "user/$userId/bestauthor/$authorId"
            Log.d("UserViewModel4", "No book IDs available$/$userId/bestauthor/$authorId")
            database.getReference(path).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // 如果该节点存在，更新 LiveData 为 true，表示用户已关注该作者
                    isFollowing.value = snapshot.exists()
                    Log.d("UserViewModel4", "No book IDs available${isFollowing.value}")
                }

                override fun onCancelled(error: DatabaseError) {
                    // 在监听过程中发生错误，可以在这里处理
                    Log.e(
                        "UserViewModel",
                        "Failed to watch following status",
                        error.toException()
                    )
                }
            })
        }
    }

    fun toggleFollowingAuthor(authorId: String, isFollowing: Boolean) {
        val userId = Firebase.auth.currentUser?.uid
        val path = "user/$userId/bestauthor/$authorId"
        val authorRef = Firebase.database.reference.child(path)

        if (isFollowing) {
            // 如果当前已关注，执行取消关注操作，即删除节点
            authorRef.removeValue().addOnSuccessListener {
                Log.d("UserViewModel", "Unfollowed author successfully")
            }.addOnFailureListener {
                Log.e("UserViewModel", "Failed to unfollow author", it)
            }
        } else {
            // 如果当前未关注，执行关注操作，即添加节点
            authorRef.setValue(true).addOnSuccessListener {
                Log.d("UserViewModel", "Followed author successfully")
            }.addOnFailureListener {
                Log.e("UserViewModel", "Failed to follow author", it)
            }
        }
    }

    @SuppressLint("NullSafeMutableLiveData")
    private fun loaduserDetails() {
        currentUser?.uid?.let { userId ->
            val bestAuthorsRef = database.getReference("user/$userId/imageurl")
            bestAuthorsRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    viewModelScope.launch {
                        // 使用 it.key 来获取每个子节点的键
                        val name = snapshot.child("name").value as? String
                        val imageUrl = snapshot.child("imageurl").value as? String
                        val introdutcion = snapshot.child("introdutcion").value as? String
                        val userId = userId
                        Log.e("UserViewModel", "Author IDs: $name")
                        userDetails(name, imageUrl, userId)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("UserViewModel", "Failed to read value.", error.toException())
                }
            })
        }
    }

    fun getBooklist(): LiveData<List<Book>> {
        loadUserBooklist()
        return booklistLiveData
    }

    fun getwriterlist(authorIds: String): LiveData<AuthorDetails> {
        viewModelScope.launch {
            fetchWriterDetails(authorIds)
        }
        return AuthorDetailsLiveData
    }

    fun getwriterbooklist(authorIds: String): LiveData<List<Book>> {
        viewModelScope.launch {
            fetchBooksByAuthor(authorIds)
        }
        return AuthorbookDetailsLiveData
    }

    fun getBestAuthorDetails(): LiveData<List<AuthorDetails>> {
        loadBestAuthorDetails()
        return bestAuthorDetailsLiveData
    }

    private fun loadUserBooklist() {
        currentUser?.uid?.let { userId ->
            viewModelScope.launch {
                val bookIds = fetchBookIds(userId)
                fetchBooksDetails(bookIds)
            }
        }
    }

    private fun loadUserBookl() {
        currentUser?.uid?.let { userId ->
            viewModelScope.launch {
                val bookIds = fetchBookIds(userId)
                fetchBooksDetails(bookIds)
            }
        }
    }

    private fun loadBestAuthorDetails() {
        currentUser?.uid?.let { userId ->
            val bestAuthorsRef = database.getReference("user/$userId/bestauthor")
            bestAuthorsRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    viewModelScope.launch {
                        // 使用 it.key 来获取每个子节点的键
                        val authorIds = snapshot.children.mapNotNull { it.key }
                        Log.e("UserViewModel", "Author IDs: $authorIds")
                        fetchAuthorsDetails(authorIds)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("UserViewModel", "Failed to read value.", error.toException())
                }
            })
        }
    }

    private suspend fun fetchBestAuthorIds(userId: String): List<String> =
        withContext(Dispatchers.IO) {
            val bestAuthorsRef = database.getReference("user/$userId/bestauthor/bestauthorids")
            try {
                val snapshot = bestAuthorsRef.get().await()
                val authorIds = snapshot.children.map { it.value.toString() }
                return@withContext authorIds
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error fetching best author IDs", e)
                return@withContext listOf()
            }
        }

    private suspend fun fetchAuthorsDetails(authorIds: List<String>) {
        val authorDetailsList = coroutineScope {
            authorIds.map { authorId ->
                async(Dispatchers.IO) {
                    try {
                        val authorRef = database.getReference("user/$authorId")
                        val snapshot = authorRef.get().await()
                        val name = snapshot.child("name").value as? String
                        val imageUrl = snapshot.child("imageurl").value as? String
                        val authorId = authorId
                        if (name != null) {
                            AuthorDetails(name, imageUrl, authorId)
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        Log.e(
                            "UserViewModel",
                            "Error fetching author details for ID $authorId",
                            e
                        )
                        null
                    }
                }
            }.awaitAll().filterNotNull()
        }
        bestAuthorDetailsLiveData.postValue(authorDetailsList)
    }

    @SuppressLint("NullSafeMutableLiveData")
    private suspend fun fetchAuthorName(authorId: String) {
        val authorNameRef = database.getReference("user/$authorId/name")
        try {
            val snapshot = authorNameRef.get().await()
            val authorName = snapshot.value as? String
            bestAuthorNameLiveData.postValue(authorName)
            Log.d("UserViewModel", "Fetched author name: $authorName")
        } catch (e: Exception) {
            Log.e("UserViewModel", "Error fetching author name", e)
            bestAuthorNameLiveData.postValue("Unknown")
        }
    }

    @SuppressLint("NullSafeMutableLiveData")
    private suspend fun fetchAuthorimage(authorId: String) {
        val authorimageRef = database.getReference("user/$authorId/imageurl")
        try {
            val snapshot = authorimageRef.get().await()
            val authorimage = snapshot.value as? String
            bestAuthorNameLiveData.postValue(authorimage)
            Log.d("UserViewModel", "Fetched author name: $authorimage")
        } catch (e: Exception) {
            Log.e("UserViewModel", "Error fetching author name", e)
            bestAuthorNameLiveData.postValue("Unknown")
        }
    }

    private suspend fun fetchBookIds(userId: String): List<String> =
        withContext(Dispatchers.IO) {
            val userRef = database.getReference("user/$userId/booklist")
            try {
                val snapshot = userRef.get().await()
                val bookIds = snapshot.children.map { it.key!! } // 获取所有子节点的键，这些键是书籍ID
                Log.d("UserViewModel", "Fetched book IDs: $bookIds")
                return@withContext bookIds
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error fetching book IDs", e)
                return@withContext emptyList()
            }
        }

    //这里增加当前阅读记录
    private suspend fun fetchBooksDetails(bookIds: List<String>) {
        val books = mutableListOf<Book>()
        val booksRef = database.getReference("stories")
        currentUser?.uid?.let { userId ->
            bookIds.forEach { id ->
                try {
                    val ref =
                        db.child("user").child(userId).child("booklist").child(id).child("record")
                    val userRef = database.getReference("user/$userId/booklist/$id/record")
                    val page = database.getReference("user/$userId/booklist/$id/record")

                    val pageIdSnapshot = userRef.get().await()
                    val pageId = pageIdSnapshot.getValue(String::class.java)
                    val pageTextSnapshot =
                        pageId?.let {
                            database.getReference("stories").child(id).child("pages").child(it)
                                .child("text").get().await()
                        }
                    val pageText = pageTextSnapshot?.getValue(String::class.java)
                    val snapshot = booksRef.child(id).get().await()
                    snapshot.getValue(Book::class.java)?.let { book ->
                        book.pagetext = pageText.toString()
                        book.bookid = id
                        books.add(book)
                    }
                } catch (e: Exception) {
                    Log.e("UserViewModel", "Error fetching book details for ID $id", e)
                }
            }
            booklistLiveData.postValue(books)
            Log.d("UserViewModel", "Posted books to LiveData")
        }
    }

    private fun fetchBooksByAuthor(authorId: String) {
        val booksRef = database.getReference("stories")
        Log.e("UserViewModel1", "Error fetching author details for ID $authorId")
        booksRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                viewModelScope.launch {
                    val books = snapshot.children.mapNotNull { childSnapshot ->
                        val bookAuthorId = childSnapshot.child("authorId").value as? String
                        if (bookAuthorId == authorId) {
                            childSnapshot.getValue(Book::class.java)?.apply {
                                bookid = childSnapshot.key ?: ""
                                Log.d(
                                    "UserViewModel1",
                                    "Error fetching author details for ID $name"
                                )
                            }
                        } else null
                    }
                    if (books.isEmpty()) {
                        Log.d("UserViewModel5", "No books found for author ID $authorId")
                    }
                    AuthorbookDetailsLiveData.postValue(books)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("UserViewModel", "Failed to read value.", error.toException())
            }
        })
    }

    @SuppressLint("NullSafeMutableLiveData", "SuspiciousIndentation")
    private suspend fun fetchWriterDetails(authorId: String) {
        val authorDetails = withContext(Dispatchers.IO) {

            try {
                val authorRef = database.getReference("user/$authorId")
                val snapshot = authorRef.get().await()
                val name = snapshot.child("name").value as? String

                val imageUrl = snapshot.child("imageurl").value as? String

                AuthorDetails(name, imageUrl, authorId)

            } catch (e: Exception) {
                Log.e("UserViewModel", "Error fetching author details for ID $authorId", e)
                null
            }
        }
        // 直接发布 AuthorDetails 对象到 LiveData
        AuthorDetailsLiveData.postValue(authorDetails)
    }

    fun watchBookInBooklist(bookId: String) {
        val path = "user/$userId/booklist/$bookId"
        database.getReference(path).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // 如果该节点存在，更新 LiveData 为 true，表示书籍已在书架上
                _isBookInBookstore.value = snapshot.exists()
            }

            override fun onCancelled(error: DatabaseError) {
                // 在监听过程中发生错误，可以在这里处理
                _isBookInBookstore.value = false
            }
        })

    }

    private val firebaseAuth = FirebaseAuth.getInstance()

    fun logout() {
        firebaseAuth.signOut()
        // 清除用户数据等操作

    }

    fun saveBioToFirebas(userId: String, bioText: String) {
        val firebaseRef = Firebase.database.reference.child("user").child(userId).child("bio")
        firebaseRef.setValue(bioText)
    }



    private val firebaseDatabase = Firebase.database.reference

    private val introductionReference =
        firebaseDatabase.child("user").child("userid").child("introduction")

    private val _introduction = MutableLiveData<String>()
    val introduction: LiveData<String> = _introduction

    init {
        introductionReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val introductionValue = snapshot.getValue(String::class.java)
                _introduction.value = introductionValue ?: ""
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    fun updateIntroduction(introduction: String) {
        introductionReference.setValue(introduction)
    }


}
        data class User(
    var userId: String = "",
    var name: String = ""
)
data class userDetails(val name: String?, val imageUrl: String?,val userId: String?)
data class AuthorDetails(val name: String?, val imageUrl: String?,val authorId:String)
