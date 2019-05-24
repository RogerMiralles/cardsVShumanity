package com.example.cardsvshumanity.actiBarajas;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.cardsvshumanity.R;

public class EditarBaraja extends AppCompatActivity {

    private TextView textoNombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_baraja);
        Intent in=getIntent();
        textoNombre=findViewById(R.id.txtNombreBarajaEdit);
        textoNombre.setText(in.getStringExtra("nombre"));
    }
}
