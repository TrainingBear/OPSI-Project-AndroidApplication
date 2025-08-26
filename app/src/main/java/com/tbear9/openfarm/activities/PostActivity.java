package com.tbear9.openfarm.activities;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.tbear9.openfarm.R;
import com.tbear9.openfarm.Util;
import com.tbear9.openfarm.api.Parameters;
import com.tbear9.openfarm.api.UserVariable;
import com.tbear9.openfarm.databinding.ActivityPostBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class PostActivity extends AppCompatActivity {
    private ProcessCameraProvider cameraProvider;
    CameraSelector camSel = CameraSelector.DEFAULT_BACK_CAMERA;
    ActivityPostBinding binding;
    UserVariable variable;
    ActivityResultLauncher<String> perm = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if(isGranted){
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    ActivityResultLauncher<String> gallery = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
        perm.launch(Manifest.permission.READ_MEDIA_IMAGES);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                binding.image.setImageBitmap(bitmap);
                binding.image.setVisibility(VISIBLE);
                binding.image.setOnClickListener(v -> {
                    binding.image.setVisibility(GONE);
                });
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                variable.setImage(stream.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    ActivityResultLauncher<Void> camera = registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), result -> {
        perm.launch(Manifest.permission.CAMERA);
        binding.image.setImageBitmap(result);
        binding.image.setVisibility(VISIBLE);
        binding.image.setOnClickListener(v -> {
            binding.image.setVisibility(GONE);
        });
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_post);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.openCamera.setOnClickListener(v -> camera.launch(null));
        binding.openGalery.setOnClickListener(v -> gallery.launch("image/*"));
    }

    private void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
    }
    private void initializeCam(){
        getPerms();

        ListenableFuture<ProcessCameraProvider> camProviderFuture = ProcessCameraProvider.getInstance(this);
        camProviderFuture.addListener(()->{
            Preview preview = new Preview.Builder().build();
            preview.setSurfaceProvider(binding.preview.getSurfaceProvider());
            try {
                this.cameraProvider = camProviderFuture.get();
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, camSel, preview);
            } catch (Exception e) {
                e.printStackTrace();
            }}, ContextCompat.getMainExecutor(this));
        binding.cebret.setOnClickListener(v -> cebret());
        binding.cebret.setVisibility(VISIBLE);
        binding.preview.setVisibility(VISIBLE);
    }
    private void cebret(){
        ImageCapture capture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ImageCapture.OutputFileOptions options = new ImageCapture.OutputFileOptions.Builder(buffer).build();
        cameraProvider.unbindAll();
        cameraProvider.bindToLifecycle(this, camSel, capture);
        binding.cebret.setVisibility(INVISIBLE);
        binding.preview.setVisibility(INVISIBLE);
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
                variable.setImage(buffer.toByteArray());
                cameraProvider.unbindAll();
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Util.debug("ODEBUG", exception.getMessage());
                exception.printStackTrace();
            }
        });
    }

    private void getPerms(){
        if(ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }
}