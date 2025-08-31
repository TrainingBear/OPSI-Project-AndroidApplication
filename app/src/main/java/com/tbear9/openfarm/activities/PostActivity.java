package com.tbear9.openfarm.activities;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
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
import com.trbear9.plants.api.CustomParameters;
import com.trbear9.plants.api.Parameters;
import com.tbear9.openfarm.databinding.ActivityPostBinding;
import com.trbear9.plants.api.SoilParameters;
import com.trbear9.plants.api.UserVariable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class PostActivity extends AppCompatActivity implements Listener {
    private ActivityPostBinding binding;
    ActivityResultLauncher<String> perm = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
        boolean[] allGranted = {true};
            if (result) {
                Log.i("Permission", "Permission granted");
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                Log.w("Permission", "Permission denied");
                allGranted[0] = false;
            }
        ;
        if(!allGranted[0]) finish();
    });
    ActivityResultLauncher<String> gallery = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            try {
                if(uri == null) {
                    Toast.makeText(this, "Image not loaded", Toast.LENGTH_SHORT);
                    return;
                }
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
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
        Log.i("Image", "Image loaded with camera");
    });

    private CameraSelector camSel = CameraSelector.DEFAULT_BACK_CAMERA;
    private UserVariable variable = UserVariable.builder().build();
    private ProcessCameraProvider cameraProvider;
    private Location location;
    private Map<String, Parameters> params = new HashMap<>();
    private static int page = 1;

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
        binding.page1.setVisibility(GONE);
        binding.page2.setVisibility(GONE);
        show();
        location = getIntent().getParcelableExtra("location");
    }

    private void show(){
        if(page==1) {
            binding.page1.setVisibility(VISIBLE);
            binding.openCamera.setOnClickListener(v -> {
                Log.i("Post", "Camera launched");
                perm.launch(Manifest.permission.CAMERA);
                camera.launch(null);
            });

            binding.openGalery.setOnClickListener(v -> {
                Log.i("Post", "Gallery launched");
                perm.launch(Manifest.permission.READ_MEDIA_IMAGES);
                gallery.launch("image/*");
            });
            SoilParameters.builder().pH(Float.MAX_VALUE).build();
        }
        if(page==2) {
            binding.page2.setVisibility(VISIBLE);
        }
    }

    private void hide(){
        if(page==1) {
            binding.page1.setVisibility(GONE);
        }
        if(page==2) binding.page2.setVisibility(GONE);
    }
    @Override protected void onDestroy() {super.onDestroy();binding = null;}@Override protected void onStop() {super.onStop();binding = null;}public void next(){hide();page++;show();}public void back(){hide();page--;show();}
}