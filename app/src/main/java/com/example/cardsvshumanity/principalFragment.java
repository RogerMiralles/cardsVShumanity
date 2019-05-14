package com.example.cardsvshumanity;


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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cardsvshumanity.jugarPerfil.segundaVentana;
import com.example.cardsvshumanity.logReg.login;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;


/**
 * A simple {@link Fragment} subclass.
 */
public class principalFragment extends Fragment {


    public static final String RSA_ECB_OAEPWITHSHA_256_ANDMGF_1_PADDING = "RSA/ECB/PKCS1PADDING";

    public principalFragment() {
        // Required empty public constructor
    }

    private String mUser;
    private Button mJuegaIniciaSes;
    private Button mSalir;
    private final int codigo=0;
    private ImageButton mIbtn;
    private TextView txt;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView= inflater.inflate(R.layout.fragment_principal, container, false);
        //loadLocale();
        mUser=null;
        mSalir= rootView.findViewById(R.id.btnSalida);
        mSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUser=null;
                ponerCosasVisIn();
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
                if(mUser != null) {
                    Intent listSong = new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(), segundaVentana.class);
                    startActivity(listSong);
                }
                else{
                    //Intent listSong = new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(), login.class);
                    //startActivityForResult(listSong,codigo);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //Crea Socket
                                Socket sk = new Socket("192.168.137.1", 55555);
                                DataInputStream dis = new DataInputStream(sk.getInputStream());
                                DataOutputStream dos = new DataOutputStream(sk.getOutputStream());

                                //Lee clave publica
                                byte[] publicKeyEncoded = fromHex(dis.readUTF());
                                PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKeyEncoded));

                                KeyGenerator keyGen = KeyGenerator.getInstance("AES");
                                SecretKey secretKey = keyGen.generateKey();
                                byte[] rawKey = secretKey.getEncoded();

                                Cipher c = Cipher.getInstance(RSA_ECB_OAEPWITHSHA_256_ANDMGF_1_PADDING);
                                c.init(Cipher.ENCRYPT_MODE, publicKey);
                                byte[] asdfadsf = c.doFinal(rawKey);
                                dos.writeUTF(toHex(asdfadsf));
                            } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
                                Log.d("InternetClass", e.getMessage());
                            }
                        }
                    }).start();
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
        txt.setText(getString(R.string.cartas_contra_la_humanidad));
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
                    Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Traduciendo al castellano", Toast.LENGTH_SHORT).show();
                    setLocale("es");
                    //getActivity().recreate();
                    //Intent in=new Intent(getActivity().getApplicationContext(),MainActivity.class);
                    //in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    //getActivity().finish();
                    //startActivity(in);

                    Fragment fragment = principalFragment.this;
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

                    Fragment fragment = principalFragment.this;
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

                    Fragment fragment = principalFragment.this;
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


    public static byte[] fromHex(String hex){
        int length = hex.length()/2;
        byte[] array = new byte[length];
        for(int i = 0; i<length; i++){
            String firstPart = hex.charAt(i*2)+"", secondPart = hex.charAt(i*2+1)+"";
            byte by = 0;
            try{
                by = (byte) (Integer.parseInt(firstPart)*16);
            }catch(NumberFormatException e){
                switch(firstPart){
                    case "a":
                        by = (byte) (10*16);
                        break;
                    case "b":
                        by = (byte) (11*16);
                        break;
                    case "c":
                        by = (byte) (12*16);
                        break;
                    case "d":
                        by = (byte) (13*16);
                        break;
                    case "e":
                        by = (byte) (14*16);
                        break;
                    case "f":
                        by = (byte) (15*16);
                        break;
                }
            }
            try{
                by += (byte)(Integer.parseInt(secondPart));
            }catch(NumberFormatException e){
                switch(secondPart){
                    case "a":
                        by += 10;
                        break;
                    case "b":
                        by += 11;
                        break;
                    case "c":
                        by += 12;
                        break;
                    case "d":
                        by += 13;
                        break;
                    case "e":
                        by += 14;
                        break;
                    case "f":
                        by += 15;
                        break;
                }
            }
            array[i] = by;
        }
        return array;
    }

    public static String toHex(byte[] array){
        StringBuilder str = new StringBuilder();

        for(int i = 0; i<array.length; i++){
            int a = array[i] & 0xf;
            int b = (array[i] & 0xf0) >> 4;
            if(b < 10){
                str.append(b);
            }else{
                str.append(Integer.toHexString(b));
            }
            if(a < 10){
                str.append(a);
            }else{
                str.append(Integer.toHexString(a));
            }
        }

        return str.toString();
    }
}
