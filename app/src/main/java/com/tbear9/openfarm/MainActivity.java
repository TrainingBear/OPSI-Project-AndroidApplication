package com.tbear9.openfarm;
package com.TBear9.openfarm;
import android.content.Intent;
import com.TBear9.openfarm.ui.BotakuhPengetahuanActivity; // ⬅️ Tambahkan ini
import com.TBear9.openfarm.ui.BotakuhPanduanActivity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.tbear9.openfarm.activities.DevActivity;
import com.tbear9.openfarm.databinding.TestlayoutBinding;
import com.TBear9.openfarm.Activities.DevActivity;
import com.TBear9.openfarm.databinding.MainmenuBinding;
import com.TBear9.openfarm.databinding.TestlayoutBinding;

import androidx.navigation.ui.AppBarConfiguration;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private TestlayoutBinding binding;
    private MainmenuBinding bindingmenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = TestlayoutBinding.inflate(getLayoutInflater());
        bindingmenu = MainmenuBinding.inflate(getLayoutInflater());
        setContentView(bindingmenu.getRoot());
        setSupportActionBar(binding.toolbar);
        bindingmenu.tombolPengetahuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BotakuhPengetahuanActivity.class);
                startActivity(intent);
            }
        });
        bindingmenu.btnPanduan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BotakuhPanduanActivity.class);
                startActivity(intent);
            }
        });
        bindingmenu.menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu(v);
            }
        });


        initmain();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void enterDev(View view){
        Intent intent = new Intent(this, DevActivity.class);
        startActivity(intent);
    }

    public void botakuh_pengetahuan(View view){
        setContentView(findViewById(R.id.botakuhpengetahuan));
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

    private void showMenu(View v){
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, v);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.AboutMe) {
                    Intent intent = new Intent(MainActivity.this, AboutMeActivity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.KeluarApk) {
                    finishAffinity();
                    return true;

                }
                return false;


        }
    });
        popupMenu.show();
    }


    public void initmain(){
        int c = binding.toolbar.getChildCount();
        for (int i = 0; i < c; i++) {
            View v = binding.toolbar.getChildAt(i);
            if(v instanceof TextView){
                TextView title = (TextView) v;
                if(title.getText().equals(binding.toolbar.getTitle())){
                    title.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent repo = new Intent(Intent.ACTION_VIEW);
                            repo.setData(Uri.parse("https://github.com/TrainingBear/OPSI-Project-AndroidApplication"));
                            view.getContext().startActivity(repo);
                        }
                    });
                }
            }
        }
        binding.author.setOnClickListener((v)->{
            Intent author = new Intent(Intent.ACTION_VIEW);
            author.setData(Uri.parse("https://github.com/TrainingBear"));
            v.getContext().startActivity(author);
        });
        binding.openfarmLogo2.setOnClickListener((v)->{
            Intent openfarm = new Intent(Intent.ACTION_VIEW);
            openfarm.setData(Uri.parse("https://github.com/TrainingBear/OPSI-Project-AndroidApplication"));
            v.getContext().startActivity(openfarm);
        });
        binding.devMode.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                enterDev(v);
                return true;
            }
        });
    }
}