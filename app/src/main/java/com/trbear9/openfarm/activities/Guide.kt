package com.trbear9.openfarm.activities

import android.content.Intent
import android.view.LayoutInflater
import android.widget.ImageButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.trbear9.openfarm.R
import com.trbear9.openfarm.TutorialActivity

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun Guide(nav: NavController? = null) {
    var scroll = rememberScrollState()
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Panduan dan Pengetahuan",
                        fontWeight = FontWeight.Bold,
                    )
                },
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
                Box(Modifier.fillMaxWidth().weight(0.1f)) {
                }
                Box(Modifier.fillMaxWidth().weight(2f)) {
                    Column(Modifier.fillMaxSize().verticalScroll(scroll)) {
                        Item(click = {
                            val intent = Intent(context, TutorialActivity::class.java)
                            context.startActivity(intent)
                                     }, content = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = R.drawable.result_imgplant),
                                    contentDescription = "Plant",
                                    modifier = Modifier.fillMaxWidth(0.3f)
                                        .padding(16.dp)
                                )
                                Text(
                                    text = "Tutorial",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 30.sp,
                                    modifier = Modifier
                                        .padding(top = 16.dp)
                                )
                            }
                        })
                        Item(click = {
                            nav?.navigate("tentang")
                        }, content = {
                            Row {
                                Image(
                                    painter = painterResource(id = R.drawable.mengapapenting),
                                    contentDescription = "Plant",
                                    modifier = Modifier.fillMaxWidth(0.3f)
                                        .padding(16.dp)
                                )
                                Text(
                                    text = "Mengapa tanaman itu penting?",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp,
                                    modifier = Modifier
                                        .padding(top = 16.dp)
                                )
                            }
                        }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Item(content: @Composable ColumnScope.() -> Unit, click: () -> Unit = {}) {
    Card(
        modifier = Modifier.background(Color.White)
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .fillMaxWidth()
            .aspectRatio(16 / 5f)
            .clickable {
                click
            },
        shape = RoundedCornerShape(16.dp), content = content
    )
}