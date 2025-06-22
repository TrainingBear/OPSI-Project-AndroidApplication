package com.TBear9.openfarm.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.TBear9.openfarm.Util;
import com.TBear9.openfarm.databinding.DevBinding;
import com.TBear9.openfarm.ml.MyModel;
import com.bumptech.glide.Glide;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.IOException;

public class DevActivity extends AppCompatActivity {
    private DevBinding binding;
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
        Util.debug("created temp file at ", file.getAbsolutePath());
        Uri uri = FileProvider.getUriForFile(DevActivity.this, getPackageName() + ".fileprovider", file);
        ActivityResultLauncher<Intent> activity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Glide.with(DevActivity.this).load(uri).into(binding.imageView);
                    }
                }
        );
        binding.openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(DevActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(DevActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
                }else{
                    Util.debug("Camera button pressed");
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    activity.launch(intent);
                }
            }
        });
        EdgeToEdge.enable(this);
    }

    public void test(){
        try {
            MyModel model = MyModel.newInstance(this);

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.UINT8);
            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            ModelEdgetpu.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }
}