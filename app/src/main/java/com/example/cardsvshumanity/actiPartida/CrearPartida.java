package com.example.cardsvshumanity.actiPartida;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.cardsvshumanity.R;
import com.example.cardsvshumanity.javaConCod.Connection;

public class CrearPartida extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_partida);
        setTitle(getString(R.string.crear_partida));

        Connection.getBarajasUser(this);

    }

    public void creaPartida(View v){

    }
}
