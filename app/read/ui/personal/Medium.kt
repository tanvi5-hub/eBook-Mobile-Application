package com.example.ebook.read.ui.personal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.*
import androidx.compose.foundation.text.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults

import com.example.ebook.read.model.UserViewModel


import androidx.compose.foundation.layout.*

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions


import androidx.compose.runtime.*

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.TextField

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.example.ebook.read.model.BioViewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.getValue
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MediumBottomApp(viewModel: BioViewModel) {
    var editableText by remember { mutableStateOf(TextFieldValue()) }
    var isEditing by remember { mutableStateOf(false) }
    var userId by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp)
                .border(0.1.dp, Color.Gray)
                .clickable {
                    if (!isEditing) {
                        isEditing = true
                        editableText = TextFieldValue("")
                    }
                }
        ) {
            Column {
                Text(
                    "Introduction",
                    fontSize = 32.sp,
                    color = if (isEditing) Color.Blue else Color.Black
                )
            }
        }

        BasicTextField(
            value = editableText,
            onValueChange = { value ->
                editableText = value
                if (userId.isNotEmpty()) {
                    viewModel.updateUserBio(userId, value.text)
                }
            },
            textStyle = TextStyle(color = if (editableText.text.isNotEmpty()) Color.Black else Color.Gray),
            modifier = Modifier
                .height(56.dp)
                .fillMaxWidth()
                .padding(16.dp),
            decorationBox = { innerTextField ->
                if (editableText.text.isEmpty()) {
                    Text(
                        text = "Please write your introduction",
                        style = TextStyle(color = Color.Gray),
                        modifier = Modifier.padding(16.dp)
                    )
                }
                innerTextField()
            }
        )
    }

    DisposableEffect(Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        userId = user?.uid ?: ""
        if (userId.isNotEmpty()) {
            viewModel.getUserBio(userId) { userBio ->
                bio = userBio
                editableText = TextFieldValue(userBio)
            }
        }

        onDispose { }
    }
}
@Composable
fun MediumBottomwriter(onClick: () -> Unit) {


        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth() // 使Row充满屏幕宽度
                .padding(0.dp) // 添加一些内边距
                .border(0.1.dp, Color.Black)
                .clickable(onClick = onClick)
        ) {


            Column {
                Text(
                    "Favorite Authors",
                    fontSize = 32.sp // 明确指定字体大小
                )

            }
        }

    }
@Composable
fun MediumBottomset(onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth() // 使Row充满屏幕宽度
            .padding(0.dp) // 添加一些内边距
            .border(0.1.dp, Color.Black)
            .drawWithContent {
                drawContent()
                drawLine(
                    color = Color.Black,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 0.1.dp.toPx()
                )
            }
            .clickable(onClick = onClick)
    ) {
        Spacer(Modifier.width(0.1.dp))

        Column {
            Text(
                "Software version",
                fontSize = 32.sp // 明确指定字体大小
            )
        }
    }
}
@Composable
fun MediumBottom(onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth() // Make Row fill the screen width
            .padding(0.dp) // Add some padding
            .border(0.1.dp, Color.Black)
            .background(Color.Red) // Change background color to red

            .drawWithContent {
                drawContent()
                drawLine(
                    color = Color.Black,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 0.1.dp.toPx()
                )
            }
            .clickable(onClick = onClick)
    ) {
        Spacer(Modifier.width(0.1.dp))

        Column {
            Text(
                "Log out",
                fontSize = 32.sp // Specify font size
            )
        }
    }
}
@Composable
fun LogoutButton(userViewModel: UserViewModel) {
    Button(
        onClick = { userViewModel.logout() },
        colors = ButtonDefaults.buttonColors(Color.Red),
        contentPadding = PaddingValues(16.dp)
    ) {
        Text(text = "Logout", color = Color.White)
    }
}
@Preview
@Composable
fun PreviewSearchResult() {

    MediumBottomwriter(onClick={})
}

