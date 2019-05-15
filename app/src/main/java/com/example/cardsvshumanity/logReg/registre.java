package com.example.cardsvshumanity.logReg;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class registre extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private EditText correo;
    private EditText contra;
    private EditText nom;

    private ImageView iUsuari;
    private Uri imagenSeleccionada;
    private String imagenS;
    private static final int PICK_IMAGE = 100;
    private static final int TAKE_PHOTO=0;

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
        Intent pCamara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (pCamara.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (photoFile != null) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "MyPicture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "Photo taken on " + System.currentTimeMillis());
                imagenSeleccionada = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                pCamara.putExtra(MediaStore.EXTRA_OUTPUT, imagenSeleccionada);
                Toast.makeText(this,"a llegado",Toast.LENGTH_LONG).show();
                startActivityForResult(pCamara, TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
                imagenSeleccionada = data.getData();
                iUsuari.setImageURI(imagenSeleccionada);
        }else if (requestCode == TAKE_PHOTO && resultCode == RESULT_OK) {
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagenSeleccionada);
                iUsuari.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
                        permisos();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void permisos(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED&&ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
                 {

            if ( ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)&&ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},225);
                
            }
        } else {
            camara();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        imagenS = image.getAbsolutePath();
        return image;
    }

    public void onClickAtras(View view){
        finish();
    }
}
