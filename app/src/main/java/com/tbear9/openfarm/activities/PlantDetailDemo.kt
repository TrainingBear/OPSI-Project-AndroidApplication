package com.tbear9.openfarm.activities

import android.R.attr.maxWidth
import android.annotation.SuppressLint
import android.opengl.ETC1.getWidth
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class PlantDetailDemo {

    @SuppressLint("ConfigurationScreenWidthHeight")
    @Composable
    fun getHeightByWidth(width: Dp = getScreenWidth(), bias: Float): Dp {
        val res: Dp = (width!! * bias)
        return res
    }

    @SuppressLint("ConfigurationScreenWidthHeight")
    @Composable
    fun getScreenWidth(): Dp {
        return LocalConfiguration.current.screenWidthDp.dp;
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Preview
    @Composable
    fun Demo() {
        Scaffold(topBar = {
            TopAppBar(
                title = { Text("Results") },
                navigationIcon = {
                    IconButton(onClick = {  }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.LightGray
                )
            )
        }){
            pad ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(pad)
                    .background(Color.White)
            ){
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(getHeightByWidth(maxWidth.dp, bias = 0.6f))
                            .background(Color.Gray)
                            .clip(RoundedCornerShape(16.dp))
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Diserale discota",
                            fontSize = 30.sp,
                        )
                        Text(
                            text = "Tanaman ini ialah tanaman yang hidup di tanah, biasanya hidup di luar negri",
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row {
                            Box(
                                modifier = Modifier
                                    .size(maxWidth.dp/3-3.dp,
                                        getHeightByWidth(maxWidth.dp/3-3.dp, 0.4f)
                                    )
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.LightGray)
                            )
                        }
                    }
                }
            }
        }
    }
}