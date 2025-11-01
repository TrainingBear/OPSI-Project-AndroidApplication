package com.trbear9.internal


import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.ai.edge.litert.CompiledModel
import com.trbear9.plants.api.SoilParameters
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp

object TFService {
//    val labels = listOf("Aluvial", "Andosol", "Humus", "Kapur", "Laterit", "Pasir")
    val labels = listOf("Aluvial", "Andosol", "Entisol", "Humus", "Inceptisol", "Laterit", "Kapur", "Pasir")
//    val soils = mapOf(
//            "Aluvial" to SoilParameters.ALLUVIAL,
//            "Andosol" to SoilParameters.ANDOSOL,
//            "Humus" to SoilParameters.HUMUS,
//            "Kapur" to SoilParameters.KAPUR,
//            "Laterit" to SoilParameters.LATERITE,
//            "Pasir" to SoilParameters.PASIR)
    val soils = mapOf(
            "Aluvial" to SoilParameters.ALLUVIAL,
            "Andosol" to SoilParameters.ANDOSOL,
            "Humus" to SoilParameters.HUMUS,
            "Kapur" to SoilParameters.KAPUR,
            "Laterit" to SoilParameters.LATERITE,
            "Pasir" to SoilParameters.PASIR,
            "Inceptisol" to SoilParameters.INCEPTISOL,
            "Entisol" to SoilParameters.ENTISOL
        )
    var model: CompiledModel? = null
    fun load(context: Context){
        model = CompiledModel.create(context.assets, "model.tflite")
    }
    fun predict(bitmap: Bitmap): FloatArray {
        // Preallocate input/output buffers
        Log.d("TFService", "predicting... ")
        val inputBuffers = model!!.createInputBuffers()
        val outputBuffers = model!!.createOutputBuffers()

        val tensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(bitmap)
        val image = ImageProcessor.Builder()
            .add(ResizeOp(320, 320, ResizeOp.ResizeMethod.BILINEAR))
            .build()
            .process(tensorImage)

        val buffer = image.buffer
        buffer.rewind()
        val floatArray = FloatArray(buffer.remaining() / 4)
        buffer.asFloatBuffer().get(floatArray)

        Log.d("TensorDebug", "Shape = ${tensorImage.tensorBuffer.shape.contentToString()}")
        Log.d("TensorDebug", "Mean = ${tensorImage.tensorBuffer.floatArray.average()}")
        Log.d("TensorDebug", "Min = ${tensorImage.tensorBuffer.floatArray.minOrNull()}")
        Log.d("TensorDebug", "Max = ${tensorImage.tensorBuffer.floatArray.maxOrNull()}")

        // Fill the first input
        inputBuffers[0].writeFloat(floatArray)
        // Invoke
        model!!.run(inputBuffers, outputBuffers)
        // Read the output
        val outputFloatArray = outputBuffers[0].readFloat()

        // Clean up buffers and model
        inputBuffers.forEach { it.close() }
        outputBuffers.forEach { it.close() }
        Log.d("TFService", "Predict Done! ")
        return outputFloatArray
    }

    fun close(){
        model?.close()
    }

    fun argmax(prediction: FloatArray): Pair<String, Float> {
        var max = Float.MIN_VALUE;
        var soil = ""
        for (i in 0..prediction.size - 1) {
            if (prediction[i]> max) {
                max = prediction[i]
                soil = labels[i]
            }
        }
        return Pair(soil, max)
    }

    fun getPredictions(prediction: FloatArray): List<Pair<String, Float>> {
        val predictions = mutableListOf<Pair<String, Float>>()
        for (i in 0..prediction.size - 1) {
            predictions.add(Pair(labels[i], prediction[i]))
        }
        return predictions
    }
}