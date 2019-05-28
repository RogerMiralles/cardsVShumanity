package com.example.cardsvshumanity.javaConCod;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.cardsvshumanity.cosasRecicler.Baraja;
import com.example.cardsvshumanity.cosasRecicler.CartaBlanca;
import com.example.cardsvshumanity.cosasRecicler.CartaNegra;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.crypto.SecretKey;

public class Connection {

    public static final int OK = 1;
    public static final int NO = -1;
    private static final int PORT = 55555;
    private static final String HOST = "192.168.137.1";
    public static final int BLOCK_SIZE = 1024;

    //ORDENES
    private static final int CREATE_USER = 101;
    private static final int LOGIN_USER = 102;
    private static final int ERASE_USER = 103;
    private static final int GET_BASIC_INFO_BARAJA = 104;
    private static final int GET_CARTAS_BARAJA = 105;

    //ERRORES
    public static final int SOCKET_DISCONNECTED = Integer.MIN_VALUE;
    public static final int CREATE_USER_ERROR_EXISTING_USER = -1;
    public static final int CREATE_USER_ERROR_INVALID_EMAIL = -2;
    public static final int CREATE_USER_ERROR_INVALID_PARAMETERS = -3;
    public static final int USER_ERROR_INVALID_PASSWORD = -4;
    public static final int USER_ERROR_NON_EXISTANT_USER = -5;
    public static final int BARAJA_ERROR_NON_EXISTANT_BARAJA = -6;


    public static final int USER_NOT_LOGINED = -101;
    public static final int UNKOWN_ERROR = -100;

    private static Usuario user;

    public static ConnectionThread RegistrarUsuario(Activity context, String email, String password,
                                 String name, Drawable image){
        return new ConnectionThread(CREATE_USER, context, email, password, name, image);
    }

    public static ConnectionThread LogInUsuario(Activity context, final String email, final String password){
        return new ConnectionThread(LOGIN_USER, context, email, password);
    }


    public static ConnectionThread borrarCuenta(Activity context, String password){
        return new ConnectionThread(ERASE_USER, context, password);
    }

    public static ConnectionThread getBarajasUser(Activity context){
        return new ConnectionThread(GET_BASIC_INFO_BARAJA, context);
    }

    public static ConnectionThread getCartasUser(Activity context, Baraja baraja){
        return new ConnectionThread(GET_CARTAS_BARAJA, context, baraja);
    }

    public static boolean logOut(){
        if(user!=null){
            user=null;
            return true;
        }
        return false;
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

        private Runnable runBegin, runEnd;
        private ErrorRunable runNo;
        private SuccessRunnable runOk;
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

                case ERASE_USER:
                    run = getRunEraseUser();
                    break;

                case GET_BASIC_INFO_BARAJA:
                    run = getRunGetInfoBasicBaraja();
                    break;

                case GET_CARTAS_BARAJA:
                    run = getRunGetCartasFromBaraja();
                    break;

                default:
                    run = null;
                    break;
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
                            throw new Exception(ex.getMessage());
                        }
                        sk.setSoTimeout(0);

                        SocketHandler skHandler = new SocketHandler(sk);


                        DataInputStream dis;
                        DataOutputStream dos;

                        dis = skHandler.getDis();
                        dos = skHandler.getDos();

                        //Envia orden
                        dos.writeInt(LOGIN_USER);

                        //Lee clave publica
                        SecretKey secretKey = skHandler.recibePublicKeyEnviaSecretKey();

                        //envia email contrasenya y nombre codificados con clave simetrica
                        skHandler.enviarString(email, secretKey);

                        String hexPassword = Codification.toHex(Codification.generateHashCode(password.getBytes(StandardCharsets.UTF_8)));
                        skHandler.enviarHex(hexPassword, secretKey);

