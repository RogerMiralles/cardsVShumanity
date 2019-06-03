package com.example.cardsvshumanity.fragsBar;


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
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cardsvshumanity.MainActivity;
import com.example.cardsvshumanity.R;
import com.example.cardsvshumanity.actiLogReg.Login;
import com.example.cardsvshumanity.javaConCod.Connection;

import java.io.File;
import java.util.Locale;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class AjustesFragment extends Fragment {

    private Button mIdioma;
    private Button mDatos;
    private Button mCuenta;

    public AjustesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView=inflater.inflate(R.layout.fragment_ajustes, container, false);
        Objects.requireNonNull(getActivity()).setTitle(getString(R.string.tituloAjustes));
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
                deleteCache(getActivity());
                Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),getString(R.string.lDatos),Toast.LENGTH_LONG).show();
            }
        });
        mCuenta= rootView.findViewById(R.id.btnCuenta);
        mCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Connection.isLogined()) {
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

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    private void noUsuari(){
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setMessage(getString(R.string.noUsuari));
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                getString(R.string.si),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent intent=new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(), Login.class);
                        startActivity(intent);
                    }
                });

        builder1.setNegativeButton(
                getString(R.string.no),
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
        builder1.setMessage(getString(R.string.confirmar));
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                getString(R.string.si),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        final EditText password = new EditText(getContext());
                        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {
                                if(which == DialogInterface.BUTTON_POSITIVE) {
                                    if (!password.getText().toString().isEmpty()) {
                                        Connection.ConnectionThread thread = Connection.borrarCuenta(getActivity(), password.getText().toString());
                                        thread.setRunBegin(new Runnable() {
                                            @Override
                                            public void run() {
                                                dialog.dismiss();
                                            }
                                        });

                                        thread.setRunNo(new Connection.ConnectionThread.ErrorRunable() {
                                            @Override
                                            public void run() {
                                                AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
                                                builder2.setPositiveButton(R.string.ok, null);

                                                switch (getError()){
                                                    case Connection.USER_ERROR_INVALID_PASSWORD:
                                                        builder2.setMessage(R.string.error_password_diff);
                                                        break;
                                                    case Connection.USER_ERROR_NON_EXISTANT_USER:
                                                        builder2.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                                Connection.logOut();
                                                            }
                                                        });
                                                        builder2.setMessage(R.string.error_erase_user_nouser);
                                                        break;
                                                    case Connection.SOCKET_DISCONNECTED:
                                                        builder2.setMessage(R.string.noConexion);
                                                        break;
                                                    case Connection.UNKOWN_ERROR:
                                                        builder2.setMessage(R.string.error_unknown_error);
                                                        break;
                                                    case Connection.USER_NOT_LOGINED:
                                                        Intent intent = new Intent(getContext(), MainActivity.class);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        startActivity(intent);
                                                        break;
                                                }
                                                builder2.show();
                                            }
                                        });

                                        thread.setRunOk(new Connection.ConnectionThread.SuccessRunnable() {
                                            @Override
                                            public void run() {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        Connection.logOut();
                                                    }
                                                })
                                                        .setMessage(R.string.borrar_cuenta_success)
                                                        .create().show();
                                            }
                                        });

                                        thread.start();
                                    }
                                    else{
                                       Toast.makeText(getContext(), R.string.camposVacios, Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else{
                                    dialog.dismiss();
                                }
                            }
                        };
                        password.setHint(R.string.contrase_a);
                        builder.setView(password)
                                .setPositiveButton(R.string.ok, clickListener)
                                .setNegativeButton(R.string.cancel, clickListener)
                                .create().show();
                        //Connection.borrarCuenta(getActivity());
                        //Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),getString(R.string.bCuenta),Toast.LENGTH_LONG).show();
                        //Toast.makeText(getActivity().getApplicationContext(), "todavia no", Toast.LENGTH_SHORT).show();
                    }
                });

        builder1.setNegativeButton(
                getString(R.string.no),
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
        mBuilder.setTitle(getString(R.string.eligeIdioma));
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), getString(R.string.espanol), Toast.LENGTH_SHORT).show();
                    setLocale("es");
                    Fragment fragment = AjustesFragment.this;
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.detach(fragment);
                    ft.attach(fragment);
                    ft.commit();
                }
                else if(i==1){
                    Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), getString(R.string.catalan), Toast.LENGTH_SHORT).show();
                    setLocale("ca");
                    Fragment fragment = AjustesFragment.this;
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.detach(fragment);
                    ft.attach(fragment);
                    ft.commit();
                }
                else if(i==2){
                    Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), getString(R.string.ingles), Toast.LENGTH_SHORT).show();
                    setLocale("en");
                    Fragment fragment = AjustesFragment.this;
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
        setLocale(language);
    }
}
