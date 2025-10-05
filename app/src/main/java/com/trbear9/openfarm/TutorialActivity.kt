package com.trbear9.openfarm

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.trbear9.openfarm.databinding.ActivityTutorialBinding


class TutorialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTutorialBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTutorialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val slides = listOf(
            SlideItem(R.drawable.location_acces, "Step 1", "Nyalakan izin lokasi"),
            SlideItem(R.drawable.camera_access, "Step 2", "Nyalakan izin kamera"),
            SlideItem(R.drawable.camera_pointer, "Step 3", "Arahkan kamera ke tanah, tanpa terhalang objek lain"),
            SlideItem(R.drawable.ph_imgparameter, "Step 4", "Masukkan input pH tanah (opsional)"),
            SlideItem(R.drawable.result_imgplant, "Step 5", "Tunggu proses pengolahan data, lalu hasil akan keluar")
        )

        val adapter = SlideAdapter(this, slides)
        binding.viewPager.adapter = adapter

        // hubungkan dotsIndicator dengan viewPager
        binding.dotsIndicator.attachTo(binding.viewPager)

        // Panah kembali hanya muncul di slide pertama
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tombolKembali.visibility =
                    if (position == 0) View.VISIBLE else View.GONE
            }
        })

        binding.tombolKembali.setOnClickListener {
            this.finish()
        }
    }
}
