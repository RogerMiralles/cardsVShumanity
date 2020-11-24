package com.xokundevs.cardsvshumanity.actiLogReg;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.xokundevs.cardsvshumanity.R;
import com.xokundevs.cardsvshumanity.javaConCod.Connection;
import com.xokundevs.cardsvshumanity.presenter.LoginPresenter;
import com.xokundevs.cardsvshumanity.presenter.impl.LoginPresenterImpl;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceLoginInput;
import com.xokundevs.cardsvshumanity.utils.baseutils.BasePresenterActivity;

public class Login extends BasePresenterActivity<LoginPresenter> implements LoginPresenter.View, View.OnClickListener {

    private EditText correo;
    private EditText contra;
    private final int CREATE_USER = 101;

    private Button mBtnLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(getString(R.string.tituloLogin));
        setPresenter(new LoginPresenterImpl(this));

        bindViews();
        setUpViews();
    }

    private void bindViews(){
        correo=findViewById(R.id.eTxtCorreo);
        contra=findViewById(R.id.eTxtPass);
        mBtnLogIn = findViewById(R.id.btnLogear);
    }

    private void setUpViews(){
        mBtnLogIn.setOnClickListener(this);
    }

    public void onClickLogear() {

        if(correo.getText()!=null&&contra.getText()!=null&& !correo.getText().toString().isEmpty() && !contra.getText().toString().isEmpty()){
            getPresenter().logIn(new ServiceLoginInput(correo.getText().toString(), contra.getText().toString()));
            mBtnLogIn.setEnabled(false);
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

    @Override
    public void onLoginSuccess() {
        Log.d(Login.class.getSimpleName(), "Login Success");
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
        mBtnLogIn.setEnabled(true);
    }

    @Override
    public void onLoginFailure(int error) {
        Log.d(Login.class.getSimpleName(), "Login Success");
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok, null);
        switch (error){
            case Connection.INVALID_CREDENTIALS_ERROR:
            case Connection.USER_ERROR_NON_EXISTANT_USER:
                builder.setMessage(R.string.emailContraMal);
                break;
            case Connection.SOCKET_DISCONNECTED:
                builder.setMessage(R.string.noConexion);
                break;
            case Connection.UNKNOWN_ERROR:
                builder.setMessage(R.string.error_unknown_error);
                break;
        }
        builder.create().show();
        mBtnLogIn.setEnabled(true);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnLogear) {
            onClickLogear();
        }
    }
}
