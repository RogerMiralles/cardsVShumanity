package com.xokundevs.cardsvshumanity.fragsBar;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.xokundevs.cardsvshumanity.R;
import com.xokundevs.cardsvshumanity.actiLogReg.Login;
import com.xokundevs.cardsvshumanity.actiPartida.SegundaVentana;
import com.xokundevs.cardsvshumanity.javaConCod.Connection;

import java.util.Locale;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class PrincipalFragment extends Fragment {

    public static final String ASYMMETRIC_CIPHER_MODE = "RSA/ECB/PKCS1PADDING";

    public PrincipalFragment() {
        // Required empty public constructor
    }

    private Button mJuegaIniciaSes;
    private Button mSalir;
    private final int codigo=0;
    private ImageButton mIbtn;
    private TextView txt;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView= inflater.inflate(R.layout.fragment_principal, container, false);
        getActivity().setTitle(getString(R.string.cch));
        mSalir= rootView.findViewById(R.id.btnSalida);
        mSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Connection.isLogined()){
                    Connection.logOut();
                    ponerCosasVisIn();
                }
            }
        });
        mIbtn= rootView.findViewById(R.id.imBtnIdioma);
        mIbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeLanguageDialog();
            }
        });
        mJuegaIniciaSes= rootView.findViewById(R.id.btnJugar);
        mJuegaIniciaSes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Connection.isLogined()) {
                    Intent listSong = new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(), SegundaVentana.class);
                    startActivity(listSong);
                }
                else{
                    Intent listSong = new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(), Login.class);
                    startActivityForResult(listSong,codigo);
                }
            }
        });
        txt= rootView.findViewById(R.id.txtTitulo);
        loadLocale();
        ponerCosasVisIn();
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getActivity();
        if(resultCode== Activity.RESULT_OK){
            ponerCosasVisIn();
        }
    }

    public void ponerCosasVisIn(){
        txt.setText(getString(R.string.app_name));
        if(!Connection.isLogined()){
            mJuegaIniciaSes.setText(getString(R.string.inicia_session));
            mSalir.setVisibility(View.GONE);
        }
        else{
            mJuegaIniciaSes.setText(getString(R.string.jugar));
            mSalir.setVisibility(View.VISIBLE);
        }
    }

    private void showChangeLanguageDialog() {
        final String[] listItems={"Castellano","Catalan","English"};
        AlertDialog.Builder mBuilder=new AlertDialog.Builder(getActivity());
        mBuilder.setTitle(getString(R.string.eligeIdioma));
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), getString(R.string.espanol), Toast.LENGTH_SHORT).show();
                    setLocale("es");

                    Fragment fragment = PrincipalFragment.this;
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.detach(fragment);
                    ft.attach(fragment);
                    ft.commit();
                }
                else if(i==1){
                    Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), getString(R.string.catalan), Toast.LENGTH_SHORT).show();
                    setLocale("ca");

                    Fragment fragment = PrincipalFragment.this;
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.detach(fragment);
                    ft.attach(fragment);
                    ft.commit();
                }
                else if(i==2){
                    Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), getString(R.string.ingles), Toast.LENGTH_SHORT).show();
                    setLocale("en");

                    Fragment fragment = PrincipalFragment.this;
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.detach(fragment);
                    ft.attach(fragment);
                    ft.commit();
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
        Configuration config = Objects.requireNonNull(getActivity()).getBaseContext().getResources().getConfiguration();
        config.locale = locale;
        getActivity().getBaseContext().getResources().updateConfiguration(
                config,
                getActivity().getBaseContext().getResources().getDisplayMetrics()
        );

        SharedPreferences.Editor editor=getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE).edit();
        editor.putString("My_Lang",s);
        editor.apply();

    }

    public void loadLocale(){
        SharedPreferences prefs = Objects.requireNonNull(getActivity()).getSharedPreferences("Settings", Activity.MODE_PRIVATE);
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
            default:
                mIbtn.setImageResource(R.drawable.idioma);
                break;
        }
        setLocale(language);
    }
}
