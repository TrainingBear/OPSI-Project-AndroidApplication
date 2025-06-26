package com.tbear9.openfarm;

import android.net.Uri;

import com.google.mlkit.common.model.LocalModel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.custom.CustomImageLabelerOptions;
import com.google.mlkit.vision.vkp.VkpObjectDetectorOptions;

import org.tensorflow.lite.task.core.BaseOptions;
import org.tensorflow.lite.task.vision.detector.ObjectDetector;

public final class TFLITE {
    public static ImageLabeler getTest(Uri uri) {
        LocalModel localModel =
                new LocalModel.Builder()
                .setAssetFilePath("model_unquant.tflite")
                .setUri(uri)
                .build();
        CustomImageLabelerOptions options = new CustomImageLabelerOptions.Builder(localModel)
                .setConfidenceThreshold(0.5f)
                .setMaxResultCount(10)
                .build();
        return ImageLabeling.getClient(options);
    }
}
