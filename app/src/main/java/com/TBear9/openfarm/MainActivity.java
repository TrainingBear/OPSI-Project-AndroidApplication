package com.TBear9.openfarm;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.TBear9.openfarm.Activities.DevActivity;
import com.TBear9.openfarm.databinding.TestlayoutBinding;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private TestlayoutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = TestlayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
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