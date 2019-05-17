package com.example.cardsvshumanity.logReg;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cardsvshumanity.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText correo;
    private EditText contra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        correo=findViewById(R.id.eTxtCorreo);
        contra=findViewById(R.id.eTxtPass);
        mAuth=FirebaseAuth.getInstance();
    }

    public void onClickLogear(View view) {
        String mensajePAlert=null;
        if(correo.getText()!=null&&contra.getText()!=null&& !correo.getText().toString().isEmpty() && !contra.getText().toString().isEmpty()){
            mAuth.signInWithEmailAndPassword(correo.getText().toString(),contra.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        setResult(RESULT_OK);
                        finish();
                    }else{
                        //mensajePAlert= Objects.requireNonNull(task.getException()).getMessage();
                        Toast.makeText(login.this, Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            });
        }else{
            mensajePAlert=getString(R.string.camposVacios);
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
