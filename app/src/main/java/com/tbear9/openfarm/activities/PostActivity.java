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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.location.LocationServices;
import com.tbear9.openfarm.Fragments.PostPageNav.Listener;
import com.trbear9.plants.PlantClient;
import com.trbear9.plants.api.GeoParameters;
import com.tbear9.openfarm.databinding.ActivityPostBinding;
import com.trbear9.plants.api.Response;
import com.trbear9.plants.api.UserVariable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class PostActivity extends AppCompatActivity implements Listener {
    private ActivityPostBinding binding;
    private UserVariable variable;
    private final static ObjectMapper objectMapper = new ObjectMapper();
    private static final PlantClient client =
            new PlantClient(new String[]{"TrainingBear/84d0e105aaabce26c8dfbaff74b2280e"}, 200_000);
    private static int page = 1;

    ActivityResultLauncher<String> perm;
    ActivityResultLauncher<String> gallery;
    ActivityResultLauncher<Void> camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityPostBinding binding = ActivityPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        this.binding = binding;
        variable = new UserVariable();
        camera = registerForActivityResult
            (new ActivityResultContracts.TakePicturePreview(), result -> {
                if(result == null){
                    throw new RuntimeException("Image not loaded");
                }
                if(binding == null){
                    throw new RuntimeException("The binding is null!!!!");
                }
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                result.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                binding.image.setImageBitmap(result);
                variable.setImage(stream.toByteArray());
                variable.setFilename(System.currentTimeMillis()+".jpg");
            });
        gallery = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {try {
                if(uri == null) {
                    Toast.makeText(this, "Image not loaded", Toast.LENGTH_SHORT).show();
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
            }}
        );
        perm = registerForActivityResult(
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
            }}
        );
        binding.page1.setVisibility(GONE);
        binding.openCamera.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED)
                camera.launch(null);
            else
                perm.launch(Manifest.permission.CAMERA);
        });
        next();
    }

    private void onNext(){
        if(page==1) { // page 1 == next
            binding.page1.setVisibility(VISIBLE);
        }
        if(page==2) {
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
    }
}