package com.example.cardsvshumanity.actiPartida;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.cardsvshumanity.R;

public class segundaVentana extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segunda_ventana);
        this.setTitle(getString(R.string.cch));
    }

    public void onClickCrearPartida(View view){
        Intent in=new Intent(getApplicationContext(), crearPartida.class);
        startActivity(in);
    }

    public void onClickUnirsePartida(View view){
        Intent in=new Intent(getApplicationContext(), unirsePartida.class);
        startActivity(in);
    }

    public void onClickBarajas(View view){
        Intent in=new Intent(getApplicationContext(), barajas.class);
        startActivity(in);
    }
}
