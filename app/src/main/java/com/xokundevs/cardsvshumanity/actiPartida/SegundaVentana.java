package com.xokundevs.cardsvshumanity.actiPartida;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.xokundevs.cardsvshumanity.R;
import com.xokundevs.cardsvshumanity.actiBarajas.Barajas;

public class SegundaVentana extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segunda_ventana);
        this.setTitle(getString(R.string.cch));
    }

    public void onClickCrearPartida(View view){
        Intent in=new Intent(getApplicationContext(), CrearPartida.class);
        startActivity(in);
    }

    public void onClickUnirsePartida(View view){
        Intent in=new Intent(getApplicationContext(), UnirsePartida.class);
        startActivity(in);
    }

    public void onClickBarajas(View view){
        Intent in=new Intent(getApplicationContext(), Barajas.class);
        startActivity(in);
    }
}
