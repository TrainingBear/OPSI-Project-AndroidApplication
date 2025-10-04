package com.trbear9.openfarm.activities

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun Guide(nav: NavController? = null) {
    var scroll = rememberScrollState()
    Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(
                        text = "Panduan dan Pengetahuan",
                        fontWeight = FontWeight.Bold,
                    ) },
                    navigationIcon = {
                        IconButton(onClick = {
                            nav?.navigate("home")
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                    )
                )
            }
        ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(it)
                .background(Color(0xFFF5F5F5))
        ) {
            Column(
                Modifier.fillMaxSize().padding(it)
            ) {
                Box(Modifier.fillMaxWidth().weight(1f)){

                }
                Box(Modifier.fillMaxWidth().weight(2f)) {
                    Column(Modifier.fillMaxSize().verticalScroll(scroll)) {
                        Card(
                            modifier = Modifier.background(Color.White)
                                .padding(16.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .fillMaxWidth()
                                .aspectRatio(16 / 5f),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(text = "Panduan dan Pengetahuan")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Scaffold(topBar: @Composable () -> Unit, content: @Composable () -> Unit) {
    TODO("Not yet implemented")
}