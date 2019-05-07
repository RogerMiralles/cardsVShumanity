package com.example.cardsvshumanity.logReg;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.cardsvshumanity.MainActivity;
import com.example.cardsvshumanity.R;

public class login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void onClickLogear(View view) {
        Intent listSong = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(listSong);
    }

    public void onClickRegistrar(View view) {
        Intent listSong = new Intent(getApplicationContext(), registre.class);
        startActivity(listSong);
    }


}
