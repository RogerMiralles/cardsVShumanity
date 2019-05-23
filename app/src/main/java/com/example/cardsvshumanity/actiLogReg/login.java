package com.example.cardsvshumanity.actiLogReg;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.cardsvshumanity.R;
import com.example.cardsvshumanity.javaConCod.Connection;

public class login extends AppCompatActivity {

    private EditText correo;
    private EditText contra;
    private final int CREATE_USER = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        correo=findViewById(R.id.eTxtCorreo);
        contra=findViewById(R.id.eTxtPass);
        setTitle(getString(R.string.tituloLogin));
    }

    public void onClickLogear(View view) {
        final AlertDialog alertDialog = chivato("Hola").create();

        if(correo.getText()!=null&&contra.getText()!=null&& !correo.getText().toString().isEmpty() && !contra.getText().toString().isEmpty()){
            Connection.ConnectionThread thread = Connection.LogInUsuario(this, correo.getText().toString(), contra.getText().toString());
            thread.setRunBegin(new Runnable() {
                @Override
                public void run() {
                    alertDialog.setMessage(getString(R.string.internet_dialog_cargando));
                    alertDialog.show();
                }
            });
            thread.setRunOk(new Runnable() {
                @Override
                public void run() {
                    alertDialog.dismiss();
                    AlertDialog.Builder builder = chivato(getString(R.string.jugar));
                    builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            setResult(RESULT_OK);
                            finish();
                        }
                    });
                    builder.create().show();
                }
            });

            thread.setRunNo(new Connection.ConnectionThread.ErrorRunable() {
                @Override
                public void run() {
                    alertDialog.dismiss();
                    if(getError() == Connection.NO){
                        AlertDialog.Builder builder = chivato("Contraseña y/o email incorrectos");
                        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();
                    }
                    else if(getError() == Connection.SOCKET_DISCONNECTED){
                        AlertDialog.Builder builder = chivato("No se ha podido conectar al servidor");
                        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();
                    }
                }
            });
            thread.start();
        }else{
            alertDialog.setMessage(getString(R.string.camposVacios));
            alertDialog.show();
        }
    }

    public void onClickRegistrar(View view) {
        Intent listSong = new Intent(getApplicationContext(), registre.class);
        startActivityForResult(listSong, CREATE_USER);
    }

    private AlertDialog.Builder chivato(String mensajes){
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(mensajes);
        builder1.setCancelable(false);

        return builder1;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CREATE_USER && resultCode == RESULT_OK){
            setResult(RESULT_OK);
            finish();
        }
    }
}
