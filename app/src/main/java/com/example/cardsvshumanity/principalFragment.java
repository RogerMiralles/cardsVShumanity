package com.example.cardsvshumanity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cardsvshumanity.jugarPerfil.segundaVentana;
import com.example.cardsvshumanity.logReg.login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class principalFragment extends Fragment {


    public principalFragment() {
        // Required empty public constructor
    }
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private Button mJuegaIniciaSes;
    private Button mSalir;
    private final int codigo=0;
    private ImageButton mIbtn;
    private TextView txt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView= inflater.inflate(R.layout.fragment_principal, container, false);
        //loadLocale();
        mAuth = FirebaseAuth.getInstance();
        mSalir=(Button)rootView.findViewById(R.id.btnSalida);
        mSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                ponerCosasVisIn();
            }
        });
        mIbtn=(ImageButton)rootView.findViewById(R.id.imBtnIdioma);
        mIbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeLanguageDialog();
            }
        });
        mJuegaIniciaSes=(Button)rootView.findViewById(R.id.btnJugar);
        mJuegaIniciaSes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUser != null) {
                    Intent listSong = new Intent(getActivity().getApplicationContext(), segundaVentana.class);
                    startActivity(listSong);
                }
                else{
                    Intent listSong = new Intent(getActivity().getApplicationContext(), login.class);
                    startActivityForResult(listSong,codigo);
                }
            }
        });
        txt=(TextView)rootView.findViewById(R.id.txtTitulo);
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==getActivity().RESULT_OK){
            ponerCosasVisIn();
        }
    }

    public void ponerCosasVisIn(){
        txt.setText(getString(R.string.cartas_contra_la_humanidad));
        mUser=mAuth.getCurrentUser();
        if(mUser == null){
            mJuegaIniciaSes.setText(getString(R.string.inicia_session));
            mSalir.setVisibility(View.INVISIBLE);
        }
        else{
            mJuegaIniciaSes.setText(getString(R.string.jugar));
            mSalir.setVisibility(View.VISIBLE);
        }
    }

    private void showChangeLanguageDialog() {
        final String[] listItems={"Castellano","Catalan","English"};
        AlertDialog.Builder mBuilder=new AlertDialog.Builder(getActivity());
        mBuilder.setTitle("Elige Idioma.....");
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    Toast.makeText(getActivity().getApplicationContext(), "Traduciendo al castellano", Toast.LENGTH_SHORT).show();
                    setLocale("es");
                    getActivity().recreate();
                }
                else if(i==1){
                    Toast.makeText(getActivity().getApplicationContext(), "Traduciendo al catalan", Toast.LENGTH_SHORT).show();
                    setLocale("ca");
                    getActivity().recreate();
                }
                else if(i==2){
                    Toast.makeText(getActivity().getApplicationContext(), "Traduciendo al ingles", Toast.LENGTH_SHORT).show();
                    setLocale("en");
                    getActivity().recreate();
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
        getActivity().getBaseContext().getResources().updateConfiguration(config,getActivity().getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor=getActivity().getSharedPreferences("Settings", getActivity().getBaseContext().MODE_PRIVATE).edit();
        editor.putString("My_Lang",s);
        editor.apply();

    }
    public void loadLocale(){
        SharedPreferences prefs =getActivity().getSharedPreferences("Settings", getActivity().MODE_PRIVATE);
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
