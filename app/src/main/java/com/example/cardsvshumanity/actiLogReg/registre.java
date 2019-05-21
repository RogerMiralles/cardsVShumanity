package com.example.cardsvshumanity.actiLogReg;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.cardsvshumanity.javaConCod.Connection;
import com.example.cardsvshumanity.R;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class registre extends AppCompatActivity {

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
        setTitle(getString(R.string.tituloRegistro));
    }

    public void onClickRegistrarse(View view){
        String mensajePAlert=null;
        if(correo.getText()!=null&&contra.getText()!=null&& !correo.getText().toString().isEmpty() && !contra.getText().toString().isEmpty()
        && !nom.getText().toString().isEmpty()){
            if(nom.getText().toString().getBytes(StandardCharsets.UTF_8).length>15){
                mensajePAlert=getString(R.string.mCarac);
                chivato(mensajePAlert);
            }else{
                Connection.getInstance(this).RegistrarUsuario(new Runnable() {
                                                                  @Override
                                                                  public void run() {
                                                                      if(Connection.getInstance().isLogined()) {
                                                                          Intent intent = new Intent(registre.this, login.class);
                                                                          startActivity(intent);
                                                                      }
                                                                  }
                                                              }, correo.getText().toString(),
                        contra.getText().toString(), nom.getText().toString(), iUsuari.getDrawable());
            }
        }else{
            mensajePAlert=getString(R.string.camposVacios);
            chivato(mensajePAlert);
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
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(imagenSeleccionada!=null)
        outState.putString("uri", imagenSeleccionada.toString());

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        try {
            imagenSeleccionada = Uri.parse(savedInstanceState.getString("uri"));
            iUsuari.setImageURI(imagenSeleccionada);
        }catch(NullPointerException ignored){}
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
