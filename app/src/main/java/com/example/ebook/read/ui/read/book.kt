package com.example.ebook.read.ui.read

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.io.File

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.Text
import androidx.compose.runtime.*

import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.geometry.Offset

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState

import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.input.pointer.consumeDownChange
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.positionChange
import com.example.ebook.read.model.StoriesViewModel

@Composable
fun TxtFileContent(txtFilePath: String) {
    val fileContent = remember(txtFilePath) {
        File(txtFilePath).readText()
    }

    val pageSize = 4000  // Adjust this to fit the content appropriately
    val pages = remember(fileContent) {
        fileContent.chunked(pageSize)
    }

    var currentPage by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .padding(16.dp)
            .background(Color.White)
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val thirdWidth = size.width / 3
                    when {
                        offset.x < thirdWidth -> {
                            if (currentPage > 0) currentPage--
                        }
                        offset.x > 2 * thirdWidth -> {
                            if (currentPage < pages.size - 1) currentPage++
                        }
                    }
                }
            }
    ) {
        BasicText(
            text = pages.getOrElse(currentPage) { "No content" },
            modifier = Modifier.fillMaxWidth()
        )
    }
}



//我使用的是firebase Realtime Database，我想要创建的是一个多选项的小说，存放的位置和树一样
@Preview
@Composable
fun PreviewTxtFileContent() {
    TxtFileContent("C:/Users/23227/Desktop/read/pin.txt")
}