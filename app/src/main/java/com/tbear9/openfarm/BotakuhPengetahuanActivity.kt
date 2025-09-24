package com.TBear9.openfarm.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tbear9.openfarm.databinding.BotakuhpengetahuanBinding

class BotakuhPengetahuanActivity : AppCompatActivity() {

    private lateinit var binding: BotakuhpengetahuanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = BotakuhpengetahuanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Aksi ketika tombol panah diklik
        binding.tombolKembali.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Opsional: agar tidak bisa balik lagi pakai tombol back
        }
    }
}
