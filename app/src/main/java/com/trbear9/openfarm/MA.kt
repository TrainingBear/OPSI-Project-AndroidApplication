package com.trbear9.openfarm

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.ScaleGestureDetector
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.LocationServices
import com.trbear9.internal.Data
import com.trbear9.internal.TFService
import com.trbear9.openfarm.activities.Guide
import com.trbear9.openfarm.activities.ResultScreen
import com.trbear9.openfarm.activities.SearchLayout
import com.trbear9.openfarm.activities.SearchResult
import com.trbear9.openfarm.activities.SoilResult
import com.trbear9.openfarm.activities.SoilStats
import com.trbear9.plants.api.GeoParameters
import com.trbear9.plants.api.Response
import com.trbear9.plants.api.SoilParameters
import com.trbear9.plants.api.UserVariable
import kotlinx.coroutines.flow.Flow

class MA : AppCompatActivity() {
    companion object {
        var response: Flow<Response>? = null
        var loaded by mutableStateOf(false)
        var url by mutableStateOf("null")
        var geo = GeoParameters()
        var soil = SoilParameters()
        var pH: Float? = null
        var image: Bitmap? = null
        val searchResult = SearchResult()
        val soilResult = SoilResult()
    }

    var cam: Boolean = false
    var gps: Boolean = false

