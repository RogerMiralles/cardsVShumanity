package com.example.cardsvshumanity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.cardsvshumanity.jugarPerfil.segundaVentana;

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
