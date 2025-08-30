package com.tbear9.openfarm.activities;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.tbear9.openfarm.Fragments.PostPageNav.Listener;
import com.tbear9.openfarm.R;
import com.tbear9.openfarm.api.UserVariable;
import com.tbear9.openfarm.databinding.ActivityPostBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class PostActivity extends AppCompatActivity implements Listener {
    private ProcessCameraProvider cameraProvider;
    CameraSelector camSel = CameraSelector.DEFAULT_BACK_CAMERA;
    ActivityPostBinding binding;
    UserVariable variable = UserVariable.builder().build();
    ActivityResultLauncher<String[]> perm = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
        boolean[] allGranted = {true};
        result.forEach((permission, isGranted) -> {
            if (isGranted) {
                Log.i("Permission", "Permission granted");
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                Log.w("Permission", "Permission denied");
                allGranted[0] = false;
            }
        });
        if(!allGranted[0]) finish();
    });
    ActivityResultLauncher<String> gallery = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            try {
                if(uri == null) {
                    Toast.makeText(this, "Image not loaded", Toast.LENGTH_SHORT);
                    return;
                }
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                binding.image.setImageBitmap(bitmap);
                binding.image.setVisibility(VISIBLE);
                binding.image.setOnClickListener(v -> {
                    binding.image.setVisibility(GONE);
                });
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                variable = UserVariable.builder()
                        .image(stream.toByteArray())
                        .build();
            } catch (IOException e) {
                e.printStackTrace();
            }
                Log.i("Image", "Image loaded");
        }
        );
    ActivityResultLauncher<Void> camera = registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), result -> {
        binding.image.setImageBitmap(result);
        binding.image.setVisibility(VISIBLE);
        binding.image.setOnClickListener(v -> {
            binding.image.setVisibility(GONE);
        });
        Log.i("Image", "Image loaded with camera");
    });

    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        perm.launch(new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.READ_MEDIA_IMAGES
        });

        binding.openCamera.setOnClickListener(v -> {
            Log.i("Camera", "Camera launched");
            Toast.makeText(this, "Camera launched", Toast.LENGTH_SHORT).show();
            camera.launch(null);
        });
        binding.openGalery.setOnClickListener(v -> {
            Log.i("Gallery", "Gallery launched");
            Toast.makeText(this, "Gallery launched", Toast.LENGTH_SHORT).show();
            gallery.launch("image/*");
        });

    }

    @Override
    public void next(){
//        hide();
//        page++;
        show();
    }
    @Override
    public void back(){
        hide();
//        page--;
//        show();
    }

    private void show(){
        switch (page){
            case 1 :
                binding.openCamera.setVisibility(VISIBLE);
                binding.page1.setVisibility(VISIBLE);
                Toast.makeText(this, "Shown", Toast.LENGTH_SHORT).show();
        }
    }

    private void hide(){
        switch(page){
            case 1:
                binding.openCamera.setVisibility(GONE);
                binding.page1.setVisibility(GONE);
                Toast.makeText(this, "Hidden", Toast.LENGTH_SHORT).show();
        }
    }
}