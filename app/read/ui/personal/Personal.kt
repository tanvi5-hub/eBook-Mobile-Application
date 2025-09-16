package com.example.ebook.read.ui.personal

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.height


import androidx.compose.foundation.layout.*

import com.example.ebook.read.ui.home.HomeBottomAppBarExample
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.ebook.SignInActivity
import com.example.ebook.read.model.BioViewModel

import com.example.ebook.read.model.UserViewModel


@Composable
fun PersonScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: UserViewModel = viewModel(),
    BioviewModel: BioViewModel = viewModel(),
) {
    val context = LocalContext.current // 获取当前Context

    // 读取数据


    Scaffold(
        bottomBar = {
            Column {
                MediumBottom(onClick = {
                    // 返回到SignInActivity页面
                    val intent = Intent(context, SignInActivity::class.java)
                    context.startActivity(intent)
                })
                Spacer(Modifier.height(100.dp))
                HomeBottomAppBarExample(navController)
            }

        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            ImageButton()

            // 显示IntroductionInput
            MediumBottomApp(BioviewModel)

            MediumBottomwriter(onClick = { navController.navigate("Bestwriter") })
            MediumBottomset(onClick = { navController.navigate("version") })

            // 显示IntroductionDisplay


            // 在MediumBottomset下方添加一条黑线
            Divider(color = Color.Black, thickness = 1.dp)
        }
    }
}

