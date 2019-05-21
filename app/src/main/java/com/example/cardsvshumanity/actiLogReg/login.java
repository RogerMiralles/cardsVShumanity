package com.example.cardsvshumanity.actiLogReg;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.example.cardsvshumanity.R;
import com.example.cardsvshumanity.javaConCod.Connection;

public class login extends AppCompatActivity {

    private EditText correo;
    private EditText contra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        correo=findViewById(R.id.eTxtCorreo);
        contra=findViewById(R.id.eTxtPass);
        setTitle(getString(R.string.tituloLogin));
    }

    public void onClickLogear(View view) {
        String mensajePAlert=null;
        if(correo.getText()!=null&&contra.getText()!=null&& !correo.getText().toString().isEmpty() && !contra.getText().toString().isEmpty()){
            Connection.getInstance(this).LogInUsuario(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, correo.toString(), contra.toString());
        }else{
            mensajePAlert=getString(R.string.camposVacios);
            //Toast.makeText(this,"campos vacios",Toast.LENGTH_LONG).show();
        }
        chivato(mensajePAlert);
    }

    public void onClickRegistrar(View view) {
        Intent listSong = new Intent(getApplicationContext(), registre.class);
        startActivity(listSong);
    }

    private void chivato(String mensajes){
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(mensajes);
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

}
