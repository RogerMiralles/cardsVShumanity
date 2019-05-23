package com.example.cardsvshumanity.javaConCod;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

public class Connection {

    public static final int OK = 1;
    public static final int NO = -1;
    private static final int PORT = 55555;

    private static final int CREATE_USER = 101;
    private static final int LOGIN_USER = 102;
    public static final int BLOCK_SIZE = 1024;


    //ERRORES
    public static final int CREATE_USER_ERROR_EXISTING_USER = -1;
    public static final int CREATE_USER_ERROR_INVALID_EMAIL = -2;
    public static final int CREATE_USER_ERROR_INVALID_PARAMETERS = -3;
    public static final int SOCKET_DISCONNECTED = Integer.MIN_VALUE;

    private static Usuario user;

    public static ConnectionThread RegistrarUsuario(Activity context, String email, String password,
                                 String name, Drawable image){
        return new ConnectionThread(CREATE_USER, context, email, password, name, image);
    }

    public static ConnectionThread LogInUsuario(Activity context, final String email, final String password){
        return new ConnectionThread(LOGIN_USER, context, email, password);
    }


    public static boolean logOut(){
        if(user!=null){
            user=null;
            return true;
        }
        return false;
    }

    public static void borrarCuenta(Context context){
        Toast.makeText(context, "no acabado. MSG desde Connection", Toast.LENGTH_SHORT).show();
    }

    public static boolean isLogined(){
        return user != null;
    }

    public static String getEmail(){
        return (user == null)?  null : user.email;
    }

    public static File getImage(){
        return (user == null)?  null : user.drawable;
    }

    public static String getName(){
        return (user == null)?  null : user.name;
    }

    public static Integer getWins(){
        return (user == null)? null: user.wins;
    }

    private static class Usuario {
        private String email, password, name;
        private File drawable;
        private int wins;
        private Usuario(String email, String password, String name, File drawable, int wins){
            this.email = email;
            this.name = name;
            this.password = password;
            this.drawable = drawable;
            this.wins = wins;
        }
    }

    public static class ConnectionThread extends Thread{

        private Runnable runBegin, runEnd, runOk;
        private ErrorRunable runNo;
        private int order;
        private Activity activityContext;
        private Object[] arguments;

        public ConnectionThread(int order,@NonNull Activity activityContext, Object... arguments){
            this.order = order;
            this.activityContext = activityContext;
            this.arguments = arguments;
        }

        @Override
        public void run() {
            if(runBegin != null)
                activityContext.runOnUiThread(runBegin);

            getCommand().run();

            if(runEnd != null)
                activityContext.runOnUiThread(runEnd);
        }

        private Runnable getCommand(){
            Runnable run;
            switch (order){
                case CREATE_USER:
                    run =  getRunCreateUser();
                    break;

                case LOGIN_USER:
                    run = getRunLogIn();
                    break;

                default:
                    run = null;
            }
            return run;
        }

