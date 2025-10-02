package com.trbear9.internal


import android.content.Context
import android.graphics.Bitmap
import com.trbear9.plants.api.SoilParameters
import org.tensorflow.lite.task.vision.classifier.ImageClassifier
import org.tensorflow.lite.task.vision.classifier.ImageClassifier.ImageClassifierOptions
import org.tensorflow.lite.support.image.TensorImage

object TFService {
    val labels = listOf("Aluvial", "Andosol", "Humus", "Kapur", "Laterit", "Pasir")
    val soils = mapOf(
            "Aluvial" to SoilParameters.ALLUVIAL,
            "Andosol" to SoilParameters.ANDOSOL,
            "Humus" to SoilParameters.HUMUS,
            "Kapur" to SoilParameters.KAPUR,
            "Laterit" to SoilParameters.LATERITE,
            "Pasir" to SoilParameters.PASIR)

    fun predict(context: Context, bitmap: Bitmap): List<Pair<String, Float>> {
        val options = ImageClassifierOptions.builder()
            .setMaxResults(5)  // return top-5 predictions
            .build()

        // Load model from assets
        val classifier = ImageClassifier.createFromFileAndOptions(
            context,
            "model.tflite",
            options
        )

        // Convert Bitmap → TensorImage
        val tensorImage = TensorImage.fromBitmap(bitmap)

        // Run inference
        val results = classifier.classify(tensorImage)

        // Parse predictions
        return results.flatMap { it.categories.map { cat -> cat.label to cat.score } }
    }

    fun argmax(prediction: List<Pair<String, Float>>): Pair<String, Float> {
        var max = Float.MIN_VALUE;
        var soil = ""
        for (i in 0..prediction.size - 1) {
            if (prediction[i].second > max) {
                max = prediction[i].second;
                soil = prediction[i].first;
            }
        }
        return Pair(soil, max)
    }
}