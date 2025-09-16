package com.example.ebook.read.ui.navigation


import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import com.example.ebook.read.ui.bestwriter.BestwriterScreen
import com.example.ebook.read.ui.book.NovelDetailsPage
import com.example.ebook.read.ui.home.MainScreen
import com.example.ebook.read.ui.liberary.LibraryScreen
import com.example.ebook.read.ui.personal.PersonScreen
import com.example.ebook.read.ui.read.StoryScreen
import com.example.ebook.read.ui.search.SearchPage
import com.example.ebook.read.ui.version.VersionScreen
import com.example.ebook.read.ui.writer.AuthorDetailsPage
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation() {
    val navController = rememberAnimatedNavController()
    AnimatedNavHost(navController = navController, startDestination = "home") {
        composable("home") { MainScreen(navController) }
        composable("personal") { PersonScreen(navController) }
        composable("library") { LibraryScreen(navController) }
        composable("search") { SearchPage(navController) }
        composable("version"){ VersionScreen(navController)}
        composable("Bestwriter"){ BestwriterScreen(navController)}
        composable("bookDetails/{bookId}") { backStackEntry ->
            // 从路由参数中获取书籍ID
            val bookId = backStackEntry.arguments?.getString("bookId")
            if (bookId != null) {
                NovelDetailsPage(bookId, navController)
            }
        }
        composable("writerDetails/{authorId}") { backStackEntry ->
            val authorId= backStackEntry.arguments?.getString("authorId")
            if (authorId != null) {
                AuthorDetailsPage(authorId, navController)
            }
        }
        composable("Book/{bookId}") { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId")
            if (bookId != null) {
                StoryScreen(bookId, navController)
            }
        }
    }
}