    private val imgCapture = ImageCapture.Builder().build()

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
                            nav?.navigate("home")
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = selected == 1,
                        onClick = {
                            selected = 1
                            nav?.navigate("result_soil")
                        },
                        icon = { Icon(Icons.Default.Park, contentDescription = "Hasil") },
                        label = { Text("Tanaman") }
                    )
                    NavigationBarItem(
                        selected = selected == 2,
                        onClick = {
                            selected = 2
                            nav?.navigate("tanah")
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
                                            .clickable{nav?.navigate("help")}
                                    )
                                    Column(
                                        horizontalAlignment = Alignment.Start,
                                        verticalArrangement = Arrangement.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Text(
                                            text = "OpenFarm",
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1C8604),
                                            fontSize = 24.sp
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
                                                nav?.navigate("tutor")
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
                                    "cara merawat tanah di lahan anda.\n" +
                                    "Sebagai tanda dukungan, berikan kami secangkir kopi:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(10.dp)
                        )
                        Row(horizontalArrangement = Arrangement.Center) {
                            Button(
                                onClick = {coffe = false},
                                modifier = Modifier.padding(10.dp)
                                    .wrapContentSize(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.DarkGray
                                )
                            ){
                                Text(
                                    text = "Tidak, terimakasih!",
                                    fontSize = 10.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, "https://saweria.co/Kujatic".toUri())
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.padding(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFFFF9100
                                    )
                                )
                            ) {
                                Text(
                                    text = "DONASI",
                                    fontSize = 18.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold
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
                            onClick = {nav?.navigate("search")},
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
                                text = "Cari tanaman apa aja di sini",
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1C8604),
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
                Box(modifier = Modifier.fillMaxWidth().weight(3f)) {
                    Image(
                        painter = painterResource(id = R.drawable.background_opsi_mainactivity_rescaled),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }
                Box(Modifier.fillMaxWidth().weight(1f)) {
                    BoxWithConstraints(Modifier.fillMaxSize()) {
                        val dynamicFontSize = (maxHeight.value / 3).sp
                        Button(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(start = 30.dp, end = 30.dp, bottom = 20.dp)
                                .wrapContentSize(),
                            onClick = {
                                perm.launch(arrayOf(Manifest.permission.CAMERA))
                                if (ContextCompat.checkSelfPermission(
                                        this@MA,
                                        Manifest.permission.CAMERA
                                    ) != PackageManager.PERMISSION_GRANTED
                                ) else {
                                    cam = true
                                    Toast.makeText(
                                        this@MA,
                                        "Camera permission granted!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    if (gps) nav?.navigate("camera")
                                    else getLocation()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2E7D32)
                            )
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center
                                ,modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = "Scan tanahmu!",
                                    fontSize = dynamicFontSize,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    modifier = Modifier.fillMaxSize(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun App() {
        var nav = rememberNavController()
        NavHost(navController = nav, startDestination = "home") {
            composable("camera") {
                CameraActivity(nav, onClick = {
                    nav.navigate("soil")
                })
            }
            composable("soil") {
                SoilActivity(nav, onClick = { pH: Float?, depth: Int? ->
                    val variable = UserVariable()
                    val soil = SoilParameters()
                    if (pH != null) soil.pH = pH
                    if (depth != null) soil.numericDepth = depth
                    MA.soil = soil
                    variable.geo.rainfall = 2000.0
                    variable.geo.min = 18.0
                    variable.image = image
                    soilResult.collected = false
                    MA.response = Data.process(variable, soilResult)
                    Toast.makeText(this@MA, "finished", Toast.LENGTH_SHORT)
                        .show()
                    Util.debug("Job has been finished!")
                    soilResult.res = MA.response
                    nav.navigate("result_soil")
                })
            }
            composable("result") {
                
            }
            composable("result_soil") {
                ResultScreen(
                    soilResult = soilResult,
                    onBack = { nav.navigate("soil") },
                    nav = nav
                )
            }
            composable("search_result") {
                ResultScreen(
                    searchResult = searchResult,
                    onBack = { nav.navigateUp() },
                    nav = nav
                )
            }
            composable("search"){
                SearchLayout(
                    searchResult = searchResult,
                    nav = nav
                )
            }
            composable("tanah") {
                SoilStats(nav)
            }
            composable("home") {
                Home(nav)
            }
            composable("help") {
                Guide(nav)
            }
            composable("about") {

            }
        }
    }

    private fun takePhoto(context: Context, imageCapture: ImageCapture, onFinish: () -> Unit) {
        imageCapture.takePicture(
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onError(exc: ImageCaptureException) {
                    exc.printStackTrace()
                }

                override fun onCaptureSuccess(image: ImageProxy) {
                    MA.image = image.toBitmap()
                    onFinish()
                    image.close()
                }
            }
        )
    }

    private fun getLocation() {
        perm.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        );
        if (ActivityCompat.checkSelfPermission(
                this,
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
        MA.geo = geo;
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    @Preview
    fun SoilActivity(
        nav: NavController? = null,
        onClick: (pH: Float?, depth: Int?) -> Unit? = { a, b -> }) {
        var pH by remember { mutableStateOf("") }
        var depth by remember { mutableStateOf("40") }
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(
                        text = "Soil",
                        fontWeight = FontWeight.Bold,
                    ) },
                    navigationIcon = {
                        IconButton(onClick = {
                            nav?.navigate("camera")
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
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color.White)
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center),
                ) {
                    Text(
                        text = "Nilai pH lebih baik di kosongkan jika tak memiliki pH meter",
                        fontSize = 20.sp,
                        color = Color.Black,
                        style = TextStyle.Default,
                        textAlign = TextAlign.Center
                    )
                    OutlinedTextField(
                        value = pH,
                        onValueChange = { input ->
                            if (input.all { it.isDigit() || (it == ',' || it == '.') }) {
                                pH = input
                            }
                        },
                        label = { Text("pH tanah") },
                        placeholder = { Text("") },
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
                        placeholder = { Text("40") },
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
                        onClick = { onClick(pH, depth) },
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


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    @Preview
    fun CameraActivity(nav: NavController? = null,onClick: () -> Unit = {}) {
        Scaffold(
            topBar =
                {
                    TopAppBar(
                        title = { Text(
                            text="Camera",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ) },
                        navigationIcon = {
                            IconButton(onClick = { nav?.navigate("home") }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Black,
                        )
                    )
                }
                ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp, vertical = 24.dp),
                        verticalArrangement = Arrangement.Top
                    ) {
                        Text(
                            text = "Foto tanahmu!\n",
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            fontSize = 30.sp,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        Text(
                            text =
                                "Pastikan tanah tidak tertutupi objek apapun \n" +
                                        "seperti batu, ranting, rumput, dsb",
                            fontSize = 16.sp,
                            textAlign = TextAlign.Start,
                            color = Color.White,
                        )

                    }
                    Box(
                        modifier = Modifier
                            .size(336.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .align(Alignment.Center)
                            .aspectRatio(1f)
                    ) {
                        CameraPreviewView(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        drawOutline(15f, 60f)
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .background(Color.Green.copy(alpha = 0.4f))
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(100.dp)
                                .align(Alignment.Center)
                                .padding(top = 16.dp, bottom = 16.dp),
                            shape = CircleShape,
                            color = Color.Unspecified,
                            shadowElevation = 8.dp,
                            border = BorderStroke(4.dp, Color(0xFF0AF10D)), // blue border
                            onClick = {
                                takePhoto(this@MA, imgCapture, onClick)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = "Circle Button",
                                tint = Color(0xFF2196F3),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Composable
    fun CameraPreviewView(
        lensFacing: Int = CameraSelector.LENS_FACING_BACK,
        modifier: Modifier,
    ) {
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current

        AndroidView(
            modifier = modifier,
            factory = { ctx ->
                PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }
            },
            update = { previewView ->
                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = androidx.camera.core.Preview.Builder().build().apply {
                        surfaceProvider = previewView.surfaceProvider
                    }

                    val cameraSelector = CameraSelector.Builder()
                        .requireLensFacing(lensFacing)
                        .build()

                    try {
                        cameraProvider.unbindAll()
                        val camera = cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            imgCapture,
                            preview
                        )

                        val scaleGestureDetector = ScaleGestureDetector(
                            context,
                            object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                                override fun onScale(detector: ScaleGestureDetector): Boolean {
                                    val currentZoom =
                                        camera.cameraInfo.zoomState.value?.zoomRatio ?: 1f
                                    val scale = detector.scaleFactor
                                    val newZoom = (currentZoom * scale)
                                        .coerceIn(
                                            camera.cameraInfo.zoomState.value?.minZoomRatio ?: 1f,
                                            camera.cameraInfo.zoomState.value?.maxZoomRatio ?: 10f
                                        )
                                    camera.cameraControl.setZoomRatio(newZoom)
                                    return true
                                }
                            }
                        )

                        previewView.setOnTouchListener { _, event ->
                            scaleGestureDetector.onTouchEvent(event)
                            true // consume gesture
                        }

                    }catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, ContextCompat.getMainExecutor(context))
            }
        )
    }

    @Composable
    fun drawOutline(strokeWidth: Float = 8f, length: Float = 40f) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Top-left
            drawLine(Color.Green, Offset(0f, 0f), Offset(length, 0f), strokeWidth)
            drawLine(Color.Green, Offset(0f, 0f), Offset(0f, length), strokeWidth)
            // Top-right
            drawLine(
                Color.Green,
                Offset(size.width - length, 0f),
                Offset(size.width, 0f),
                strokeWidth
            )
            drawLine(
                Color.Green,
                Offset(size.width, 0f),
                Offset(size.width, length),
                strokeWidth
            )
            // Bottom-left
            drawLine(
                Color.Green,
                Offset(0f, size.height - length),
                Offset(0f, size.height),
                strokeWidth
            )
            drawLine(
                Color.Green,
                Offset(0f, size.height),
                Offset(length, size.height),
                strokeWidth
            )
            // Bottom-right
            drawLine(
                Color.Green,
                Offset(size.width - length, size.height),
                Offset(size.width, size.height),
                strokeWidth
            )
            drawLine(
                Color.Green,
                Offset(size.width, size.height - length),
                Offset(size.width, size.height),
                strokeWidth
            )
        }
    }
}