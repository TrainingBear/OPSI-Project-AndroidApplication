package com.tbear9.openfarm.activities;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.util.concurrent.ListenableFuture;
import com.tbear9.openfarm.Util;
import com.tbear9.openfarm.databinding.DevBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DevActivity extends AppCompatActivity {
    private DevBinding binding;
    private ExecutorService cameraExecutor;
    CameraSelector camSel = CameraSelector.DEFAULT_BACK_CAMERA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DevBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        File images = new File(getCacheDir(), "images");
        if(images.mkdirs()) Util.debug("Created images directory");
        File file = null;
        try {
            file = File.createTempFile("captured_", ".jpng", images);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        getPerms();

        binding.imageCaptureButton.setOnClickListener( e -> poto());
        binding.videoCaptureButton.setOnClickListener( e -> rekam());
        binding.button2.setOnClickListener( e -> {
            binding.viewFinder.setVisibility(binding.viewFinder.getVisibility() == VISIBLE ? INVISIBLE : VISIBLE);
        });
        binding.viewFinder.setVisibility(INVISIBLE);
        cameraExecutor = Executors.newSingleThreadExecutor();
        EdgeToEdge.enable(this);
    }

    private ProcessCameraProvider cameraProvider;
    private void poto(){
//        getPerms();
//        binding.viewFinder.setVisibility(VISIBLE);
//        com.google.common.util.concurrent.ListenableFuture<ProcessCameraProvider> camProviderFuture = ProcessCameraProvider.getInstance(this);
//        camProviderFuture.addListener(()->{
//            Preview preview = new Preview.Builder().build();
//            preview.setSurfaceProvider(binding.viewFinder.getSurfaceProvider());
//            try {
//                this.cameraProvider = camProviderFuture.get();
//                cameraProvider.unbindAll();
//                cameraProvider.bindToLifecycle(this, camSel, preview);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }}, ContextCompat.getMainExecutor(this));
//        binding.button1.setOnClickListener(e -> cebret());
//        binding.button1.setVisibility(VISIBLE);
    }
    private void cebret(){
        ImageCapture capture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ImageCapture.OutputFileOptions options = new ImageCapture.OutputFileOptions.Builder(buffer).build();
        cameraProvider.unbindAll();
        cameraProvider.bindToLifecycle(this, camSel, capture);
        binding.button1.setVisibility(INVISIBLE);
        binding.viewFinder.setVisibility(INVISIBLE);
        capture.takePicture(options, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback(){
            @Override
            public void onCaptureStarted() {
                ImageCapture.OnImageSavedCallback.super.onCaptureStarted();
            }

            @Override
            public void onCaptureProcessProgressed(int progress) {
                ImageCapture.OnImageSavedCallback.super.onCaptureProcessProgressed(progress);
            }

            @Override
            public void onPostviewBitmapAvailable(@NonNull Bitmap bitmap) {
                ImageCapture.OnImageSavedCallback.super.onPostviewBitmapAvailable(bitmap);
            }

            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                byte[] byteArray = buffer.toByteArray();
                cameraProvider.unbindAll();
                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                binding.imageView.setImageBitmap(bitmap);
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Util.debug("ODEBUG", exception.getMessage());
                exception.printStackTrace();
            }
        });
    }
    private void rekam(){}

    private void getPerms(){
        if(ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

}