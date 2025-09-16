package com.example.ebook.read.ui.content

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun NovelDirectoryScreen() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("小说名") })
        }
    ) {
        NovelDirectoryContent()
    }
}
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun NovelDirectoryContent() {
    val storyPaths = listOf(
        listOf("主路线", "分支A", "结局1"),
        listOf("主路线", "分支A", "结局2"),
        listOf("主路线", "分支B", "结局3"),
        listOf("主路线", "分支B", "结局4"),
        listOf("主路线", "隐藏分支", "隐藏结局")
    )

    val levels = remember { mutableStateListOf<MutableList<String>>() }
    val positions = remember { mutableStateListOf<MutableList<Offset>>() }

    storyPaths.forEach { path ->
        path.forEachIndexed { index, node ->
            if (levels.size <= index) {
                levels.add(mutableListOf())
                positions.add(mutableListOf())
            }
            if (!levels[index].contains(node)) {
                levels[index].add(node)
            }
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val constraintsMaxHeight = maxHeight

        Column(modifier = Modifier.padding(16.dp)) {
            levels.forEachIndexed { index, level ->
                StoryLevel(level, positions[index])
                if (index < levels.size - 1) {
                    Spacer(modifier = Modifier.height(50.dp)) // Add space between levels for drawing connections
                }
            }
        }

        val maxWidth = calculateMaxWidth(positions)
        val maxHeight = constraintsMaxHeight.value + 50.dp.value * (levels.size - 1) // Include spacers

        DrawConnections(levels, positions, maxWidth, maxHeight)
    }
}

@Composable
fun DrawConnections(levels: List<List<String>>, positions: List<MutableList<Offset>>, maxWidth: Float, maxHeight: Float) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .height(maxHeight.dp)
            .width(maxWidth.dp)
    ) {
        for (i in 0 until levels.size - 1) {
            val currentLevel = levels[i]
            val nextLevel = levels[i + 1]
            val currentPositions = positions[i]
            val nextPositions = positions[i + 1]
            currentLevel.forEachIndexed { index, node ->
                nextLevel.forEachIndexed { nextIndex, nextNode ->
                    if (node == nextNode || nextNode.startsWith(node)) {
                        drawLine(
                            color = Color.Black,
                            start = currentPositions[index],
                            end = nextPositions[nextIndex],
                            strokeWidth = 2f
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StoryLevel(level: List<String>, positionsState: MutableList<Offset>) {
    var needToUpdatePositions by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        level.forEachIndexed { index, node ->
            Button(
                onClick = { /* handle node selection */ },
                modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
                    // 计算位置
                    val pos = Offset(
                        x = layoutCoordinates.positionInParent().x + layoutCoordinates.size.width / 2f,
                        y = layoutCoordinates.positionInParent().y + layoutCoordinates.size.height.toFloat()
                    )
                    // 更新状态变量，触发 LaunchedEffect
                    if (positionsState.size <= index) {
                        positionsState.add(pos)
                    } else {
                        positionsState[index] = pos
                    }
                    needToUpdatePositions = true
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(node, fontSize = 12.sp)
            }
        }
    }

    // LaunchedEffect 监听状态变量，执行相关逻辑
    LaunchedEffect(needToUpdatePositions) {
        if (needToUpdatePositions) {
            // 这里可以放置一些需要在位置更新后执行的代码
            needToUpdatePositions = false  // 重置状态，防止重复执行
        }
    }
}

fun calculateMaxWidth(positions: List<MutableList<Offset>>): Float {
    return positions.flatten().maxOfOrNull { it.x } ?: 0f
}

fun calculateMaxHeight(positions: List<MutableList<Offset>>): Float {
    return positions.flatten().maxOfOrNull { it.y } ?: 0f
}

@Preview(showBackground = true)
@Composable
fun PreviewNovelDirectoryScreen() {
    NovelDirectoryScreen()
}