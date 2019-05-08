package com.example.cardsvshumanity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.cardsvshumanity.jugarPerfil.perfil;
import com.example.cardsvshumanity.jugarPerfil.segundaVentana;
import com.example.cardsvshumanity.logReg.login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private Button mJuegaIniciaSes;
    private Button mPerfil;
    private Button mSalir;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("Cartas Contra la Humanidad");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mJuegaIniciaSes = findViewById(R.id.btnJugar);
        mPerfil=findViewById(R.id.btnPerfil);
        mSalir=findViewById(R.id.btnSalida);
        if(mUser == null){
            mJuegaIniciaSes.setText(getString(R.string.inicia_session));
            mPerfil.setVisibility(View.INVISIBLE);
            mSalir.setVisibility(View.INVISIBLE);
        }
        else{
            mJuegaIniciaSes.setText(getString(R.string.jugar));
            mPerfil.setVisibility(View.VISIBLE);
            mSalir.setVisibility(View.VISIBLE);
        }
    }

    public void onClickJugar(View view) {
        if(mUser != null) {
            Intent listSong = new Intent(getApplicationContext(), segundaVentana.class);
            startActivity(listSong);
        }
        else{
            Intent listSong = new Intent(getApplicationContext(), login.class);
            startActivity(listSong);
        }
    }

    public void onClickPerfil(View view) {
        Intent listSong = new Intent(getApplicationContext(), perfil.class);
        startActivity(listSong);
    }

    public void onClickIdioma(View view){
        Toast.makeText(getApplicationContext(),"Todavia no",Toast.LENGTH_LONG).show();
    }

    public void onClickSalir(View view){
        mAuth.signOut();
    }
}
