package com.xokundevs.cardsvshumanity.actiLogReg;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.xokundevs.cardsvshumanity.R;
import com.xokundevs.cardsvshumanity.javaConCod.Connection;
import com.xokundevs.cardsvshumanity.presenter.CreateUserPresenter;
import com.xokundevs.cardsvshumanity.presenter.impl.CreateUserPresenterImpl;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceCreateUserInput;
import com.xokundevs.cardsvshumanity.utils.baseutils.BaseActivity;
import com.xokundevs.cardsvshumanity.utils.baseutils.BasePresenterActivity;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Registre extends BasePresenterActivity<CreateUserPresenter> implements CreateUserPresenter.View{

    private EditText correo;
    private EditText contra;
    private EditText nom;

    private String imagenS;
    private static final int PICK_IMAGE = 100;
    private static final int TAKE_PHOTO=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registre);
        setPresenter(new CreateUserPresenterImpl(this));
        correo=findViewById(R.id.eTxtCorreo3);
        contra=findViewById(R.id.eTxtPass3);
        nom=findViewById(R.id.eTxtNombreU);
        setTitle(getString(R.string.tituloRegistro));
    }

    public void onClickRegistrarse(View view){
        if(correo.getText()!=null&&contra.getText()!=null&& !correo.getText().toString().isEmpty() && !contra.getText().toString().isEmpty()
        && !nom.getText().toString().isEmpty()){
            if(nom.getText().toString().getBytes(StandardCharsets.UTF_8).length>15){
                AlertDialog.Builder builder = chivato(getString(R.string.mCarac));
                builder.setPositiveButton(R.string.ok, null);
                builder.show();
            }else{
                ServiceCreateUserInput param = new ServiceCreateUserInput(correo.getText().toString(), nom.getText().toString(), contra.getText().toString());
                getPresenter().onCreateUser(param);
            }
        }else{

            AlertDialog.Builder builder = chivato(getString(R.string.camposVacios));
            builder.setPositiveButton(R.string.ok, null);
            builder.create().show();
        }

    }

    public void onClickAtras(View view){
        finish();
    }

    private AlertDialog.Builder chivato(String mensajes){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(mensajes);
        builder1.setCancelable(false);

        return builder1;
    }

    @Override
    public void onCreateUserSuccess() {
        AlertDialog.Builder builder1 = chivato(getString(R.string.no_error_registro));
        builder1.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                setResult(RESULT_OK);
                finish();
            }
        });
        builder1.create().show();
    }

    @Override
    public void onCreateUserError(int error) {
        String mensaje=null;
        switch (error){
            case Connection.SOCKET_DISCONNECTED:
                mensaje=getString(R.string.noConexion);
                break;
            case Connection.CREATE_USER_ERROR_EXISTING_USER:
                mensaje=getString(R.string.error_existing_email);
                break;
            case Connection.CREATE_USER_ERROR_INVALID_EMAIL:
                mensaje=getString(R.string.error_invalid_email);
                break;
            case Connection.CREATE_USER_ERROR_INVALID_PARAMETERS:
                mensaje=getString(R.string.error_invalid_parameters);
                break;
            case Connection.UNKNOWN_ERROR:
                mensaje=getString(R.string.error_unknown_error);
                break;
            case Connection.CREATE_USER_ERROR_INVALID_USERNAME:
                break;
            case Connection.CREATE_USER_ERROR_LONG_EMAIL:
                break;
            case Connection.CREATE_USER_ERROR_LONG_USERNAME:
                break;
        }
        AlertDialog.Builder builder1 = chivato(mensaje);
        builder1.setPositiveButton(R.string.ok, null);
        builder1.create().show();
    }
}
