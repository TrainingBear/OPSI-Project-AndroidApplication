package com.trbear9.openfarm

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightbulbCircle
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.trbear9.internal.Data
import com.trbear9.internal.TFService
import com.trbear9.openfarm.util.Screen
import com.trbear9.plants.Inputs
import com.trbear9.plants.api.GeoParameters

val inputs = Inputs()
var cam: Boolean = false
var gps: Boolean = false
var perm: ActivityResultLauncher<Array<String>> = object: ActivityResultLauncher<Array<String>>(){
    override val contract: ActivityResultContract<Array<String>, *>
        get() = TODO("Not yet implemented")

    override fun launch(
        input: Array<String>,
        options: ActivityOptionsCompat?
    ) {
    }

    override fun unregister() {
    }

}

class MainActivity : AppCompatActivity() {

    private val perm = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Data.load(this)
        setContent {
            App()
        }
        getLocation()
        perm.launch(arrayOf(Manifest.permission.CAMERA))
        if (ContextCompat.checkSelfPermission
                (this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
        ) else {
            cam = true
            Toast.makeText(this, "Camera permission granted!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        TFService.close()
    }

    private fun getLocation() {
        perm.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        );
        if (ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "Tolong aktifkan dan izikan GPS", Toast.LENGTH_SHORT).show();
        } else gps = true

        val geo = GeoParameters();
        LocationServices.getFusedLocationProviderClient(this)
            .lastLocation
            .addOnSuccessListener {
                if (it != null) {
                    geo.latitude = it.latitude;
                    geo.longtitude = it.longitude;
                }
            };
        inputs.geo = geo;
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun Home(nav: NavController? = null) {
    val context = LocalContext.current
    var scroll = rememberScrollState()
    var coffe by remember {mutableStateOf(false)}

    Scaffold(
        bottomBar = {
            var selected by remember { mutableIntStateOf(0) }
            NavigationBar {
                NavigationBarItem(
                    selected = selected == 0,
                    onClick = {
                        selected = 0
                        nav?.navigate(Screen.home)
                    },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = selected == 1,
                    onClick = {
                        selected = 1
                        nav?.navigate(Screen.soilResult)
                    },
                    icon = { Icon(Icons.Default.Park, contentDescription = "Hasil") },
                    label = { Text("Tanaman") }
                )
                NavigationBarItem(
                    selected = selected == 2,
                    onClick = {
                        selected = 2
                        nav?.navigate(Screen.soilStats)
                    },
                    icon = { Icon(Icons.Default.Grain, contentDescription = "Tanah") },
                    label = { Text("Tanah") }
                )
            }
        },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF6FAD4F)),
                modifier = Modifier.wrapContentHeight(),
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(Modifier.fillMaxHeight().weight(7f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = R.drawable.oak_sapling),
                                    contentDescription = "Help",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clickable{nav?.navigate(Screen.about)}
                                )
                                Column(
                                    horizontalAlignment = Alignment.Start,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Text(
                                        text = buildAnnotatedString {
                                            withStyle(
                                                style = SpanStyle(
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 24.sp,
                                                )
                                            ) {
                                                append("OpenFarm")
                                            }
                                            withStyle(
                                                style = SpanStyle(
                                                    color = Color.DarkGray,
                                                    fontWeight = FontWeight.Normal,
                                                    fontSize = 16.sp,
                                                )
                                            ) {
                                                append("\nby Jasper")
                                            }
                                        },
                                        lineHeight = 18.sp,
                                        modifier = Modifier.clickable{nav?.navigate(Screen.about)}
                                    )
                                }
                            }
                        }

                        Box(Modifier.fillMaxHeight().weight(3f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Coffee,
                                    tint = Color.Black,
                                    contentDescription = "Buy us a coffe!",
                                    modifier = Modifier.padding(top = 20.dp, bottom = 20.dp)
                                        .size(30.dp)
                                        .clickable {coffe = !coffe}
                                )

                                Icon(
                                    imageVector = Icons.Default.QuestionMark,
                                    tint = Color.Black,
                                    contentDescription = "Help",
                                    modifier = Modifier.padding(20.dp)
                                        .size(30.dp)
                                        .clickable {
                                            nav?.navigate(Screen.tentang)
                                        }
                                )
                            }
                        }
                    }
                }
            )
        }
    ) {
        if(coffe){
            BasicAlertDialog(
                onDismissRequest = {coffe = false},
                modifier = Modifier
                    .padding(it)
                    .wrapContentSize()
                ,
            ) {
                Column(modifier = Modifier
                    .wrapContentSize()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFFFB7B7))
                    .border(BorderStroke(1.dp, Color.Black))

                    ,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        imageVector = Icons.Default.Coffee,
                        contentDescription = "Coffee",
                        modifier = Modifier.size(50.dp)
                    )
                    Text(
                        text = "Aplikasi ini di buat oleh salah satu tim OPSI SMA Negeri 1 Ambarawa." +
                                "Aplikasi ini tentang bagaimana anda merawat tanaman, dan bagaimana" +
                                "cara merawat tanah di lahan anda.\n\n" +
                                "Sebagai tanda dukungan, berikan kami secangkir kopi:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(10.dp)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, "https://saweria.co/Kujatic".toUri())
                                context.startActivity(intent)
                            },
                            modifier = Modifier.padding(10.dp).wrapContentSize(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(
                                    0xFFFF9100
                                )
                            )
                        ) {
                            Text(
                                text = "Beri dukungan!",
                                fontSize = 18.sp,
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Button(
                            onClick = {coffe = false},
                            modifier = Modifier.padding(10.dp)
                                .wrapContentSize(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            )
                        ){
                            Text(
                                text = "Tidak, terimakasih!",
                                color = Color.Black,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(Color(0xFF6FAD4F))
                .verticalScroll(scroll)
            ,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(Modifier.fillMaxWidth().weight(1f)) {
                BoxWithConstraints(Modifier.fillMaxSize()) {
                    val dynamicFontSize = (maxHeight.value / 4.4).sp
                    val icon = (maxHeight/ 3)
                    Button(
                        onClick = {nav?.navigate(Screen.search)},
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White
                        ),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            modifier = Modifier.size(icon),
                            tint = Color.Black
                        )
                        Text(
                            text = "Cek database tanaman",
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            fontSize = dynamicFontSize,
                        )

                    }
                }
            }
            Box(Modifier.fillMaxWidth().weight(2f)) {
                BoxWithConstraints(Modifier.fillMaxSize()) {
                    val dynamicFontSize = (maxHeight.value / 4).sp
                    val dy2 = (maxHeight.value / 9).sp
                    Text(
                        text = buildAnnotatedString {
                            append("Open Farm")
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = dy2,
                                    fontFamily = FontFamily.SansSerif,
                                )
                            ) {
                                append("\n")
                                append("Teman belajar bertani anda\nkapan saja dan dimana saja!")
                            }
                        },
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = dynamicFontSize,
                        fontFamily = FontFamily.SansSerif,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center).fillMaxSize()
                            .padding(top = 20.dp)
                    )
                }
            }
            Box(modifier = Modifier.fillMaxWidth().weight(5f)) {

                Image(
                    painter = painterResource(id = R.drawable.background_opsi_mainactivity_rescaled),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16/13.5f)
                        .align(Alignment.Center),
                )

                BoxWithConstraints(Modifier.fillMaxWidth()
                    .fillMaxWidth()
                    .aspectRatio(16/3f)
                    .align(Alignment.BottomCenter)
                    .padding(start = 50.dp, end = 50.dp, bottom = 20.dp)
                ) {
                    val dynamicFontSize = (maxHeight.value / 2).sp
                    Button(
                        modifier = Modifier
                            .fillMaxSize(),
                        onClick = {
                            perm.launch(arrayOf(Manifest.permission.CAMERA))
                            if (ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.CAMERA
                                ) != PackageManager.PERMISSION_GRANTED
                            ) else {
                                cam = true
                                Toast.makeText(
                                    context,
                                    "Camera permission granted!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                if(gps) nav?.navigate(Screen.camera)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF5A623)
                        ),
//                            border = BorderStroke(2.dp, Color.White),
                    ) {
                        Text(
                            text = "Scan tanahmu!",
                            fontSize = dynamicFontSize,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            modifier = Modifier.fillMaxSize()
                                .align(Alignment.CenterVertically),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }

    }
}