package com.tbear9.openfarm.activities;

import static android.view.View.VISIBLE;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
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

import com.google.ai.edge.litert.LiteRtException;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.tbear9.openfarm.R;
import com.tbear9.openfarm.TFLITE;
import com.tbear9.openfarm.Util;
import com.tbear9.openfarm.databinding.DevBinding;
import com.bumptech.glide.Glide;

import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DevActivity extends AppCompatActivity {
    private DevBinding binding;
    private ExecutorService cameraExecutor;

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
        getPermis();

        binding.imageCaptureButton.setOnClickListener( e -> poto());
        binding.videoCaptureButton.setOnClickListener( e -> rekam());

        cameraExecutor = Executors.newSingleThreadExecutor();
//        Util.debug("created temp file at ", file.getAbsolutePath());
//        Uri uri = FileProvider.getUriForFile(DevActivity.this, getPackageName() + ".fileprovider", file);
//        ActivityResultLauncher<Intent> activity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
//                result -> {
//                    if (result.getResultCode() == RESULT_OK) {
//                        Glide.with(DevActivity.this).load(uri).into(binding.imageView);
//                        binding.welcomemsg.setText("Camera has been executed!");
//                        try {
//                            process(uri);
//                        }catch (IOException e){
//                            binding.welcomemsg.setText("Error: IOException");
//                            e.printStackTrace();
//                        }
//                    }
//                }
//        );
//        binding.openCamera.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(ContextCompat.checkSelfPermission(DevActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
//                    ActivityCompat.requestPermissions(DevActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
//                }else{
//                    Util.debug("Camera button pressed");
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                    activity.launch(intent);
//                }
//            }
//        });
        EdgeToEdge.enable(this);
    }

    private void poto(){
        getPermis();
        binding.viewFinder.setVisibility(VISIBLE);
    }
    private void rekam(){}

    public void process(Uri uri) throws IOException {
        try (Interpreter interpreter = new Interpreter(Util.loadModelFile(getAssets(), "model_unquant.tflite"));){
            float[][] output = new float[0][3];
            ByteBuffer inputBuffer = InputImage.fromFilePath(this, uri).getByteBuffer();
            interpreter.run(inputBuffer, output);
            for (float[] floats : output) {
                for (float aFloat : floats) {
                    Util.debug("ODEBUG", aFloat, "tak tau");
                }
            }
        }
//        TFLITE.test.process(InputImage.fromFilePath(this, uri)).addOnSuccessListener(result -> {
//            binding.welcomemsg.setText("Result: "+result.get(0).getText()+" with confidence "+result.get(0).getConfidence());
//            for (ImageLabel label : result)
//                Util.debug("ODEBUG", label.getText()," = ", label.getConfidence());
//        }).addOnFailureListener(e->{
//            binding.welcomemsg.setText("Error: "+e.getMessage());
//            Util.debug("ODEBUG", e.getMessage());
//            e.printStackTrace();
//        });
    }

    private void getPermis(){
        if(ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

}