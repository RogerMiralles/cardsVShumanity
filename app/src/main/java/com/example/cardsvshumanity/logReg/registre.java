package com.example.cardsvshumanity.logReg;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cardsvshumanity.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class registre extends AppCompatActivity {

    private Button mRegistro;
    private FirebaseAuth mAuth;
    private EditText correo;
    private EditText contra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registre);
        correo=findViewById(R.id.eTxtCorreo3);
        contra=findViewById(R.id.eTxtPass3);
        mAuth = FirebaseAuth.getInstance();
    }

    public void onClickRegistrarse(View view){
        if(correo.getText()!=null||contra.getText()!=null){
            mAuth.createUserWithEmailAndPassword(correo.getText().toString(),contra.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        finish();
                    }else{
                        Toast.makeText(registre.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            });
        }else{
            Toast.makeText(this,"campos vacios",Toast.LENGTH_LONG).show();
        }
    }
}
