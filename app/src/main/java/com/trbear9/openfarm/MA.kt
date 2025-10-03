package com.trbear9.openfarm

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.LocationServices
import com.trbear9.internal.Data
import com.trbear9.openfarm.activities.ResultScreen
import com.trbear9.openfarm.activities.SoilStats
import com.trbear9.plants.CsvHandler
import com.trbear9.plants.api.GeoParameters
import com.trbear9.plants.api.Response
import com.trbear9.plants.api.SoilParameters
import com.trbear9.plants.api.UserVariable
import com.trbear9.plants.api.blob.Plant
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow

class MA : AppCompatActivity() {
    companion object {
        var response: Flow<Response>? = null
        var loaded by mutableStateOf(false)
        var plants: SnapshotStateMap<Int, MutableList<Plant>> = mutableStateMapOf()
        var plantByCategory = SnapshotStateMap<String, SnapshotStateMap<Int, MutableList<Plant>>>()
        var url by mutableStateOf("null")
        var geo = GeoParameters()
        var soil = SoilParameters()
        var pH: Float? = null
        var image: Bitmap? = null
        var started = false;
    }
    var cam: Boolean = false
    var gps: Boolean = false

    private val imgCapture = ImageCapture.Builder().build()

    private val perm = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CsvHandler.load(this)
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Preview
    @Composable
    fun Home(nav:NavController? = null) {
        val context = LocalContext.current
        var scroll = rememberScrollState()
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
                            nav?.navigate("result")
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
                            Row{
//                                Column(
//                                    verticalArrangement = Arrangement.Center,
//                                    horizontalAlignment = Alignment.CenterHorizontally,
//                                    modifier = Modifier.padding(top = 20.dp, bottom = 20.dp)
//                                        .fillMaxHeight()
//                                    ,
//                                ) {
//                                    Text(
//                                        text = "Belikan kami",
//                                        fontWeight = FontWeight.Light,
//                                        fontSize = 16.sp
//                                    )
//                                    Text(
//                                        text = "segelas kopi!",
//                                        fontWeight = FontWeight.Light,
//                                        fontSize = 16.sp
//                                    )
//                                }
                                Icon(
                                    imageVector = Icons.Default.Coffee,
                                    tint = Color.Black,
                                    contentDescription = "Buy us a coffe!",
                                    modifier = Modifier.padding(top = 20.dp, bottom = 20.dp)
                                        .size(30.dp)
                                        .clickable {
                                        }
                                )
                            }
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
                        Box(
                            modifier = Modifier.wrapContentSize()
                                .clickable {
                                    nav?.navigate("about")
                                }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Image(
                                    painter = painterResource(id = R.drawable.oak_sapling),
                                    contentDescription = "Help",
                                    modifier = Modifier.padding()
                                        .size(40.dp)
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
                    }
                )
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .background(Color(0xFF6FAD4F))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.verticalScroll(scroll)
                ) {
                    TextField(
                        value = "",
                        onValueChange = {},
                        label = {
                            Row {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Cari tanaman apa aja di sini",
                                    fontWeight = FontWeight.Light
                                )
                            }
                        },
                        modifier = Modifier
                            .height(70.dp)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                    )
                    Text(
                        text = "Open Farm",
                        fontWeight = FontWeight.Bold,
                        fontSize = 40.sp,
                        fontFamily = FontFamily.SansSerif,
                        modifier = Modifier.padding(top = 20.dp)
                    )
                    Text(
                        text = "Teman belajar bertani anda",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp,
                        fontFamily = FontFamily.SansSerif,
                        modifier = Modifier.padding(top = 5.dp, start = 30.dp, end = 30.dp)
                    )
                    Text(
                        text = "kapan saja dan dimana saja!",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp,
                        fontFamily = FontFamily.SansSerif,
                        modifier = Modifier.padding(start = 30.dp, end = 30.dp)
                    )
                    Box(
                        modifier = Modifier
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
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 30.dp, end = 30.dp, top = 30.dp)
                            .aspectRatio(16 / 3f)
                            .clip(RoundedCornerShape(10.dp))
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
                            containerColor = Color.Gray
                        )
                    ) {
                        Text(
                            text = "Scan tanahmu!",
                            fontSize = 35.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun App() {
        var nav = rememberNavController()
        val scope = rememberCoroutineScope()
        var job: Job? = null
        NavHost(navController = nav, startDestination = "home") {
            composable("camera") {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Camera") },
                            navigationIcon = {
                                IconButton(onClick = {nav.navigate("home")}) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.LightGray,
                            )
                        )
                    }
                ) {
                    CameraActivity(it, onClick = {
                        nav.navigate("soil")
                    })
                }
            }
            composable("soil") {
                SoilActivity(nav, onClick = { (pH, depth) ->
                    {
                        val variable = UserVariable()
                        try {
                            if(pH != -1){
                                val soil = SoilParameters()
                                soil.pH = pH
                                MA.soil = soil
                            }
                        } catch (e: NumberFormatException) {
                            Toast.makeText(
                                this@MA,
                                "Invalid pH value of ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        plants.clear()
                        variable.geo.rainfall = 2000.0
                        variable.geo.min = 18.0
                        variable.image = image
                        response = Data.process(this@MA, variable)

                        Toast.makeText(this@MA, "finished", Toast.LENGTH_SHORT)
                            .show()
                        Util.debug("Job has been finished!")

                        nav.navigate("result")
                    }
                })
            }
            composable("result") {
                ResultScreen(response, onBack = { nav.navigate("soil") }, nav)
            }
            composable("tanah") {
                SoilStats(nav)
            }
            composable("home") {
                Home(nav)
            }
            composable("tutor") {
//                AndroidView(
//                    factory = { ctx ->
//                        // 👇 Inflate your XML layout
//                        LayoutInflater.from(ctx)
//                            .inflate(R.layout.activity_tutorial, null, false)
//                    },
//                    update = { view ->
//                        // You can access child views here
//                        val button = view.findViewById<Button>(R.id.tombolKembali)
//
//                        button.setOnClickListener {
//                            nav.navigate("home")
//                        }
//                    }
//                )
            }
            composable("about") {
//                AndroidView(
//                    factory = { ctx ->
//                        LayoutInflater.from(ctx)
//                            .inflate(R.layout.activity_tutorial, null, false)
//                    },
//                    update = { view ->
//                        val button = view.findViewById<Button>(R.id.buttonBackmenuabout)
//
//                        button.setOnClickListener {
//                            nav.navigate("home")
//                        }
//                    }
//                )
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
    fun SoilActivity(nav: NavController? = null, onClick: (pH: Float, depth: Int) -> Unit? = {a, b ->}) =
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Soil") },
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
                        containerColor = Color.LightGray,
                    )
                )
            }) { padding ->
            Box(modifier = Modifier
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
                    var pH by remember { mutableStateOf<Float>() }
                    var depth by remember { mutableStateOf(40) }

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
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                    )
                    OutlinedTextField(
                        value = depth,
                        onValueChange = { input ->
                            if (input.all { padding.isDigit() || (padding == ',' || padding == '.') }) {
                                depth = input.toInt()
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

    @Composable
    fun CameraActivity(innerPadding: PaddingValues, onClick: () -> Unit) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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