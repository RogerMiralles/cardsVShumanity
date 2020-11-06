package com.xokundevs.cardsvshumanity.actiLogReg;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.xokundevs.cardsvshumanity.R;
import com.xokundevs.cardsvshumanity.javaConCod.Connection;

public class Login extends AppCompatActivity {

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

        if(correo.getText()!=null&&contra.getText()!=null&& !correo.getText().toString().isEmpty() && !contra.getText().toString().isEmpty()){
            Connection.ConnectionThread thread = Connection.LogInUsuario(this, correo.getText().toString(), contra.getText().toString());

            final AlertDialog alertDialog = chivato(getString(R.string.internet_dialog_cargando)).create();

            thread.setRunBegin(new Runnable() {
                @Override
                public void run() {
                    alertDialog.show();
                }
            });

            thread.setRunOk(new Connection.ConnectionThread.SuccessRunnable() {
                @Override
                public void run() {
                    alertDialog.dismiss();
                    AlertDialog.Builder builder = chivato(getString(R.string.loginCorrecto));
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                    builder.setCancelable(false);
                    builder.setPositiveButton(R.string.ok, null);
                    switch (getError()){
                        case Connection.INVALID_CREDENTIALS_ERROR:
                        case Connection.USER_ERROR_NON_EXISTANT_USER:
                            builder.setMessage(R.string.emailContraMal);
                            break;
                        case Connection.SOCKET_DISCONNECTED:
                            builder.setMessage(R.string.noConexion);
                            break;
                        case Connection.UNKOWN_ERROR:
                            builder.setMessage(R.string.error_unknown_error);
                            break;
                    }
                    builder.create().show();
                    if(alertDialog.isShowing())
                        alertDialog.dismiss();
                }
            });

            thread.start();
        }else{
            AlertDialog.Builder builder = chivato(getString(R.string.camposVacios));
            builder.setPositiveButton(R.string.ok, null);
            builder.create().show();
        }
    }

    public void onClickRegistrar(View view) {
        Intent listSong = new Intent(getApplicationContext(), Registre.class);
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
