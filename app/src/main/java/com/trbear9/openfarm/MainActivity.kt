package com.trbear9.openfarm

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonetizationOn
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import com.pseudoankit.coachmark.LocalCoachMarkScope
import com.pseudoankit.coachmark.UnifyCoachmark
import com.pseudoankit.coachmark.model.HighlightedViewConfig
import com.pseudoankit.coachmark.model.ToolTipPlacement
import com.pseudoankit.coachmark.scope.enableCoachMark
import com.pseudoankit.coachmark.util.CoachMarkKey
import com.trbear9.internal.Data
import com.trbear9.internal.TFService
import com.trbear9.openfarm.util.Screen
import com.trbear9.plants.Inputs
import com.trbear9.plants.api.GeoParameters
import androidx.core.content.edit
import com.trbear9.openfarm.util.DataStore

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

//var firstTime = false

class MainActivity : AppCompatActivity() {
    private val perm = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val pref = getSharedPreferences("openfarm", MODE_PRIVATE)
//        firstTime = pref.getBoolean("firstTime", true)
//        pref.edit { putBoolean("firstTime", true) }
        DataStore.init(this)
        DataStore.getPref()!!.edit { putBoolean("firstTime", true) }
        Data.load(this)
        setContent {
            App(this)
        }
        getLocation(this)
        perm.launch(arrayOf(Manifest.permission.CAMERA))
    }

    override fun onDestroy() {
        super.onDestroy()
        TFService.close()
    }
}
fun getLocation(context: Context) {
    perm.launch(
        arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    );
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Tolong izinkan untuk menggunakan fitur ini?", Toast.LENGTH_SHORT).show();
        return
    }
    else gps = true

    val geo = GeoParameters();
    LocationServices.getFusedLocationProviderClient(context)
        .lastLocation
        .addOnSuccessListener {
            if (it != null) {
                geo.latitude = it.latitude;
                geo.longtitude = it.longitude;
            }
        }
    inputs.geo = geo;
    "long: ${geo.longtitude} lat: ${geo.latitude} mdpl: ${geo.altitude}".debug("GETLOCATION")
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun Home(nav: NavController? = null) {
    val context = LocalContext.current
    var scroll = rememberScrollState()
    var coffe by remember { mutableStateOf(false) }

    UnifyCoachmark() {
        LaunchedEffect(Unit) {
            if (DataStore.firstTime) {
                coffe = true
            }
        }
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
                                            .clickable { nav?.navigate(Screen.about) }
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
                                            modifier = Modifier.clickable { nav?.navigate(Screen.about) }
                                        )
                                    }
                                }
                            }

                            Box(Modifier.fillMaxHeight().weight(3f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.MonetizationOn,
                                        tint = Color.Black,
                                        contentDescription = "Buy us a coffe!",
                                        modifier = Modifier.padding(top = 20.dp, bottom = 20.dp)
                                            .size(30.dp)
                                            .clickable { coffe = !coffe }
                                    )

                                    Icon(
                                        imageVector = Icons.Default.QuestionMark,
                                        tint = Color.Black,
                                        contentDescription = "Help",
                                        modifier = Modifier.padding(20.dp)
                                            .size(30.dp)
                                            .clickable {
                                                nav?.navigate(Screen.help)
                                            }
                                            .enableCoachMark(
                                                key = MarkKey.help,
                                                toolTipPlacement = ToolTipPlacement.Start,
                                                highlightedViewConfig = highlightConfig,
                                            ){MarkKey.help.tooltip(ToolTipPlacement.Start)}
                                    )
                                }
                            }
                        }
                    }
                )
            }
        ) {
            if (coffe) {
                BasicAlertDialog(
                    onDismissRequest = { coffe = false },
                    modifier = Modifier
                        .padding(it)
                        .wrapContentSize(),
                ) {
                    BoxWithConstraints(Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .wrapContentSize()
                                .clip(RoundedCornerShape(30.dp))
                                .background(Color(0x99FFFFFF)),
//                            .border(BorderStroke(1.dp, Color.Black)),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = "Coffee",
                                modifier = Modifier.size(50.dp)
                            )
                            Text(
                                text = "Aplikasi ini di buat oleh pelajar SMAN 1 Ambarawa dan pelajar MAN 3 Jakarta pusat." +
                                        "\nSebagai tanda dukungan, bisa beri kami secangkir kopi:",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(10.dp)
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Button(
                                    onClick = {
                                        val intent =
                                            Intent(
                                                Intent.ACTION_VIEW,
                                                "https://saweria.co/Kujatic".toUri()
                                            )
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier
                                        .padding(10.dp).wrapContentSize()
                                        .shadow(10.dp, RoundedCornerShape(20.dp)),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(
                                            0xFFFF9100
                                        )
                                    ),
                                ) {
                                    Text(
                                        text = "Beri dukungan!",
                                        fontSize = 18.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                                Button(
                                    onClick = {
                                        coffe = false
                                        if(DataStore.firstTime){
                                            show(MarkKey.scantanah, MarkKey.help)
                                            DataStore.completeFirstTime()
                                        }
                                    },
                                    modifier = Modifier
                                        .padding(bottom = 10.dp).wrapContentSize()
                                        .shadow(10.dp, RoundedCornerShape(20.dp)),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Gray
                                    ),
                                ) {
                                    val fontsize = (this@BoxWithConstraints.maxWidth.value / 27).sp
                                    Text(
                                        text = "Tidak, terimakasih!",
                                        color = Color.White,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = fontsize
                                    )
                                }
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
                    .verticalScroll(scroll),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(Modifier.fillMaxWidth().weight(1f)) {
                    BoxWithConstraints(Modifier.fillMaxSize()) {
                        val dynamicFontSize = (maxHeight.value / 4.4).sp
                        val icon = (maxHeight / 3)
                        Button(
                            onClick = { nav?.navigate(Screen.search) },
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
                        val dy2 = (maxHeight.value / 8).sp
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
                                    append("Panduan cerdas untuk Agrikultur berkelanjutan berbasis tehnologi.")
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
                Box(modifier = Modifier.fillMaxWidth().weight(5f).padding(bottom = 50.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.background_opsi_mainactivity_rescaled),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16 / 13.5f)
                            .align(Alignment.Center),
                    )
                }
            }
            Box(Modifier.fillMaxSize().padding(it)) {
                BoxWithConstraints(
                    Modifier.fillMaxWidth()
                        .fillMaxWidth()
                        .aspectRatio(16 / 3f)
                        .align(Alignment.BottomCenter)
                        .padding(start = 50.dp, end = 50.dp, bottom = 20.dp)
                ) {
                    val dynamicFontSize = (maxHeight.value / 2).sp
                    Button(
                        modifier = Modifier
                            .fillMaxSize()
                            .shadow(10.dp, RoundedCornerShape(15.dp))
                            .enableCoachMark(
                                key = MarkKey.scantanah,
                                toolTipPlacement = ToolTipPlacement.Top,
                                highlightedViewConfig = highlightConfig,
                            ){
                                MarkKey.scantanah.tooltip(ToolTipPlacement.Top)
                            },
                        onClick = {
                            perm.launch(arrayOf(Manifest.permission.CAMERA))
                            if (ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.CAMERA
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                perm.launch(arrayOf(Manifest.permission.CAMERA))
                                Toast.makeText(
                                    context,
                                    "Tolong izinkan kamera untuk menggunakan fitur ini!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                cam = true
                                nav?.navigate(Screen.camera)
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

@Composable
private fun ColumnScope.CoachMarkTargetText(
    text: String,
    alignment: Alignment.Horizontal,
    key: MarkKey,
    placement: ToolTipPlacement,
    tooltip: @Composable (() -> Unit)? = null
) {
    val coachMarkScope = LocalCoachMarkScope.current

    Text(
        text = text,
        modifier = Modifier
            .align(alignment)
            .enableCoachMark(
                key = key,
                toolTipPlacement = placement,
                highlightedViewConfig = HighlightedViewConfig(
                    shape = HighlightedViewConfig.Shape.Rect(12.dp),
                    padding = PaddingValues(8.dp)
                ),
                coachMarkScope = coachMarkScope,
                tooltip = tooltip
            )
            .padding(16.dp),
        color = Color.Black
    )
}

@Composable
fun Tooltip(key: CoachMarkKey) {
}

@Composable
@Preview
fun LoadingScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize().background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.oak_sapling),
            contentDescription = "app icon",
            modifier = Modifier.size(160.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.smanega),
                contentDescription = "logo sma ambarawa 1",
                modifier = Modifier.width(60.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.fertinnv),
                contentDescription = "logo fert innovation",
                modifier = Modifier.width(120.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.logoman3),
                contentDescription = "logo man jakarta 3",
                modifier = Modifier.width(60.dp)
            )
        }
    }
}