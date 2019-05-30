package com.example.cardsvshumanity.actiPartida;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.cardsvshumanity.R;

public class UnirsePartida extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unirse_partida);
        setTitle(getString(R.string.unirse_partida));
    }
}
