package com.example.cardsvshumanity.jugarPerfil;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.cardsvshumanity.R;

public class perfil extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        this.setTitle("Cartas Contra la Humanidad");
    }
}