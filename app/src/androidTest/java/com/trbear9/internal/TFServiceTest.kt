package com.trbear9.internal

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import com.trbear9.ui.activities.SoilResult
import com.trbear9.plants.CsvHandler
import com.trbear9.plants.parameters.GeoParameters
import com.trbear9.plants.parameters.SoilParameters
import com.trbear9.plants.parameters.UserVariable
import kotlinx.coroutines.runBlocking
import org.junit.Test

class TFServiceTest {
    val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun predict(){
        TFService.load(context)
        val bit = listOf(
            BitmapFactory.decodeStream(context.assets.open("dataset/Aluvial/aluvial-004.jpg")),
            BitmapFactory.decodeStream(context.assets.open("dataset/Andosol/aafgfwqe.jpeg")),
            BitmapFactory.decodeStream(context.assets.open("dataset/Humus/a.png")),
            BitmapFactory.decodeStream(context.assets.open("dataset/Kapur/adcqaewef.jpg")),
            BitmapFactory.decodeStream(context.assets.open("dataset/Laterit/aug_0_158.jpg")),
            BitmapFactory.decodeStream(context.assets.open("dataset/Pasir/asdgwecf.jpg"))
        )
        for(i in bit) {
            val prediction = TFService.predict(i)

            val argmax = TFService.argmax(prediction)
            Log.i("TEST", "Max prediction: ${argmax.first} with confidence ${argmax.second}")
            Log.i("TEST", "Prediction: ")
            TFService.getPredictions(prediction).forEach { (label, score) ->
                Log.i("TEST", "Label: $label, Score: $score")
            }
        }
        TFService.close()
    }

    @Test
    fun process(){
        CsvHandler.load(context)
        val bit = BitmapFactory.decodeStream(context.assets.open("dataset/Aluvial/aluvial-004.jpg"))
        val variable = UserVariable()
        variable.image = bit
        variable.soil = SoilParameters.ALLUVIAL
        variable.geo = GeoParameters()
        val res = Data.process(variable, soilResult = SoilResult())

        runBlocking {
            res.collect {
                Log.i("TEST", "Progress: ${it.progress} / ${it.target}")
            }
        }
    }
}