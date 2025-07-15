package com.tbear9.openfarm;

import com.google.mlkit.common.model.LocalModel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.custom.CustomImageLabelerOptions;

public final class TFLITE {
    public static ImageLabeler getTest() {
        LocalModel localModel =
                new LocalModel.Builder()
                .setAssetFilePath("model.tflite")
                .build();
        CustomImageLabelerOptions options = new CustomImageLabelerOptions.Builder(localModel)
                .build();
        return ImageLabeling.getClient(options);
    }
}
