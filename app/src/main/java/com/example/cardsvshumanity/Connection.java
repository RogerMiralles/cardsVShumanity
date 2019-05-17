package com.example.cardsvshumanity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import javax.crypto.SecretKey;

public class Connection {

    private static final int OK = 1;
    private static final int NO = -1;

    private static final int CREATE_USER = 101;
    public static final int BLOCK_SIZE = 1024;

    private static Connection INSTANCE;
    private Context context;
    static {
        try {
            INSTANCE = new Connection();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    private Connection() throws IllegalAccessException {
        if(INSTANCE != null){
            throw new IllegalAccessException("Only one instance of connection must exists");
        }
    }

    public static Connection getInstance(Context context){
        if(context != null)
            INSTANCE.context = context;
        return INSTANCE;
    }

    public static Connection getInstance(){
        return INSTANCE;
    }

    public void RegistrarUsuario(final Runnable runnable, final String email, final String password,
                                 final String name, final Drawable image){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket sk = null;
                try{
                    if(context == null)
                        throw new Exception("No context added");

                    sk = new Socket("192.168.137.1",55555);
                    DataInputStream dis;
                    DataOutputStream dos;

                    dis = new DataInputStream(sk.getInputStream());
                    dos = new DataOutputStream(sk.getOutputStream());

                    //Envia orden
                    dos.writeInt(CREATE_USER);

                    //Lee clave publica
                    String pkHex = dis.readUTF();

                    //Genera simmetric key y codifica
                    SecretKey secretKey = Codification.generateNewSimetricKey();
                    String secretKeyCoded = Codification.encodeWithPublicKey(pkHex, secretKey.getEncoded());

                    //Envia simmetric key
                    dos.writeUTF(secretKeyCoded);

                    //envia email contrasenya y nombre codificados con clave simetrica
                    dos.writeUTF(Codification.encodeWithSimetricKey(email.getBytes(StandardCharsets.UTF_8), secretKey));
                    dos.writeUTF(Codification.encodeWithSimetricKey(
                            Codification.generateHashCode(password.getBytes(StandardCharsets.UTF_8))
                            , secretKey
                            )
                    );
                    dos.writeUTF(Codification.encodeWithSimetricKey(name.getBytes(StandardCharsets.UTF_8), secretKey));

                    //Coge la imagen y crea un fichero;
                    Bitmap bitmap = ((BitmapDrawable) image).getBitmap();
                    ByteArrayOutputStream streamOutput = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, streamOutput);


                    File f = new File(context.getExternalFilesDir(null), "image");
                    if(!f.exists()){
                        f.createNewFile();
                    }

                    DataOutputStream fileDos = new DataOutputStream(new FileOutputStream(f));


                    streamOutput.writeTo(fileDos);
                    fileDos.flush();
                    fileDos.close();

                    //codifica la imagen
                    File imagenCodificada = Codification.encodeFileWithSymmetricKey(context, f, "encodedImage", secretKey);

                    //envia la imagen
                    ///Primero se envia el Formato y longitud
                    dos.writeUTF(Codification.encodeWithSimetricKey(".jpeg".getBytes(StandardCharsets.UTF_8), secretKey));
                    dos.writeUTF(
                            Codification.encodeWithSimetricKey(
                                    Codification.fromHex(
                                            Codification.parseLongToHex(
                                                    imagenCodificada.length()
                                            )
                                    )
                                    , secretKey
                            )
                    );

                    DataInputStream fileInput = new DataInputStream(new FileInputStream(imagenCodificada));
                    long length = imagenCodificada.length();
                    long actual = 0;
                    byte[] datos = new byte[BLOCK_SIZE];
                    while(length > actual){
                        int leido;
                        if(length-actual > BLOCK_SIZE){
                            leido = fileInput.read(datos, 0, BLOCK_SIZE);
                        }
                        else{
                            leido = fileInput.read(datos, 0, (int) (length-actual));
                        }
                        actual+=leido;
                        dos.write(datos, 0, leido);
                    }

                    final int result = dis.readInt();
                    final int error = (result == 1)? 0 : dis.readInt();
                        ((Activity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(result == 1){
                                    Toast.makeText(context, "Te has registrado correctamente", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    String message;
                                    switch(error){
                                        case -1:
                                            message = context.getString(R.string.error_existing_email);
                                            break;
                                        case -2:
                                            message = context.getString(R.string.error_invalid_email);
                                            break;
                                        case -3:
                                            message = context.getString(R.string.error_invalid_parameters);
                                            break;
                                        default:
                                            message = context.getString(R.string.error_unknown_error);
                                            break;
                                    }
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                }catch (Exception e){
                    Log.d("Connection", "ha fallado: "+ e.getMessage()
                    );
                    e.printStackTrace();
                }
                finally {
                    try{
                        sk.close();
                    } catch (IOException | NullPointerException e) {}
                    if(runnable != null)
                        runnable.run();
                }

            }
        }).start();
    }
}
