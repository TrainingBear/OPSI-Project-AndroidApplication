package com.TBear9.openfarm.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.TBear9.openfarm.MainActivity
import com.TBear9.openfarm.databinding.BotakuhpanduanBinding

class BotakuhPanduanActivity : AppCompatActivity() {

    private lateinit var binding: BotakuhpanduanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = BotakuhpanduanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tombolKembali2.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Supaya tidak kembali ke halaman ini saat menekan tombol back
        }
    }
}
