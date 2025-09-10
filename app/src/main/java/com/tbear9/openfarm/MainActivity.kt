package com.tbear9.openfarm

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.TBear9.openfarm.ui.BotakuhPanduanActivity
import com.TBear9.openfarm.ui.BotakuhPengetahuanActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tbear9.openfarm.activities.DevActivity
import com.tbear9.openfarm.databinding.BottomsheetPageBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeActivity().Greeting()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    fun enterDev(view: View?) {
        val intent = Intent(this, DevActivity::class.java)
        startActivity(intent)
    }

    //    public void botakuh_pengetahuan(View view){
    //        setContentView(findViewById(R.id.botakuhpengetahuan));
    //        Intent intent = new Intent(this, MainActivity.class);
    //        startActivity(intent);
    //
    //    }
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
}