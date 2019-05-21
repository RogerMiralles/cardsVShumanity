package com.example.cardsvshumanity.javaConCod;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;

import com.example.cardsvshumanity.R;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

public class Connection {

    private static final int OK = 1;
    private static final int NO = -1;
    private static final int PORT = 55555;

    private static final int CREATE_USER = 101;
    private static final int LOGIN_USER = 102;
    public static final int BLOCK_SIZE = 1024;

    private static Connection INSTANCE;
    private Context context;

    private Usuario user;
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

                    sk = new Socket("192.168.137.1",PORT);
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
                    dos.writeUTF(Codification.encodeWithSimetricKey(email.getBytes(StandardCharsets.UTF_8), secretKey, true));
                    dos.writeUTF(Codification.encodeWithSimetricKey(
                            Codification.generateHashCode(password.getBytes(StandardCharsets.UTF_8))
                            , secretKey, true
                            )
                    );
                    dos.writeUTF(Codification.encodeWithSimetricKey(name.getBytes(StandardCharsets.UTF_8), secretKey, true));

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
                    File imagenCodificada = Codification.encodeFileWithSymmetricKey(context, f, "encodedImage", secretKey, true);

                    //envia la imagen
                    ///Primero se envia el Formato y longitud
                    dos.writeUTF(Codification.encodeWithSimetricKey(".jpeg".getBytes(StandardCharsets.UTF_8), secretKey, true));
                    dos.writeUTF(
                            Codification.encodeWithSimetricKey(
                                    Codification.fromHex(
                                            Codification.parseLongToHex(
                                                    imagenCodificada.length()
                                            )
                                    )
                                    , secretKey, true
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

                    imagenCodificada.delete();

                    final int result = dis.readInt();
                    final int error = (result == OK)? 0 : dis.readInt();

                    if(result == OK){
                        user = new Usuario(email, password, name, f);
                    }

                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String message;
                            if(result == OK){
                                message=context.getString(R.string.noError);
                            }
                            else{
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
                            }
                            chivato(message);
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

    private void chivato(String mensajes){
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage(mensajes);
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                context.getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void LogInUsuario(final Runnable runnable, final String email, final String password){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket sk = null;
                try {
                    if (context == null)
                        throw new Exception("No context added");

                    Log.d(Connection.class.getSimpleName(), "EMPIEZA -- LogIn");

                    sk = new Socket("192.168.137.1", PORT);
                    DataInputStream dis;
                    DataOutputStream dos;

                    dis = new DataInputStream(sk.getInputStream());
                    dos = new DataOutputStream(sk.getOutputStream());

                    //Envia orden
                    dos.writeInt(LOGIN_USER);

                    //Lee clave publica
                    String pkHex = dis.readUTF();

                    //Genera simmetric key y codifica
                    SecretKey secretKey = Codification.generateNewSimetricKey();
                    String secretKeyCoded = Codification.encodeWithPublicKey(pkHex, secretKey.getEncoded());

                    //Envia simmetric key
                    dos.writeUTF(secretKeyCoded);

                    //envia email contrasenya y nombre codificados con clave simetrica
                    dos.writeUTF(Codification.encodeWithSimetricKey(email.getBytes(StandardCharsets.UTF_8), secretKey, true));
                    dos.writeUTF(Codification.encodeWithSimetricKey(
                            Codification.generateHashCode(password.getBytes(StandardCharsets.UTF_8))
                            , secretKey, true
                            )
                    );

                    int i = dis.readInt();
                    if(i == OK){
                        String name;
                        File imageTemp, image;

                        name = new String(Codification.fromHex(
                                Codification.encodeWithSimetricKey(Codification.fromHex(dis.readUTF()),secretKey, false))
                        );

                        long fileLength = Codification.parseHexToLong(
                                Codification.encodeWithSimetricKey(Codification.fromHex(dis.readUTF()), secretKey, false)
                        );

                        if(fileLength != 0) {
                            File f = context.getExternalFilesDir(null);
                            imageTemp = new File(f, "imageEncoded");

                            if(!imageTemp.exists()){
                                imageTemp.createNewFile();
                            }

                            DataOutputStream dosFile = new DataOutputStream(new FileOutputStream(imageTemp));

                            long count = 0;
                            byte[] dataReader = new byte[BLOCK_SIZE];
                            while (fileLength > count) {
                                int readed;
                                if (fileLength - count > BLOCK_SIZE) {
                                    readed = dis.read(dataReader, 0, BLOCK_SIZE);
                                } else {
                                    readed = dis.read(dataReader, 0, (int) (fileLength - count));
                                }
                                count += readed;
                                dosFile.write(dataReader, 0, readed);
                            }
                            dosFile.flush();
                            dosFile.close();

                            image = Codification.encodeFileWithSymmetricKey(context, imageTemp, "image", secretKey, false);
                            imageTemp.delete();
                        }
                        else{
                            image = null;
                        }

                        user = new Usuario(email, password, name, image);
                        ((Activity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "OK", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    Log.d(Connection.class.getSimpleName(), "Acaba -- LogIn");
                    try{
                        runnable.run();
                        sk.close();
                    }catch(NullPointerException | IOException e){}
                }
            }
        }).start();
    }

    public boolean isLogined(){
        return user != null;
    }

    public String getEmail(){
        return (user == null)?  null : user.email;
    }

    public File getImage(){
        return (user == null)?  null : user.drawable;
    }

    public String getName(){
        return (user == null)?  null : user.name;
    }

    private class Usuario {
        private String email, password, name;
        private File drawable;

        private Usuario(String email, String password, String name, File drawable){
            this.email = email;
            this.name = name;
            this.password = password;
            this.drawable = drawable;
        }
    }
}

