package com.example.ebook.read.ui.read




import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.KeyboardReturn
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

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



