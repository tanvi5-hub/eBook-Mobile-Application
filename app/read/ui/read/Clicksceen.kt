package com.example.ebook.read.ui.read
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration.Indefinite
import androidx.compose.material3.SnackbarDuration.Short
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult.ActionPerformed
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.compose.material3.FilledIconButton
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material.NavigationRail
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Label

import androidx.compose.ui.Alignment
import com.example.ebook.R
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp




import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.BookOnline
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FindInPage
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Man
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.TextButton
import androidx.compose.material3.CenterAlignedTopAppBar

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar

import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar

import androidx.compose.material3.rememberTopAppBarState

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.KeyboardReturn
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.example.ebook.read.ui.theme.ReaderTheme
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun Clickback(
    shape: Shape = ButtonShape,
    onBackClick: () -> Unit,
    onDetailsClick: () -> Unit
) {
    BottomAppBar {
        // 使用Row来水平排列按钮，并且让它们只占据自身大小的空间
        Row(
            modifier = Modifier.fillMaxHeight(), // 让Row填充整个BottomAppBar的高度
            horizontalArrangement = Arrangement.SpaceBetween // 在按钮之间平分空间
        ) {
            // 返回按钮
            TextButton(
                shape = shape,
                onClick = onBackClick
            ) {
                Icon(
                    Icons.Filled.KeyboardReturn,
                    contentDescription = "Localized description",
                    tint = Color.Black
                )
            }
            // Spacer将"Details"按钮推到最右边
            Spacer(modifier = Modifier.weight(1f))
            // 详情按钮
            TextButton(
                shape = shape,
                onClick = onDetailsClick
            ) {
                Text("Details", color = Color.Black)
            }
        }
    }
}
private val ButtonShape = RoundedCornerShape(0.dp)
@Preview
@Composable
fun Clickscreen(
    shape: Shape = ButtonShape,

) {

    BottomAppBar {
        // 使用Row来水平排列按钮，并且平均分配空间
        Row(
            modifier = Modifier.fillMaxWidth(), // 让Row填充整个BottomAppBar的宽度
            horizontalArrangement = Arrangement.SpaceBetween // 在按钮之间平均分配空间
        ) {
            // 为每个Button指定weight(1f)，以确保它们平均分配空间
            TextButton(
                shape=shape,
                modifier = Modifier.weight(1f), // 平均分配空间
                onClick = { /* do something */ }) {
                Icon(
                    Icons.Filled.MenuBook,
                    contentDescription = "Localized description",
                    tint = Color.Black
                )
                Text(text = "Content", color = Color.Black)
            }
            TextButton(
                shape=shape,
                modifier = Modifier.weight(1f), // 平均分配空间
                onClick = { /* do something */ }) {
                Icon(
                    Icons.Filled.DarkMode,
                    contentDescription = "Localized description",
                    tint = Color.Black
                )
                Text(text = "Find", color = Color.Black)
            }
            TextButton(
                shape=shape,
                modifier = Modifier.weight(1f), // 平均分配空间
                onClick = { /* do something */ }) {
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = "Localized description",
                    tint = Color.Black
                )
                Text(text = "Setting", color = Color.Black)
            }
        }
    }
}



