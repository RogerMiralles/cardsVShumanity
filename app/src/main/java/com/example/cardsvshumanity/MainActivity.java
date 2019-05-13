package com.example.cardsvshumanity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cardsvshumanity.jugarPerfil.perfil;
import com.example.cardsvshumanity.jugarPerfil.segundaVentana;
import com.example.cardsvshumanity.logReg.login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;
import java.util.prefs.Preferences;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private Button mJuegaIniciaSes;
    private Button mPerfil;
    private Button mSalir;
    private final int codigo=0;
    private ImageButton mIbtn;
    private TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Connection.crearCon();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //loadLocale();
        this.setTitle("CCH");
        mJuegaIniciaSes = findViewById(R.id.btnJugar);
        mPerfil=findViewById(R.id.btnPerfil);
        mSalir=findViewById(R.id.btnSalida);
        mIbtn=findViewById(R.id.imBtnIdioma);
        txt=findViewById(R.id.txtTitulo);
        loadLocale();
        mAuth = FirebaseAuth.getInstance();
        ponerCosasVisIn();
    }

    public void onClickJugar(View view) {
        if(mUser != null) {
            Intent listSong = new Intent(getApplicationContext(), segundaVentana.class);
            startActivity(listSong);
        }
        else{
            Intent listSong = new Intent(getApplicationContext(), login.class);
            startActivityForResult(listSong,codigo);
        }
    }

    public void onClickPerfil(View view) {
        Intent listSong = new Intent(getApplicationContext(), perfil.class);
        startActivity(listSong);
    }

    public void onClickIdioma(View view){
        showChangeLanguageDialog();
    }

    public void onClickSalir(View view){
        mAuth.signOut();
        ponerCosasVisIn();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            ponerCosasVisIn();
        }
    }

    public void ponerCosasVisIn(){
        txt.setText(getString(R.string.cartas_contra_la_humanidad));
        mUser=mAuth.getCurrentUser();
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


    private void showChangeLanguageDialog() {
        final String[] listItems={"Castellano","Catalan","English"};
        AlertDialog.Builder mBuilder=new AlertDialog.Builder(this);
        mBuilder.setTitle("Elige Idioma.....");
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    Toast.makeText(getApplicationContext(), "Traduciendo al castellano", Toast.LENGTH_SHORT).show();
                    setLocale("es");
                    recreate();
                }
                else if(i==1){
                    Toast.makeText(getApplicationContext(), "Traduciendo al catalan", Toast.LENGTH_SHORT).show();
                    setLocale("ca");
                    recreate();
                }
                else if(i==2){
                    Toast.makeText(getApplicationContext(), "Traduciendo al ingles", Toast.LENGTH_SHORT).show();
                    setLocale("en");
                    recreate();
                }
                dialogInterface.dismiss();
            }
        });
        AlertDialog mDialog=mBuilder.create();
        mDialog.show();
    }

    private void setLocale(String s) {
        Locale locale=new Locale(s);
        Locale.setDefault(locale);
        Configuration config =new Configuration();
        config.locale=locale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor=getSharedPreferences("Settings", getBaseContext().MODE_PRIVATE).edit();
        editor.putString("My_Lang",s);
        editor.apply();

    }
    public void loadLocale(){
        SharedPreferences prefs =getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language=prefs.getString("My_Lang","");
        switch(language){
            case "ca":
                mIbtn.setImageResource(R.drawable.catalonia);
                break;
            case "es":
                mIbtn.setImageResource(R.drawable.spain);
                break;
            case "en":
                mIbtn.setImageResource(R.drawable.uk);
                break;
        }
        setLocale(language);
    }
}
