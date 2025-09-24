package com.tbear9.openfarm.activities;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.location.LocationServices;
import com.tbear9.openfarm.Fragments.PostPageNav.Listener;
import com.tbear9.openfarm.R;
import com.trbear9.plants.PlantClient;
import com.trbear9.plants.api.GeoParameters;
import com.tbear9.openfarm.databinding.ActivityPostBinding;
import com.trbear9.plants.api.Response;
import com.trbear9.plants.api.SoilParameters;
import com.trbear9.plants.api.UserVariable;
import com.trbear9.plants.api.CustomParameters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class PostActivity extends AppCompatActivity implements Listener {
    private ActivityPostBinding binding;
    private UserVariable variable;
    private final static ObjectMapper objectMapper = new ObjectMapper();
    private static int page = 1;

    ActivityResultLauncher<String> perm = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> {
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
    ActivityResultLauncher<String> gallery = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {try {
                if(uri == null) {
                    Toast.makeText(this, "Image not loaded", Toast.LENGTH_SHORT);
                    return;
                }
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                bitmap = Bitmap.createScaledBitmap(bitmap, 320, 320, true);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                variable.setImage(stream.toByteArray());
                binding.image.setImageBitmap(bitmap);
            } catch (IOException e) {
                Log.e("I/O", e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
        );
    ActivityResultLauncher<Void> camera = registerForActivityResult
            (new ActivityResultContracts.TakePicturePreview(), result -> {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Bitmap scalledResult = Bitmap.createScaledBitmap(result, 320, 320, true);
                scalledResult.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                binding.image.setImageBitmap(scalledResult);
                variable.setImage(stream.toByteArray());
    });



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
        variable = new UserVariable();
        binding.page1.setVisibility(GONE);
        binding.page2.setVisibility(GONE);
        next();
    }

    private void onNext(){
        if(page==1) { // page 1 == next
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

    private void getLocation(){
        perm.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        perm.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Tolong aktifkan dan izikan GPS", Toast.LENGTH_SHORT).show();
            this.finish();
        }

        GeoParameters geo= new GeoParameters();
        LocationServices.getFusedLocationProviderClient(this)
                .getLastLocation()
                .addOnSuccessListener(location -> {
                    if(location != null){
                        geo.setLatitude(location.getLatitude());
                        geo.setLatitude(location.getLongitude());
                    }
                });
        variable.add(geo);
    }
    private void onPrevious(){
        if(page==1) { // page 1 == previous
            binding.page1.setVisibility(GONE);
        }
        if(page==2) binding.page2.setVisibility(GONE);
    }
    @Override protected void onDestroy() {super.onDestroy();binding = null;}@Override protected void onStop() {super.onStop();binding = null;}
    public void next(){
        onNext();
        page++;
        onPrevious();
    }
    public void back(){
        onPrevious();
        page--;
        onNext();
    }

    public void onFinish() {
        Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show();
        if (variable.getImage() == null) {
            throw new NullPointerException("Image is null");
        }
        getLocation();
        Response produk = PlantClient.sendPacket(variable, "PROCESS");
        try {
            Log.i("JSON", objectMapper.readTree(objectMapper.writeValueAsString(produk)).toPrettyString());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}