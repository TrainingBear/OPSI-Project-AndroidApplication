package com.tbear9.openfarm

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.EnergySavingsLeaf
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Park
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tbear9.openfarm.activities.Camera
import com.tbear9.openfarm.databinding.BottomsheetPageBinding
import com.tbear9.openfarm.databinding.MainmenuBinding

class MainActivity : AppCompatActivity() {
    companion object {
        var url: String? = null
    }
    private var binding: MainmenuBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainmenuBinding.inflate(layoutInflater)
        setContentView(binding!!.getRoot())
        binding!!.chooseButton.setOnClickListener(View.OnClickListener { v: View? ->
            showBottomSheet()
        })

        binding!!.menuButton.setOnClickListener(View.OnClickListener { v: View? -> showPopup(v) })
        binding!!.helpIcon.setOnClickListener(View.OnClickListener { v: View? ->
            val intent = Intent(this@MainActivity, AboutMeActivity::class.java)
            startActivity(intent)
        })

        binding!!.start.setOnClickListener {
            val intent = Intent(this, Camera::class.java)
            startActivity(intent)
        }

    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    @Preview
    fun Home(){
        Scaffold (
             bottomBar = {
                 var selected by remember { mutableIntStateOf(1) }
                 NavigationBar {
                     NavigationBarItem(
                         selected = selected == 0,
                         onClick = {
                             selected = 0
//                             nav.navigate("home")
                         },
                         icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                         label = { Text("Home") }
                     )
                     NavigationBarItem(
                         selected = selected == 1,
                         onClick = {
                             selected = 1
//                             nav.navigate("result")
                         },
                         icon = { Icon(Icons.Default.Park, contentDescription = "Hasil") },
                         label = { Text("Tanaman") }
                     )
                     NavigationBarItem(
                         selected = selected == 2,
                         onClick = {
                             selected = 2
//                             nav.navigate("tanah")
                         },
                         icon = { Icon(Icons.Default.Grain, contentDescription = "Tanah") },
                         label = { Text("Tanah") }
                     )
                 }
             },
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.LightGray),
                    title = {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Help,
                                contentDescription = "Help",
                                modifier = Modifier.padding(10.dp)
                                    .size(30.dp)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.EnergySavingsLeaf,
                                contentDescription = "Help",
                                tint = Color(0xFF1C8604),
                                modifier = Modifier.padding(10.dp)
                                    .size(40.dp)
                            )
                            Column(
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = "OpenFarm",
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF1C8604)
                                )
                                Text(
                                    text = buildAnnotatedString {
                                        append("Server: ")
                                        if (url != null) withStyle(
                                            style = SpanStyle(
                                                fontWeight = FontWeight.Medium,
                                                color = Color.Green
                                            )
                                        ) {
                                            append("Online ")
                                            withStyle(style = SpanStyle(fontWeight = FontWeight.Light)) {
                                                append("(url)")
                                            }
                                        } else withStyle(
                                            style = SpanStyle(
                                                fontWeight = FontWeight.Medium,
                                                color = Color.Red
                                            )
                                        ) {
                                            append("Offline")
                                        }
                                    },
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 16.sp
                                )
                            }

                        }
                    }
                )
            }
        ) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(Color(0xFF5AC02F))
            ){
                Column(horizontalAlignment = Alignment.CenterHorizontally)
                {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16 / 5f)
                            .padding(top = 50.dp, start = 10.dp, end = 10.dp),
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        content = {
                            Text(
                                text = "More Options",
                                fontWeight = FontWeight.Medium,
                                fontSize = 20.sp
                            )
                        }
                    )
                    Text(
                        text = "Open Farm",
                        fontWeight = FontWeight.Bold,
                        fontSize = 40.sp,
                        fontFamily = FontFamily.SansSerif,
                        modifier = Modifier.padding(top = 20.dp)
                    )
                    Text(
                        text = "Teman belajar bertani anda kapan saja dan dimana saja!",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp,
                        fontFamily = FontFamily.SansSerif,
                        modifier = Modifier.padding(top = 5.dp, start = 15.dp, end = 15.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(3946 / 2523f)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.background_opsi_mainactivity_rescaled),
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 10.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 30.dp, end = 30.dp)
                            .aspectRatio(16 / 3f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White)
                    )
                }
            }
        }
    }

    private fun showPopup(v: View?) {
        val popupMenu = PopupMenu(this, v)
        val inflater = popupMenu.menuInflater
        inflater.inflate(R.menu.popup_menu, popupMenu.getMenu())

        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem): Boolean {
                if (item.getItemId() == R.id.keluar_apk) {
                    finishAffinity() // keluar aplikasi
                    return true
                }
                return false
            }
        })

        popupMenu.show()
    }

    // --- BottomSheet muncul disini ---
    private fun showBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(this@MainActivity)
        val bottomBinding = BottomsheetPageBinding.inflate(getLayoutInflater())
        bottomSheetDialog.setContentView(bottomBinding.getRoot())

        // Tombol Panduan
        bottomBinding.btnPanduan.setOnClickListener(View.OnClickListener { v: View? ->
            val intent = Intent(this@MainActivity, BotakuhPanduanActivity::class.java)
            startActivity(intent)
            bottomSheetDialog.dismiss()
        })

        // Tombol Pengetahuan
        bottomBinding.tombolPengetahuan.setOnClickListener(View.OnClickListener { v: View? ->
            val intent = Intent(this@MainActivity, BotakuhPengetahuanActivity::class.java)
            startActivity(intent)
            bottomSheetDialog.dismiss()
        })

        bottomSheetDialog.show()
    }
}