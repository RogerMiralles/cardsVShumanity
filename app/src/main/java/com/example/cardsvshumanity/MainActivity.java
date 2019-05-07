package com.example.cardsvshumanity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.cardsvshumanity.jugarPerfil.perfil;
import com.example.cardsvshumanity.jugarPerfil.segundaVentana;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("Cartas Contra la Humanidad");
        mAuth = FirebaseAuth.getInstance();
    }

    public void onClickJugar(View view) {
        Intent listSong = new Intent(getApplicationContext(), segundaVentana.class);
        startActivity(listSong);
    }

    public void onClickPerfil(View view) {
        Intent listSong = new Intent(getApplicationContext(), perfil.class);
        startActivity(listSong);
    }

    public void onClickIdioma(View view){
        Toast.makeText(getApplicationContext(),"Todavia no",Toast.LENGTH_LONG).show();
    }
}
