package com.example.cardsvshumanity.logReg;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.cardsvshumanity.MainActivity;
import com.example.cardsvshumanity.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class registre extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private EditText correo;
    private EditText contra;
    private EditText nom;
    private ImageView iUsuari;
    private Uri imagenSeleccionada;
    private static final int PICK_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registre);
        correo=findViewById(R.id.eTxtCorreo3);
        contra=findViewById(R.id.eTxtPass3);
        nom=findViewById(R.id.eTxtNombreU);
        iUsuari=findViewById(R.id.imgUsuario);
        mAuth = FirebaseAuth.getInstance();
    }

    public void onClickRegistrarse(View view){
        if(correo.getText()!=null&&contra.getText()!=null&& !correo.getText().toString().isEmpty() && !contra.getText().toString().isEmpty()
        && !nom.getText().toString().isEmpty()){
            mAuth.createUserWithEmailAndPassword(correo.getText().toString(),contra.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        mAuth.signInWithEmailAndPassword(correo.getText().toString(),contra.getText().toString());
                        mUser=mAuth.getCurrentUser();
                        Intent listSong = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(listSong);
                    }else{
                        Toast.makeText(registre.this, Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            });
        }else{
            Toast.makeText(this,"campos vacios",Toast.LENGTH_LONG).show();
        }
    }

    public void onClickImagen(View view){
        seleccionar();
    }

    private void galeria(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    private void camara(){
        //Intent camera=new Intent(Intent.ACTION_PICK);
        //startActivityForResult(camera,PICK_IMAGE);
        Toast.makeText(getApplicationContext(),"falta codigo",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imagenSeleccionada = data.getData();
            iUsuari.setImageURI(imagenSeleccionada);
        }
    }

    private void seleccionar(){
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(getString(R.string.selecciona));
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                getString(R.string.galeria),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        galeria();
                    }
                });

        builder1.setNegativeButton(
                getString(R.string.camara),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        camara();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }


    public void onClickAtras(View view){
        finish();
    }
}
