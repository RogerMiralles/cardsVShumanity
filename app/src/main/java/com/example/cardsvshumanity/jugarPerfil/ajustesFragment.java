package com.example.cardsvshumanity.jugarPerfil;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.cardsvshumanity.MainActivity;
import com.example.cardsvshumanity.R;
import com.example.cardsvshumanity.logReg.login;
import com.example.cardsvshumanity.principalFragment;

import java.util.Locale;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class ajustesFragment extends Fragment {

    private String mUser;
    private Button mIdioma;
    private Button mDatos;
    private Button mCuenta;

    public ajustesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView=inflater.inflate(R.layout.fragment_ajustes, container, false);
        mUser=null;
        mIdioma= rootView.findViewById(R.id.btnIdioma);
        mIdioma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeLanguageDialog();
            }
        });
        mDatos= rootView.findViewById(R.id.btnDatos);
        mDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //codigo a ejecutar
                Toast.makeText(getActivity().getApplicationContext(),"Se han borrado los datos(falta codigo)",Toast.LENGTH_LONG).show();
            }
        });
        mCuenta= rootView.findViewById(R.id.btnCuenta);
        mCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUser != null) {
                    confirmar();
                }
                else{
                    noUsuari();
                }

            }
        });
        loadLocale();
        return rootView;
    }

    private void noUsuari(){
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setMessage("No tienes usuario, quieres iniciar tu sesion?");
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent intent=new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(), login.class);
                        startActivity(intent);
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void confirmar(){
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setMessage("Estas seguro?");
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                            //codigo a ejecutar
                            Toast.makeText(getActivity().getApplicationContext(),"Cuenta borrada(falta codigo)",Toast.LENGTH_LONG).show();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void showChangeLanguageDialog() {
        final String[] listItems={"Castellano","Catalan","English"};
        AlertDialog.Builder mBuilder=new AlertDialog.Builder(getActivity());
        mBuilder.setTitle("Elige Idioma.....");
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Traduciendo al castellano", Toast.LENGTH_SHORT).show();
                    setLocale("es");
                    //getActivity().recreate();
                    //Intent in=new Intent(getActivity().getApplicationContext(),MainActivity.class);
                    //in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK
                    //                            | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    //getActivity().finish();
                    //startActivity(in);

                    Fragment fragment = ajustesFragment.this;
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.detach(fragment);
                    ft.attach(fragment);
                    ft.commit();
                }
                else if(i==1){
                    Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Traduciendo al catalan", Toast.LENGTH_SHORT).show();
                    setLocale("ca");
                    //Intent in=new Intent(getActivity().getApplicationContext(),MainActivity.class);
                    //in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    //getActivity().finish();
                    //startActivity(in);

                    Fragment fragment = ajustesFragment.this;
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.detach(fragment);
                    ft.attach(fragment);
                    ft.commit();
                }
                else if(i==2){
                    Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Traduciendo al ingles", Toast.LENGTH_SHORT).show();
                    setLocale("en");
                    //Intent in=new Intent(getActivity().getApplicationContext(),MainActivity.class);
                    //in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    //getActivity().finish();
                    //startActivity(in);

                    Fragment fragment = ajustesFragment.this;
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
        /*Locale.setDefault(locale);
        Configuration config =new Configuration();
        config.locale=locale;
        Objects.requireNonNull(getActivity()).getBaseContext().getResources().updateConfiguration(config,getActivity().getBaseContext().getResources().getDisplayMetrics());
        getActivity().getBaseContext();
        */
        Locale.setDefault(locale);
        Configuration config = getActivity().getBaseContext().getResources().getConfiguration();
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
        /*switch(language){
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
        }*/
        setLocale(language);
    }
}
