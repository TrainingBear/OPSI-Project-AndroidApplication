package com.tbear9.openfarm.activities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.LocationServices
import com.trbear9.plants.PlantClient
import com.trbear9.plants.api.GeoParameters
import com.trbear9.plants.api.Response
import com.trbear9.plants.api.SoilParameters
import com.trbear9.plants.api.UserVariable
import com.trbear9.plants.api.blob.Plant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class Camera : AppCompatActivity() {
    val client: PlantClient = PlantClient("TrainingBear/84d0e105aaabce26c8dfbaff74b2280e", size = 200_000)
    var response: Response? = null
    val variable = UserVariable();
    val imgCapture = ImageCapture.Builder()
        .build()

    private val perm = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineLocationGranted) {
            Toast.makeText(this, "Fine Location granted!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Fine Location denied!", Toast.LENGTH_SHORT).show()
            finish()
        }

        if (coarseLocationGranted) {
            Toast.makeText(this, "Coarse Location granted!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Coarse Location denied!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
        getLocation()
        perm.launch(arrayOf(Manifest.permission.CAMERA))
        if (ContextCompat.checkSelfPermission
                (this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
        ) finish()
        else {
            Toast.makeText(this, "Camera permission granted!", Toast.LENGTH_SHORT).show()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun App() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "camera") {
            composable("camera") {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Camera") },
                            navigationIcon = {
                                IconButton(onClick = {
                                    finish()
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
                    }
                ) {
                    CameraActivity(it, onClick = {
                        navController.navigate("soil")
                    })
                }
            }
            composable("soil") {
                Scaffold(topBar = {
                    TopAppBar(
                        title = { Text("Soil") },
                        navigationIcon = {
                            IconButton(onClick = {
                                navController.navigate("camera")
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
                }) {

                    val scope = rememberCoroutineScope()

                    SoilActivity(it, onClick = { ph ->
                        try {
                            ph?.let {
                                val soil = SoilParameters()
                                soil.pH = (it.replace(",", ".")).toFloat()
                                variable.add(soil)
                            }
                        } catch (e: NumberFormatException){
                            Toast.makeText(this@Camera, "Invalid pH value of ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                        scope.launch {
                            try {
                                val response = withContext(Dispatchers.IO) {
                                    client.sendPacket(variable)
                                }
                                // back on Main thread, update UI
                                Toast.makeText(this@Camera, "Got response: $response", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Toast.makeText(this@Camera, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                        navController.navigate("result")
                    })
                }
            }
            composable("result") {
                ResultScreen()
            }
        }
    }


    @Composable
    fun PlantCard(
        plantTitle: String = "Monstera Deliciosa",
        plantDesc: String = "Daun besar berlubang, cocok di cahaya tidak langsung.",
        score: Int,
        diff: String,
        panen: String,
        category: String,
        plantImage: Bitmap? = null
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            border = BorderStroke(1.dp, Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                // Plant Image
                if (plantImage == null)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Color.LightGray)
                            .clip(RoundedCornerShape(16.dp))
                    ) {
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = "Gambar tidak tersedia untuk $plantTitle",
                        )
                    }
                else Image(
                    bitmap = plantImage.asImageBitmap(),
                    contentDescription = plantTitle,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                )

                // Plant Title
                Text(
                    text = plantTitle.toString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 12.dp)
                )

                // Plant Description
                Text(
                    text = plantDesc.toString(),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 6.dp)
                )

                // Plant Meta
                Row(
                    modifier = Modifier.padding(top = 5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = Color(0xFFFF9800),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(6.dp)
                    ) {
                        Text(
                            text = category,
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .background(
                                color = scoreToColor(score).copy(alpha = 0.6f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(6.dp)
                    ) {
                        Text(
                            text = "Score: $score",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .background(
                                color = diffToColor(diff).copy(alpha = 0.6f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(6.dp)
                    ) {
                        Text(
                            text = diff,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                }
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .background(
                            color = Color.LightGray,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(6.dp)
                ) {
                    Text(
                        text = "panen: $panen",
                        fontSize = 12.sp
                    )
                }
            }
        }
    }

    fun diffToColor(diff: String): Color {
        return when (diff) {
            "EASY" -> Color.Green
            "MEDIUM" -> Color.Yellow
            "HARD" -> Color.Red
            else -> Color.Gray
        }
    }

    fun scoreToColor(score: Int): Color {
        return when (score) {
            in 0..2 -> Color.Red
            in 3..6 -> Color.Yellow
            in 7..354 -> Color.Green
            else -> Color.Blue
        }
    }


    @Composable
    fun SoilActivity(innerPadding: PaddingValues, onClick: (number: String?) -> Unit) = Box(
        modifier = Modifier.fillMaxSize()
            .padding(innerPadding)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
                .background(Color.White)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
                    .align(Alignment.Center),
            ) {
                var number by remember { mutableStateOf("") }

                Text(
                    text = "Nilai pH lebih baik di kosongkan jika tak memiliki pH meter",
                    fontSize = 20.sp,
                    color = Color.Black,
                    style = TextStyle.Default,
                    textAlign = TextAlign.Center
                )
                OutlinedTextField(
                    value = number,
                    onValueChange = { input ->
                        // only allow digits
                        if (input.all { it.isDigit() }) {
                            number = input
                        }
                    },
                    label = { Text("masukan nilai pH tanah") },
                    placeholder = { Text("3.54") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                )

                Button(
                    onClick = { onClick(number) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 80.dp, vertical = 8.dp)
                ) {
                    Text(text = "Next")
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CameraActivity(innerPadding: PaddingValues, onClick: () -> Unit) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
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
                        modifier = Modifier.fillMaxSize()
                            .padding(16.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    drawOutline(15f, 60f)
                }
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(Color.Green.copy(alpha = 0.4f))
                ) {
                    Surface(
                        modifier = Modifier.size(100.dp)
                            .align(Alignment.Center)
                            .padding(top = 16.dp, bottom = 16.dp),
                        shape = CircleShape,
                        color = Color.Unspecified,
                        shadowElevation = 8.dp,
                        border = BorderStroke(4.dp, Color(0xFF0AF10D)), // blue border
                        onClick = {
                            takePhoto(this@Camera, imgCapture, onClick)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = "Circle Button",
                            tint = Color(0xFF2196F3),
                            modifier = Modifier.fillMaxSize()
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }

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
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            imgCapture,
                            preview
                        )
                    } catch (e: Exception) {
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

    fun takePhoto(context: Context, imageCapture: ImageCapture, onFinish: () -> Unit) {
        imageCapture.takePicture(
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onError(exc: ImageCaptureException) {
                    exc.printStackTrace()
                }

                override fun onCaptureSuccess(image: ImageProxy) {
                    val bitmap = image.toBitmap()

                    // Crop to 320x320
                    val size = 320
                    val cropped = Bitmap.createBitmap(
                        bitmap,
                        (bitmap.width - size) / 2, // center X
                        (bitmap.height - size) / 2, // center Y
                        size,
                        size
                    )

                    val stream = ByteArrayOutputStream();
                    image.toBitmap().compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    variable.setImage(stream.toByteArray(), "${System.currentTimeMillis()}.jpg")
                    Toast.makeText(
                        this@Camera,
                        "Image has been taken with size of ${variable.image!!.size} bytes}",
                        Toast.LENGTH_SHORT
                    ).show()

                    onFinish()
                    image.close()
                }
            }
        )
    }

    fun getLocation(){
        perm.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION));
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Tolong aktifkan dan izikan GPS", Toast.LENGTH_SHORT).show();
            finish();
        }

        val geo = GeoParameters();
        LocationServices.getFusedLocationProviderClient(this)
            .lastLocation
            .addOnSuccessListener {
                if (it != null) {
                    geo.latitude = it.latitude;
                    geo.longtitude = it.longitude;
                }
            };
        variable.add(geo);
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ResultScreen() {
        val plants = remember { mutableMapOf<Int, Plant>() }
        // Example: load first plant on entry
        Scaffold(topBar = {
            TopAppBar(
                title = { Text("Results") },
                navigationIcon = {
                    IconButton(onClick = { finish() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                LazyColumn {
                    items(plants.toList()) {
                        (score, plant) ->
                        PlantCard(
                            plantTitle = plant.commonName,
                            plantDesc = plant.description,
                            plantImage = plant.thumbnail?.let {
                                BitmapFactory.decodeByteArray(it, 0, it.size)
                            },
                            score = score,
                            diff = plant.difficulty,
                            panen = "${plant.min_panen}-${plant.max_panen} hari",
                            category = plant.kategori
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Preview(uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL)
    @Composable
    fun Previewable() {
        Scaffold(topBar = {
            TopAppBar(
                title = { Text("Results") },
                navigationIcon = {
                    IconButton(onClick = { finish() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.LightGray)
            )
        }) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                PlantCard(
                    plantTitle = "Monstera Deliciosa",
                    plantDesc = "Daun besar berlubang, cocok di cahaya tidak langsung.",
                    diff = "HARD",
                    panen = "30-45 hari",
                    score = 90,
                    category = "Tanaman Hias",
                )
            }
        }
    }

}
