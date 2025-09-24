package com.tbear9.openfarm

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tbear9.openfarm.activities.Camera
import com.tbear9.openfarm.activities.DevActivity
import com.tbear9.openfarm.databinding.BottomsheetPageBinding
import com.tbear9.openfarm.databinding.MainmenuBinding

class MainActivity : AppCompatActivity() {
    private val mAppBarConfiguration: AppBarConfiguration? = null
    private var binding: MainmenuBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainmenuBinding.inflate(layoutInflater)
        setContentView(binding!!.getRoot())
        binding!!.chooseButton.setOnClickListener(View.OnClickListener { v: View? ->
            showBottomSheet()
        })

        binding!!.menuButton.setOnClickListener(View.OnClickListener { v: View? -> showPopup(v) })
        binding!!.helpIcon.setOnClickListener(View.OnClickListener { v: View? ->
            val intent = Intent(this@MainActivity, AboutMeActivity::class.java)
            startActivity(intent)
        })

        binding!!.start.setOnClickListener {
            val intent = Intent(this, Camera::class.java)
            startActivity(intent)
        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu)
    }

    fun enterDev(view: View?) {
        val intent = Intent(this, DevActivity::class.java)
        startActivity(intent)
    }

    private fun showPopup(v: View?) {
        val popupMenu = PopupMenu(this, v)
        val inflater = popupMenu.getMenuInflater()
        inflater.inflate(R.menu.popup_menu, popupMenu.getMenu())

        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem): Boolean {
                if (item.getItemId() == R.id.keluar_apk) {
                    finishAffinity() // keluar aplikasi
                    return true
                }
                return false
            }
        })

        popupMenu.show()
    }


    // --- BottomSheet muncul disini ---
    private fun showBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(this@MainActivity)
        val bottomBinding = BottomsheetPageBinding.inflate(getLayoutInflater())
        bottomSheetDialog.setContentView(bottomBinding.getRoot())

        // Tombol Panduan
        bottomBinding.btnPanduan.setOnClickListener(View.OnClickListener { v: View? ->
            val intent = Intent(this@MainActivity, BotakuhPanduanActivity::class.java)
            startActivity(intent)
            bottomSheetDialog.dismiss()
        })

        // Tombol Pengetahuan
        bottomBinding.tombolPengetahuan.setOnClickListener(View.OnClickListener { v: View? ->
            val intent = Intent(this@MainActivity, BotakuhPengetahuanActivity::class.java)
            startActivity(intent)
            bottomSheetDialog.dismiss()
        })

        bottomSheetDialog.show()
    }


    fun initmain() {

    }
}