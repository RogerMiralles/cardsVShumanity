package com.example.cardsvshumanity.actiPartida;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.cardsvshumanity.R;

public class crearPartida extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_partida);
        setTitle(getString(R.string.crear_partida));
    }
}