        private Runnable getRunLogIn(){
            return new Runnable() {
                @Override
                public void run() {
                    Socket sk = null;
                    String email, password;
                    email = (String) arguments[0];
                    password = (String) arguments[1];
                    try {
                        if (activityContext == null)
                            throw new Exception("No context added");

                        Log.d(Connection.class.getSimpleName(), "EMPIEZA -- LogIn");


                        sk = new Socket();
                        try {
                            ConnectSocket(sk);
                        }
                        catch (IOException ex){
                            if(runNo != null) {
                                runNo.setError(SOCKET_DISCONNECTED);
                                activityContext.runOnUiThread(runNo);
                            }
                            throw ex;
                        }
                        sk.setSoTimeout(0);


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

                            String encodedLength = dis.readUTF();
                            long fileLength = Codification.parseHexToLong(
                                    Codification.encodeWithSimetricKey(Codification.fromHex(encodedLength), secretKey, false)
                            );
                            Log.d(Connection.class.getSimpleName(), "Encoded length: " +encodedLength + " || Calculated long: "+ fileLength);

                            if(fileLength != 0) {
                                File f = activityContext.getExternalFilesDir(null);
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
                                    if(readed != -1) {
                                        count += readed;
                                        dosFile.write(dataReader, 0, readed);
                                    }
                                }
                                dosFile.flush();
                                dosFile.close();

                                image = Codification.encodeFileWithSymmetricKey(activityContext, imageTemp, "image", secretKey, false);
                                imageTemp.delete();
                                Log.d(Connection.class.getSimpleName(), "Imagen recibida");
                                Log.d(Connection.class.getSimpleName(), "La imagen recibida no es null?: " + Boolean.toString(image != null));
                            }
                            else{
                                image = null;
                            }

                            int wins = Codification.parseHexToInt(Codification.encodeWithSimetricKey(Codification.fromHex(dis.readUTF()),secretKey,false));

                            user = new Usuario(email, password, name, image, wins);
                            if(runOk != null)
                            activityContext.runOnUiThread(runOk);
                        }
                        else{
                            if(runNo != null) {
                                runNo.setError(NO);
                                activityContext.runOnUiThread(runNo);
                            }
                        }
                    } catch (UnknownHostException e) {
                        Log.e(Connection.class.getSimpleName(), e.getMessage());
                        e.printStackTrace();
                    } catch (IOException e) {
                        Log.e(Connection.class.getSimpleName(), e.getMessage());
                        e.printStackTrace();
                    } catch (Exception e) {
                        Log.e(Connection.class.getSimpleName(), e.getMessage());
                        e.printStackTrace();
                    }
                    finally {
                        Log.d(Connection.class.getSimpleName(), "Acaba -- LogIn");
                        try{
                            sk.close();
                        }catch(NullPointerException | IOException ignored){}
                    }
                }
            };
        }

        private Runnable getRunCreateUser(){
            return new Runnable() {
                @Override
                public void run() {
                    Socket sk = null;
                    String email, password, name;
                    Drawable image;

                    email = (String) arguments[0];
                    password = (String) arguments[1];
                    name = (String) arguments[2];
                    image = (Drawable) arguments[3];
                    try {
                        if (activityContext == null)
                            throw new Exception("No context added");

                        sk = new Socket();
                        try {
                            ConnectSocket(sk);
                        }
                        catch (IOException ex){
                            if(runNo != null) {
                                runNo.setError(SOCKET_DISCONNECTED);
                                activityContext.runOnUiThread(runNo);
                            }
                            throw ex;
                        }

                        sk.setSoTimeout(0);
                        //sk = new Socket("192.168.137.1", PORT);
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

                        File f = new File(activityContext.getExternalFilesDir(null), "image");
                        if (!f.exists()) {
                            f.createNewFile();
                        }

                        DataOutputStream fileDos = new DataOutputStream(new FileOutputStream(f));

                        streamOutput.writeTo(fileDos);
                        fileDos.flush();
                        fileDos.close();

                        //codifica la imagen
                        File imagenCodificada = Codification.encodeFileWithSymmetricKey(activityContext, f, "encodedImage", secretKey, true);

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
                        while (length > actual) {
                            int leido;
                            if (length - actual > BLOCK_SIZE) {
                                leido = fileInput.read(datos, 0, BLOCK_SIZE);
                            } else {
                                leido = fileInput.read(datos, 0, (int) (length - actual));
                            }
                            actual += leido;
                            dos.write(datos, 0, leido);
                        }

                        imagenCodificada.delete();

                        final int result = dis.readInt();
                        final int error = (result == OK) ? 0 : dis.readInt();

                        if (result == OK) {
                            user = new Usuario(email, password, name, f, 0);
                            if(runOk != null)
                                activityContext.runOnUiThread(runOk);
                        }
                        else{
                            if(runNo != null) {
                                runNo.setError(error);
                                activityContext.runOnUiThread(runNo);
                            }
                        }

                    }catch (Exception e){
                        Log.d("Connection", "ha fallado: "+ e.getMessage()
                        );
                        e.printStackTrace();
                    }
                    finally {
                        try{
                            sk.close();
                        } catch (IOException | NullPointerException e) {}
                    }
                }
            };
        }

        private static void ConnectSocket(Socket sk) throws IOException {
            sk.connect(new InetSocketAddress("192.168.137.1",PORT),10000);
        }

        public void setRunNo(ErrorRunable runNo) {
            this.runNo = runNo;
        }
        public void setRunOk(Runnable runOk) {
            this.runOk = runOk;
        }
        public void setRunBegin(Runnable runBegin) {
            this.runBegin = runBegin;
        }
        public void setRunEnd(Runnable runEnd) {
            this.runEnd = runEnd;
        }

        public static abstract class ErrorRunable implements Runnable {

            private int error = 0;

            private void setError(int error){
                this.error = error;
            }

            public int getError(){
                return error;
            }
        }
    }
}


