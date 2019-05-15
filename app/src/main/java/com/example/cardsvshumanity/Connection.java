package com.example.cardsvshumanity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.tasks.Task;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

public class Connection {

    private static final int OK = 1;
    private static final int NO = -1;

    private static final int CREATE_USER = 101;
    public static final int BLOCK_SIZE = 1024;

    private static Connection INSTANCE;

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

    public static Connection getInstance(){
        return INSTANCE;
    }

    public void RegistrarUsuario(final Runnable runnable, final String email, final String password,
                                 final String name, final Drawable image){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Socket sk = new Socket("192.168.137.1",55555);
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

                    File f = new File(Environment.getExternalStorageDirectory(), "image");
                    if(!f.exists()){
                        f.createNewFile();
                    }

                    DataOutputStream fileDos = new DataOutputStream(new FileOutputStream(f));

                    fileDos.write(streamOutput.toByteArray());
                    fileDos.flush();
                    fileDos.close();

                    //codifica la imagen
                    File imagenCodificada = Codification.encodeFileWithSymmetricKey(f, "encodedImage", secretKey);

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
                    
                    dis.readInt();

                    sk.close();

                }catch (Exception e){
                    Log.d("Connection", "ha fallado");
                    e.printStackTrace();
                }
                finally {
                    if(runnable != null)
                        runnable.run();
                }

            }
        }).start();
    }
}
