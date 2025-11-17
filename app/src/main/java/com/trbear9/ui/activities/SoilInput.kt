package com.trbear9.ui.activities

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.trbear9.ui.R
import com.trbear9.ui.util.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun InputSoil(
    nav: NavController? = null,
    onClick: (pH: Float?, depth: Int?) -> Unit? = { a, b -> }
) {
    LocalContext.current
    var pH by remember { mutableStateOf("") }
    var depth by remember { mutableStateOf("20") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFEDF1DE),
                        Color(0xFFE3F0F3),
                        Color(0xFFEFDFEE)  // ulang atau variasi
                    ), start = Offset(500f, 0f), end = Offset.Infinite
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Soil",
                            fontWeight = FontWeight.Bold,
//                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            nav?.navigate(Screen.camera)
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                )
            }
        ) { padding ->
            Column(
//                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .align(Alignment.Center),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.grafik_ph),
                    contentDescription = "Soil",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                )
                Text(
                    text = "Nilai pH lebih baik di kosongkan jika tak memiliki pH meter",
                    fontSize = 20.sp,
                    color = Color.Black,
                    style = TextStyle.Default,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 32.dp)
                )
                OutlinedTextField(
                    value = pH,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() || (it == ',' || it == '.') }) {
                            pH = input
                        }
                    },
                    label = { Text("pH tanah") },
//                    placeholder = { Text("5.6") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                )
                OutlinedTextField(
                    value = depth,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() || (it == ',' || it == '.') }) {
                            depth = input
                        }
                    },
                    label = { Text("Kedalaman tanah (cm)") },
                    placeholder = { Text("20") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                )

                val pH = pH.replace(",", ".").toFloatOrNull()
                val depth = depth.replace(",", ".").toFloatOrNull()?.toInt()
                Button(
                    onClick = {
                        onClick(pH, depth)
                        Log.d("SOIL", "pH = $pH and depth = $depth")
//                        Toast.makeText(context, "pH: $pH, depth: $depth", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 80.dp, vertical = 8.dp)
                ) {
                    Text(text = "Next")
                }
            }
        }
    }
}