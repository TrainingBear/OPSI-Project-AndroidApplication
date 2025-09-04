package com.tbear9.openfarm;

import android.content.Intent;


import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.tbear9.openfarm.activities.DevActivity;
import com.tbear9.openfarm.databinding.MainmenuBinding;

import androidx.navigation.ui.AppBarConfiguration;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private MainmenuBinding main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = MainmenuBinding.inflate(getLayoutInflater());
        setContentView(main.getRoot());
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

    private void inimain(){

    }
}