                        int i = skHandler.recibirInt(secretKey);
                        if(i == OK){
                            String name;
                            File imageTemp, image;

                            name = skHandler.recibirString(secretKey);

                            long fileLength = skHandler.recibirLong(secretKey);

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

                            int wins = skHandler.recibirInt(secretKey);

                            user = new Usuario(email, hexPassword, name, image, wins);
                            if(runOk != null)
                                activityContext.runOnUiThread(runOk);
                        }
                        else{
                            if(runNo != null) {
                                runNo.setError(NO);
                                activityContext.runOnUiThread(runNo);
                            }
                        }
                    } catch (IOException e) {
                        Log.e(Connection.class.getSimpleName(), e.getMessage());
                        e.printStackTrace();
                        if(runNo != null){
                            runNo.setError(UNKOWN_ERROR);
                            activityContext.runOnUiThread(runNo);
                        }
                    }catch (Exception e) {
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
                            throw new Exception(ex.getMessage());
                        }

                        sk.setSoTimeout(0);
                        //sk = new Socket("192.168.137.1", PORT);
                        SocketHandler skHandler = new SocketHandler(sk);
                        DataInputStream dis;
                        DataOutputStream dos;

                        dis = skHandler.getDis();
                        dos = skHandler.getDos();

                        //Envia orden
                        dos.writeInt(CREATE_USER);

                        SecretKey secretKey = skHandler.recibePublicKeyEnviaSecretKey();

                        //envia email contrasenya y nombre codificados con clave simetrica
                        skHandler.enviarString(email, secretKey);

                        if(skHandler.recibirInt(secretKey) == OK){

                            String hexPassword = Codification.toHex(Codification.generateHashCode(password.getBytes(StandardCharsets.UTF_8)));
                            skHandler.enviarHex(hexPassword, secretKey);

                            skHandler.enviarString(name, secretKey);

                            //Coge la imagen y crea un fichero;
                            Bitmap bitmap = ((BitmapDrawable) image).getBitmap();
                            ByteArrayOutputStream streamOutput = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, streamOutput);

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
                            skHandler.enviarString(".png", secretKey);

                            skHandler.enviarLong(imagenCodificada.length(), secretKey);

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

                            user = new Usuario(email, hexPassword, name, f, 0);
                            if (runOk != null)
                                activityContext.runOnUiThread(runOk);
                        }
                        else {
                            int error = skHandler.recibirInt(secretKey);
                            if (runNo != null) {
                                runNo.setError(error);
                                activityContext.runOnUiThread(runNo);
                            }
                        }
                    }
                    catch (IOException e){
                        if (runNo != null) {
                            runNo.setError(UNKOWN_ERROR);
                            activityContext.runOnUiThread(runNo);
                        }
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try{
                            sk.close();
                        } catch (IOException | NullPointerException e) {}
                    }
                }
            };
        }

        private Runnable getRunEraseUser(){
            return new Runnable() {
                @Override
                public void run() {
                    Socket socket = null;
                    DataInputStream dis = null;
                    DataOutputStream dos = null;
                    try{
                        socket = new Socket();
                        try {
                            ConnectSocket(socket);
                        } catch (Exception e) {
                            if(runNo != null){
                                runNo.setError(SOCKET_DISCONNECTED);
                                activityContext.runOnUiThread(runNo);
                            }
                            throw new Exception(e.getMessage());
                        }

                        SocketHandler skHandler = new SocketHandler(socket);
                        dos = skHandler.getDos();
                        dis = skHandler.getDis();

                        //Envia la order
                        dos.writeInt(ERASE_USER);

                        SecretKey secretKey = skHandler.recibePublicKeyEnviaSecretKey();

                        String password = Codification.toHex(
                                Codification.generateHashCode(((String) arguments[0]).getBytes(StandardCharsets.UTF_8))
                        );

                        skHandler.enviarHex(password, secretKey);

                        skHandler.enviarString(getEmail(), secretKey);

                        int result = skHandler.recibirInt(secretKey);
                        if(result == OK){
                            if(runOk != null)
                                activityContext.runOnUiThread(runOk);
                        }
                        else{
                            if(runNo != null){
                                runNo.setError(skHandler.recibirInt(secretKey));
                                activityContext.runOnUiThread(runNo);
                            }
                        }
                    } catch (IOException e){
                        e.printStackTrace();
                        if(runNo != null){
                            runNo.setError(UNKOWN_ERROR);
                            activityContext.runOnUiThread(runNo);
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    finally {
                        try{
                            dos.close();
                        }catch (NullPointerException | IOException e){}
                        try{
                            dis.close();
                        }catch (NullPointerException | IOException e){}
                        try{
                            socket.close();
                        }catch (NullPointerException | IOException e){}
                    }
                }
            };
        }

        private Runnable getRunGetInfoBasicBaraja(){
            return new Runnable() {
                @Override
                public void run() {
                    Socket sk = null;
                    try {
                        if (activityContext == null)
                            throw new Exception("No context added");

                        Log.d(Connection.class.getSimpleName(), "EMPIEZA -- LogIn");

                        if(user == null){
                            if(runNo != null){
                                runNo.setError(USER_NOT_LOGINED);
                                activityContext.runOnUiThread(runNo);
                                throw new Exception("User is not logined");
                            }
                        }

                        sk = new Socket();
                        try {
                            ConnectSocket(sk);
                        } catch (IOException ex) {
                            if (runNo != null) {
                                runNo.setError(SOCKET_DISCONNECTED);
                                activityContext.runOnUiThread(runNo);
                            }
                            throw new Exception(ex.getMessage());
                        }
                        sk.setSoTimeout(0);

                        SocketHandler skHandler = new SocketHandler(sk);

                        DataInputStream dis;
                        DataOutputStream dos;

                        dis = skHandler.getDis();
                        dos = skHandler.getDos();

                        //Envia orden
                        dos.writeInt(GET_BASIC_INFO_BARAJA);

                        //Lee clave publica
                        SecretKey secretKey = skHandler.recibePublicKeyEnviaSecretKey();

                        skHandler.enviarString(user.email, secretKey);
                        skHandler.enviarHex(user.password, secretKey);


                        int result = skHandler.recibirInt(secretKey);
                        int error = (result == OK)? 0 : skHandler.recibirInt(secretKey);
                        if(result == OK){
                            int sizeList = skHandler.recibirInt(secretKey);
                            System.out.println(sizeList);
                            ArrayList<Object[]> args = new ArrayList<>();
                            for(int i = 0; i<sizeList; i++) {

                                String nombreEmail = skHandler.recibirString(secretKey);
                                System.out.println("Email: "+nombreEmail);

                                String nombreBaraja = skHandler.recibirString(secretKey);
                                System.out.println("Baraja: "+nombreBaraja);

                                String nombreUser = skHandler.recibirString(secretKey);
                                System.out.println("Usuario: "+nombreUser);

                                int cantidadCartas = skHandler.recibirInt(secretKey);

                                String idiomaBaraja = skHandler.recibirString(secretKey);

                                args.add(new Object[]{nombreBaraja, nombreEmail, nombreUser, cantidadCartas, idiomaBaraja});
                            }

                            if(runOk != null){
                                runOk.setArgument(args);
                                activityContext.runOnUiThread(runOk);
                            }
                        }
                        else {
                            if (runNo != null) {
                                runNo.setError(error);
                                activityContext.runOnUiThread(runNo);
                            }
                        }

                    } catch (IOException e){
                        e.printStackTrace();
                        if(runNo != null){
                            runNo.setError(UNKOWN_ERROR);
                            activityContext.runOnUiThread(runNo);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
        }

        private Runnable getRunGetCartasFromBaraja(){
            return new Runnable() {
                @Override
                public void run() {
                    Socket sk = null;
                    try {
                        if (activityContext == null)
                            throw new Exception("No context added");

                        Log.d(Connection.class.getSimpleName(), "EMPIEZA -- LogIn");

                        if (user == null) {
                            if (runNo != null) {
                                runNo.setError(USER_NOT_LOGINED);
                                activityContext.runOnUiThread(runNo);
                                throw new Exception("User is not logined");
                            }
                        }

                        sk = new Socket();
                        try {
                            ConnectSocket(sk);
                        } catch (IOException ex) {
                            if (runNo != null) {
                                runNo.setError(SOCKET_DISCONNECTED);
                                activityContext.runOnUiThread(runNo);
                            }
                            throw new Exception(ex.getMessage());
                        }
                        sk.setSoTimeout(0);

                        SocketHandler skHandler = new SocketHandler(sk);

                        DataInputStream dis;
                        DataOutputStream dos;

                        dis = skHandler.getDis();
                        dos = skHandler.getDos();

                        //Envia orden
                        dos.writeInt(GET_CARTAS_BARAJA);

                        SecretKey secretKey = skHandler.recibePublicKeyEnviaSecretKey();

                        Baraja baraja = (Baraja) arguments[0];

                        skHandler.enviarString(baraja.getEmail(), secretKey);
                        skHandler.enviarString(baraja.getNombre(), secretKey);

                        int result = skHandler.recibirInt(secretKey);
                        int error = (result == OK)? 0 : skHandler.recibirInt(secretKey);

                        if(result == OK) {

                            int numeroCartas = skHandler.recibirInt(secretKey);

                            System.out.println(numeroCartas);

                            ArrayList<CartaBlanca> cartasBlancas = new ArrayList<>();
                            ArrayList<CartaNegra> cartasNegras = new ArrayList<>();

                            for (int i = 0; i < numeroCartas; i++) {

                                int isNegra = skHandler.recibirInt(secretKey);


                                String texto = skHandler.recibirString(secretKey);

                                int codigo = skHandler.recibirInt(secretKey);

                                if (isNegra == 1) {
                                    int cantidadEspacios = skHandler.recibirInt(secretKey);

                                    cartasNegras.add(new CartaNegra(baraja.getEmail(), texto, codigo, cantidadEspacios));
                                } else {
                                    cartasBlancas.add(new CartaBlanca(baraja.getEmail(), texto, codigo));
                                }
                            }

                            Object[] listas = new Object[2];
                            listas[0] = cartasBlancas;
                            listas[1] = cartasNegras;

                            if(runOk != null){
                                System.out.println("runOk");
                                runOk.setArgument(listas);
                                activityContext.runOnUiThread(runOk);
                            }
                        }
                        else{
                            if(runNo != null){
                                runNo.setError(error);
                                activityContext.runOnUiThread(runNo);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        if(runNo != null){
                            runNo.setError(UNKOWN_ERROR);
                            activityContext.runOnUiThread(runNo);
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            };
        }

        private static void ConnectSocket(Socket sk) throws IOException {
            sk.connect(new InetSocketAddress(HOST,PORT),10000);
        }

        public void setRunNo(ErrorRunable runNo) {
            this.runNo = runNo;
        }
        public void setRunOk(SuccessRunnable runOk) {
            this.runOk = runOk;
        }
        public void setRunBegin(Runnable runBegin) {
            this.runBegin = runBegin;
        }
        public void setRunEnd(Runnable runEnd) {
            this.runEnd = runEnd;
        }


        public static abstract  class SuccessRunnable implements Runnable{
            private Object argument = null;
            private void setArgument(Object arguments){
                this.argument = arguments;
            }

            public Object getArguments() {
                return argument;
            }
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


