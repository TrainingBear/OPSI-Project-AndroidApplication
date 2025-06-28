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

import com.google.common.util.concurrent.ListenableFuture;
import com.tbear9.openfarm.Util;
import com.tbear9.openfarm.databinding.DevBinding;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.task.vision.classifier.Classifications;
import org.tensorflow.lite.task.vision.classifier.ImageClassifier;

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
        binding.button3.setOnClickListener( e -> {
            camSel = camSel == CameraSelector.DEFAULT_BACK_CAMERA? CameraSelector.DEFAULT_FRONT_CAMERA : CameraSelector.DEFAULT_BACK_CAMERA;
            poto();
        });
        binding.viewFinder.setVisibility(INVISIBLE);
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

    private ProcessCameraProvider cameraProvider;
    private void poto(){
        getPerms();
        binding.viewFinder.setVisibility(VISIBLE);
        ListenableFuture<ProcessCameraProvider> camProviderFuture = ProcessCameraProvider.getInstance(this);
        camProviderFuture.addListener(()->{
            Preview preview = new Preview.Builder().build();
            preview.setSurfaceProvider(binding.viewFinder.getSurfaceProvider());
            try {
                this.cameraProvider = camProviderFuture.get();
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, camSel, preview);
            } catch (Exception e) {
                e.printStackTrace();
            }}, ContextCompat.getMainExecutor(this));
        binding.button1.setOnClickListener(e -> cebret());
        binding.button1.setVisibility(VISIBLE);
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
                try {
                    process(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Util.debug("ODEBUG", exception.getMessage());
                exception.printStackTrace();
            }
        });
    }
    private void rekam(){}

    public void process(Bitmap bitmap) throws IOException {
        try (ImageClassifier classifier = ImageClassifier.createFromFile(this, "model.tflite");){
            List<Classifications> result = classifier.classify(TensorImage.fromBitmap(bitmap));
            for (Classifications classifications : result) {
                for (Category category : classifications.getCategories()) {
                    Util.debug("Prediction", category.getLabel() + ": " + category.getScore());
                }
            }
        }
//        TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
//        tensorImage.load(bitmap);
//
//        ImageProcessor imageProcessor = new ImageProcessor.Builder()
//                .add(new ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
//                .add(new NormalizeOp(0.0f, 255.0f))
//                .build();
//
//        tensorImage = imageProcessor.process(tensorImage);
//        Util.debug("ODEBUG", "Processed image with data type : ", tensorImage.getDataType());
//        try (ImageLabeler test = TFLITE.getTest();) {
//            ImageLabeler test = TFLITE.getTest();
//            binding.linearLayout.clearAnimation();
//            LocalModel localModel =
//                    new LocalModel.Builder()
//                            .setAssetFilePath("model.tflite")
//                            .build();
//            TextView textView1 = new TextView(this);
//            textView1.setText(localModel.getAbsoluteFilePath());
//            binding.linearLayout.addView(textView1);
//            Task<List<ImageLabel>> listTask = test.process(bitmap, 0)
//                    .addOnSuccessListener(result -> {
//                        binding.welcomemsg.setText("ODEBUG: " + result.get(0).getIndex() + result.get(0).getText() + " with confidence " + result.get(0).getConfidence());
//                        for (ImageLabel label : result) {
//                            Util.debug("ODEBUG", (Object) ("ODEBUG: " + label.getIndex() + label.getText() + " with confidence " + label.getConfidence()));
//                            TextView textView = new TextView(this);
//                            textView.setText("ODEBUG: " + label.getIndex() + label.getText() + " with confidence " + label.getConfidence());
//                            binding.linearLayout.addView(textView);
//                        }
//                    })
//                    .addOnFailureListener(e -> {
//                        binding.welcomemsg.setText("ODEBUG: " + e.getMessage());
//                        Util.debug("ODEBUG", e.getMessage());
//                        e.printStackTrace();
//                    });
//        }
//        Model1 model = Model1.newInstance(this);
//        TensorImage tensorImage1 = TensorImage.fromBitmap(bitmap);
//        Model1.Outputs output = model.process(tensorImage1.getTensorBuffer());
//        for (float f : output.getOutputFeature0AsTensorBuffer().getFloatArray()){
//            Util.debug("ODEBUG", "ODEBUG: " + f);
//        }
    }

    private void getPerms(){
        if(ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

}