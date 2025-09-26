package com.tbear9.openfarm.activities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
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
import com.tbear9.openfarm.Util
import com.trbear9.plants.PlantClient
import com.trbear9.plants.api.GeoParameters
import com.trbear9.plants.api.Response
import com.trbear9.plants.api.SoilParameters
import com.trbear9.plants.api.UserVariable
import com.trbear9.plants.api.blob.Plant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class Camera : AppCompatActivity() {

    val client: PlantClient = PlantClient("TrainingBear/84d0e105aaabce26c8dfbaff74b2280e", size = 200_000)
    var response: Response? = null
    var loaded by mutableStateOf(false)
    var plants: SnapshotStateMap<Int, List<Plant>> = mutableStateMapOf()
    val variable = UserVariable();
    val imgCapture = ImageCapture.Builder()
        .build()

    private val perm = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {}


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
        var navController = rememberNavController()
        val scope = rememberCoroutineScope()
        var job: Job? = null
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
                        response = null;
                        job?.cancel()
                        loaded = false
                        plants.clear()
                        Util.debug("Launching job coroutine... ")
                        job = scope.launch {
                            Toast.makeText(this@Camera, "Sending data...", Toast.LENGTH_SHORT).show()
                            response = withContext(Dispatchers.IO) {
                                client.sendPacket(variable)
                            }
                            loaded = true
                            for (score in response!!.tanaman.keys) {
                                Util.debug("Loaded $score with size ${response!!.tanaman[score]!!.size}")
                                plants[score] = response!!.tanaman[score]!!
                            }
                            Toast.makeText(this@Camera, "finished", Toast.LENGTH_SHORT).show()
                            Util.debug("Job has been finished!")
                        }
                        navController.navigate("result")
                    })
                }
            }
            composable("result") {
                ResultScreen(plants, loaded)
            }
        }
    }

    @Composable
    fun SoilActivity(innerPadding: PaddingValues, onClick: (number: String?) -> Unit) = Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
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
                            takePhoto(this@Camera, imgCapture, onClick)
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




}
