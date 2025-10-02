package com.trbear9.internal

import android.content.Context
import android.graphics.BitmapFactory
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.*
import org.junit.Test
import org.slf4j.LoggerFactory

class TFServiceTest {
    val log = LoggerFactory.getLogger(TFServiceTest::class.java)!!
    val context = ApplicationProvider.getApplicationContext<Context>()
    @Test
    fun predict(){
        val bitmap = BitmapFactory.decodeStream(context.assets.open("dataset/Aluvial/aluvial-004.jpg"))
        val prediction = TFService.predict(context, bitmap)

        log.info("Prediction: ")
        prediction.forEach { (label, score) ->
            log.info(" {} with {}", label, score)
        }
    }
}