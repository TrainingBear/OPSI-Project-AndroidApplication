package com.tbear9.openfarm.activities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import com.fasterxml.jackson.core.StreamReadConstraints
import com.fasterxml.jackson.core.StreamWriteConstraints
import com.fasterxml.jackson.databind.ObjectMapper
import com.tbear9.openfarm.R
import com.tbear9.openfarm.Util
import com.trbear9.plants.PlantClient
import com.trbear9.plants.api.CustomParameters
import com.trbear9.plants.api.GeoParameters
import com.trbear9.plants.api.SoilParameters
import com.trbear9.plants.api.UserVariable
import com.trbear9.plants.E.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import java.io.ByteArrayOutputStream

class PlantClientTest {
    val client = PlantClient("TrainingBear/84d0e105aaabce26c8dfbaff74b2280e", size = 200_000)
    val objectMapper = ObjectMapper()
    init {
            objectMapper.factory.setStreamReadConstraints(
                StreamReadConstraints.builder()
                    .maxStringLength(1_000_000)
                    .build()
            ).setStreamWriteConstraints(
                StreamWriteConstraints.builder()
                    .maxNestingDepth(1_000_000)
                    .build()
            )
    }

    @Test
    fun testBitmap() {
        val context = ApplicationProvider.getApplicationContext<Context>();
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.inceptisol_008);
        assertNotNull(bitmap);
    }

    @Test
    fun getPlant(){
        val data= UserVariable()
        data.soil = (SoilParameters.ALLUVIAL)
        data.geo = (GeoParameters())
        data.custom = (
            CustomParameters().apply {
                category = CATEGORY.vegetables
            }
        )
        val context = ApplicationProvider.getApplicationContext<Context>();
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.inceptisol_008);
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        data.setImage(stream.toByteArray(), "inceptisol2934puyh.jpg")
        runBlocking {
            val response = client.sendPacket(data)
            Util.debug("Loaded plants with size of ${response!!.tanaman.size}")
            Util.debug(objectMapper.readTree(objectMapper.writeValueAsString(response)).toPrettyString())
        }
    }
    @Test
    fun head(){
        runBlocking {
            val url = client.getUrl()
            Log.i("TEST","URL: $url")
        }
    }

    @Test
    fun get(){
        runBlocking {
            client.GET()
        }
    }
}