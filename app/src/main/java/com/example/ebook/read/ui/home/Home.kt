package com.example.ebook.read.ui.home




import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.FindInPage
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController



@OptIn(ExperimentalAnimationApi::class)

@Composable
fun HomeBottomAppBarExample(navController: NavHostController,
) {
    BottomAppBar {
        // 使用Row来水平排列按钮，并且平均分配空间
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(), // 让Row填充整个BottomAppBar的宽度
            horizontalArrangement = Arrangement.SpaceBetween // 在按钮之间平均分配空间
        ) {
            // 为每个Button指定weight(1f)，以确保它们平均分配空间
            TextButton(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(), // 平均分配空间
                onClick = { navController.navigate("home") } // 导航到书籍页面
            ) {
                Icon(
                    Icons.Filled.Book,
                    contentDescription = "Localized description",
                    tint = Color.Black
                )
                Text(text = "Bookstore", color = Color.Black)
            }
            TextButton(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(), // 平均分配空间
                onClick = { navController.navigate("library") } // 导航到查找页面
            ) {
                Icon(
                    Icons.Filled.FindInPage,
                    contentDescription = "Localized description",
                    tint = Color.Black
                )
                Text(text = "Library", color = Color.Black)
            }
            TextButton(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(), // 平均分配空间
                onClick = { navController.navigate("personal") } // 导航到主页
            ) {
                Icon(
                    Icons.Filled.Home,
                    contentDescription = "Localized description",
                    tint = Color.Black
                )
                Text(text = "Profile", color = Color.Black)
            }
        }
    }
}
