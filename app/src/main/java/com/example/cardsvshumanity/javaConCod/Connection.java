package com.example.cardsvshumanity.javaConCod;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.cardsvshumanity.actiPartida.CrearPartida;
import com.example.cardsvshumanity.actiPartida.Jugador;
import com.example.cardsvshumanity.actiPartida.Partida;
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
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.crypto.SecretKey;

public class Connection {

    public static final int OK = 1;
    public static final int NO = -1;
    private static final int PORT = 55555;
    private static final String HOST = "192.168.137.1";
    public static final int BLOCK_SIZE = 4096;

    //ORDENES
    private static final int CREATE_USER = 101;
    private static final int LOGIN_USER = 102;
    private static final int ERASE_USER = 103;
    private static final int GET_BASIC_INFO_BARAJA = 104;
    private static final int GET_CARTAS_BARAJA = 105;
    private static final int SAVE_BARAJA = 106;
    private static final int BORRA_BARAJA = 107;
    private static final int COGER_PARTIDA = 108;
    private static final int CREAR_PARTIDA = 109;
    private static final int CONNECTAR_PARTIDA = 110;

    //ERRORES
    public static final int CREATE_USER_ERROR_EXISTING_USER = -1;
    public static final int CREATE_USER_ERROR_INVALID_EMAIL = -2;
    public static final int CREATE_USER_ERROR_INVALID_PARAMETERS = -3;
    public static final int USER_ERROR_INVALID_PASSWORD = -4;
    public static final int USER_ERROR_NON_EXISTANT_USER = -5;
    public static final int BARAJA_ERROR_NON_EXISTANT_BARAJA = -6;
    public static final int CREATE_USER_ERROR_LONG_EMAIL = -7;
    public static final int CREATE_USER_ERROR_LONG_USERNAME = -8;
    public static final int CREATE_USER_ERROR_INVALID_USERNAME = -9;
    public static final int PARTIDA_ERROR_NON_EXISTANT_PARTIDA = -10;
    public static final int PARTIDA_ERROR_EXISTING_PARTIDA = -11;
    public static final int PARTIDA_ERROR_NO_ENTRAR_DENIED = -12;

    public static final int SOCKET_DISCONNECTED = -102;
    public static final int USER_NOT_LOGINED = -101;
    public static final int UNKOWN_ERROR = -100;

    private static Usuario user;


    /**
     *
     */
    public static ConnectionThread CogerPartida(Activity context){
        return new ConnectionThread(COGER_PARTIDA, context);
    }
    /**
     * Este metodo devuelve un Hilo especial que tiene como objetivo el registrar a un usuario.
     * <br/>
     * <br/>
     * En caso de error, el ErrorRunable que se le haya a&ntilde;adido al hilo tendra uno de los siguientes
     * errores:
     * <ul>
     * <li>
     * SOCKET_DISCONNECTED - No se ha podido conectar al servidor.
     * </li>
     * <li>
     * UNKNOWN_ERROR - Ha ocurrido un error durante la ejecucion del hilo. Es probable que ocurra
     * debido a que se ha desconectado del servidor.
     * </li>
     * <li>
     * CREATE_USER_ERROR_EXISTING_USER - Ya existe un usuario con ese email
     * </li>
     * <li>
     *     CREATE_USER_ERROR_INVALID_EMAIL - El email indicado no tiene el formato estandar
     *  de un email convencional. Ej: 'pedro@correo.es' es v&aacute;lido, 'pedrep@.com no es v&aacute;lido
     * </li>
     * <li>
     *     CREATE_USER_ERROR_INVALID_PARAMETERS - A la hora de registrar el usuario ha ocurrido un error
     *     relacionado con los parametros enviados.
     * </li>
     * <li>
     *     CREATE_USER_ERROR_LONG_EMAIL - El email que se ha enviado mas caracteres de lo que el servidor permite
     * </li>
     * <li>
     *     CREATE_USER_ERROR_LONG_USERNAME - El nombre de usuario indicado contiene mas caracteres de lo que
     *     el servidor permite
     * </li>
     * <li>
     *     CREATE_USER_ERROR_INVALID_USERNAME - El nombre de usuario no es v&aacute;lido. A fecha de 29/05/2019
     *     solo se permiten los siguientes caracteres:
     *     <ul>
     *         <li>
     *             De la 'A' a la 'Z', tanto en min&uacute;scula como en may&uacute;scula sin caracteres extra&ntilde;os.
     *         </li>
     *         <li>
     *             Las cifras del 0 al 9 y cualquier combinaci&oacute;n de estas no decimal
     *         </li>
     *         <li>
     *             El caracter '_'
     *         </li>
     *     </ul>
     * </li>
     * </ul>
     * @param context Actividad en la que se ejecutará el hilo
     * @param email Correo electronico a registrar
     * @param password Contraseña del correo electronico
     * @param name Nombre del usuario a registrar
     * @param image Imagen del usuario a guardar
     * @return Hilo especial preparado para ejecutar la orden
     */
    public static ConnectionThread RegistrarUsuario(Activity context, String email, String password,
                                 String name, Drawable image){
        return new ConnectionThread(CREATE_USER, context, email, password, name, image);
    }

    /**
     * Este metodo devuelve un Hilo especial que tiene como objetivo pedir la confirmaci&oacute;n de que la
     * contrase&ntilde;a y email son correctos y recibir sus datos.
     * <br/>
     * <br/>
     * En caso de error, el ErrorRunable que se le haya a&ntilde;adido al hilo tendra uno de los siguientes
     * errores:
     * <ul>
     * <li>
     * SOCKET_DISCONNECTED - No se ha podido conectar al servidor.
     * </li>
     * <li>
     * UNKNOWN_ERROR - Ha ocurrido un error durante la ejecucion del hilo. Es probable que ocurra
     * debido a que se ha desconectado del servidor.
     * </li>
     * <li>
     * USER_ERROR_INVALID_PASSWORD - La contrase&ntilde;a introducida no es coincide con la del servidor.
     * </li>
     * <li>
     *     USER_ERROR_NON_EXISTANT_USER - El servidor no ha encontrado al usuario, si ocurre este
     *     error se recomienda cerrar la sesi&oacute;n del usuario y volver a la pantalla principal.
     * </li>
     * </ul>
     * @param context Actividad en la que se ejecutar&aacute; el hilo
     * @param email Correo electronico que identifica al usuario
     * @param password Contrase&ntilde;a del usuario
     * @return Hilo especial preparado para ejecutar la orden
     */
    public static ConnectionThread LogInUsuario(Activity context, final String email, final String password){
        return new ConnectionThread(LOGIN_USER, context, email, password);
    }


    /**
     * Este metodo devuelve un Hilo especial que tiene como objetivo comunicar el borrado del usuario
     *  (y todo lo que tenga este en el servidor guardado) al servidor.
     * <br/>
     * <br/>
     * En caso de error, el ErrorRunable que se le haya a&ntilde;adido al hilo tendra uno de los siguientes
     * errores:
     * <ul>
     * <li>
     * SOCKET_DISCONNECTED - No se ha podido conectar al servidor.
     * </li>
     * <li>
     * UNKNOWN_ERROR - Ha ocurrido un error durante la ejecucion del hilo. Es probable que ocurra
     * debido a que se ha desconectado del servidor.
     * </li>
     * <li>
     * USER_ERROR_INVALID_PASSWORD - La contrase&ntilde;a introducida no es coincide con la del servidor.
     * </li>
     * <li>
     *     USER_ERROR_NON_EXISTANT_USER - El servidor no ha encontrado al usuario, si ocurre este
     *     error se recomienda cerrar la sesi&oacute;n del usuario y volver a la pantalla principal.
     * </li>
     * <li>
     *     USER_NOT_LOGINED - Este error ocurre cuando el usuario no se ha registrado.
     * </li>
     * </ul>
     * @param context actividad en la que se ejecutar&aacute; el hilo
     * @param password contrase&ntilde;a que ha escrito el usuario
     * @return Hilo especial preparado para ejecutar la orden
     */
    public static ConnectionThread borrarCuenta(Activity context, String password){
        return new ConnectionThread(ERASE_USER, context, password);
    }

    /**
     * Este metodo devuelve un Hilo especial que tiene como objetivo comunicar el obtener la información
     *  sobre las cartas del usuario y las barajas por defecto
     *  (y todo lo que tenga este en el servidor guardado) al servidor.
     * <br/>
     * <br/>
     * En caso de error, el ErrorRunable que se le haya a&ntilde;adido al hilo tendra uno de los siguientes
     * errores:
     * <ul>
     * <li>
     * SOCKET_DISCONNECTED - No se ha podido conectar al servidor.
     * </li>
     * <li>
     * UNKNOWN_ERROR - Ha ocurrido un error durante la ejecucion del hilo. Es probable que ocurra
     * debido a que se ha desconectado del servidor.
     * </li>
     * <li>
     * USER_ERROR_INVALID_PASSWORD - La contrase&ntilde;a introducida no es coincide con la del servidor.
     * </li>
     * <li>
     *     USER_ERROR_NON_EXISTANT_USER - El servidor no ha encontrado al usuario, si ocurre este
     *     error se recomienda cerrar la sesi&oacute;n del usuario y volver a la pantalla principal.
     * </li>
     * <li>
     *     USER_NOT_LOGINED - Este error ocurre cuando el usuario no esta registrado
     * </li>
     * </ul>
     * @param context Actividad en la que se ejecutará el hilo devuelto
     * @return Hilo especial preparado para ejecutar la orden
     */
    public static ConnectionThread getBarajasUser(Activity context){
        return new ConnectionThread(GET_BASIC_INFO_BARAJA, context);
    }

    /**
     * Este metodo devuelve un Hilo especial que tiene como objetivo comunicar al servidor la orden
     * de devolver las listas de cartas.
     * <br/>
     * <br/>
     * En caso de que todo haya salido bien el SuccessRunnable recibira como argumento un Object[]
     * que contendra en la posicion [0] un ArrayList&lt;CartaBlanca&gt; y en la posicion [1] otro
     * ArrayList&lt;CartaNegra&gt; que contendran las cartas del mazo.
     * <br/>
     * <br/>
     * En caso de error, el ErrorRunable que se le haya a&ntilde;adido al hilo tendra uno de los siguientes
     * errores:
     * <ul>
     * <li>
     * SOCKET_DISCONNECTED - No se ha podido conectar al servidor.
     * </li>
     * <li>
     * UNKNOWN_ERROR - Ha ocurrido un error durante la ejecucion del hilo. Es probable que ocurra
     * debido a que se ha desconectado del servidor.
     * </li>
     * <li>
     * BARAJA_ERROR_NON_EXISTANT_BARAJA - La baraja indicada no existe.
     * </li>
     * <li>
     *     USER_ERROR_NON_EXISTANT_USER - El servidor no ha encontrado al usuario, si ocurre este
     *     error se recomienda cerrar la sesi&oacute;n del usuario y volver a la pantalla principal.
     * </li>
     * <li>
     *     USER_NOT_LOGINED - Este error ocurre cuando el usuario no esta registrado
     * </li>
     * </ul>
     * @param context Actividad en la que se ejecuta este hilo
     * @param baraja Baraja al que pertenecen las cartas
     * @return Hilo especial preparado para ejecutar la orden
     */
    public static ConnectionThread getCartasUser(Activity context, Baraja baraja){
        return new ConnectionThread(GET_CARTAS_BARAJA, context, baraja);
    }

    /**
     * Este metodo devuelve un Hilo especial que tiene como objetivo comunicar al servidor la orden
     * de guardar una baraja.
     * <br/>
     * En caso de error, el ErrorRunable que se le haya a&ntilde;adido al hilo tendra uno de los siguientes
     * errores:
     * <ul>
     * <li>
     * SOCKET_DISCONNECTED - No se ha podido conectar al servidor.
     * </li>
     * <li>
     * UNKNOWN_ERROR - Ha ocurrido un error durante la ejecucion del hilo. Es probable que ocurra
     * debido a que se ha desconectado del servidor.
     * </li>
     * <li>
     * USER_ERROR_INVALID_PASSWORD - La contrase&ntilde;a que se ha enviado no es v&aacute;lida.
     * </li>
     * <li>
     *     USER_ERROR_NON_EXISTANT_USER - El servidor no ha encontrado al usuario, si ocurre este
     *     error se recomienda cerrar la sesi&oacute;n del usuario y volver a la pantalla principal.
     * </li>
     * <li>
     *     USER_NOT_LOGINED - Este error ocurre cuando el usuario no esta registrado
     * </li>
     * </ul>
     * @param context Actividad en la que se ejecuta este hilo
     * @param baraja Baraja a guardar del servidor
     * @param cartasBlancas cartas blancas a guardar
     * @param cartasNegras cartas negras a guarder
     * @return Hilo especial preparado para ejecutar la orden
     */
    public static ConnectionThread saveBaraja(Activity context, Baraja baraja, ArrayList<CartaBlanca> cartasBlancas, ArrayList<CartaNegra> cartasNegras){
        return new ConnectionThread(SAVE_BARAJA, context, baraja, cartasBlancas, cartasNegras);
    }

    /**
     * Este metodo devuelve un Hilo especial que tiene como objetivo comunicar al servidor la orden
     * de borrar una baraja.
     * <br/>
     * En caso de error, el ErrorRunable que se le haya a&ntilde;adido al hilo tendra uno de los siguientes
     * errores:
     * <ul>
     * <li>
     * SOCKET_DISCONNECTED - No se ha podido conectar al servidor.
     * </li>
     * <li>
     * UNKNOWN_ERROR - Ha ocurrido un error durante la ejecucion del hilo. Es probable que ocurra
     * debido a que se ha desconectado del servidor.
     * </li>
     * <li>
     * USER_ERROR_INVALID_PASSWORD - La contrase&ntilde;a que se ha enviado no es v&aacute;lida.
     * </li>
     * <li>
     *     USER_ERROR_NON_EXISTANT_USER - El servidor no ha encontrado al usuario, si ocurre este
     *     error se recomienda cerrar la sesi&oacute;n del usuario y volver a la pantalla principal.
     * </li>
     * <li>
     *     USER_NOT_LOGINED - Este error ocurre cuando el usuario no esta registrado
     * </li>
     * </ul>
     * @param context Actividad en la que se ejecuta este hilo
     * @param baraja Baraja a borrar del servidor
     * @return Hilo especial preparado para ejecutar la orden
     */
    public static ConnectionThread borraBaraja(Activity context, String baraja){
        return new ConnectionThread(BORRA_BARAJA, context, baraja);
    }

    public static  ConnectionThread crearPartida(Activity context, String nombrePartida, String contrasena, int numJugadores, ArrayList<Baraja> arrayList){
        return new ConnectionThread(CREAR_PARTIDA, context, nombrePartida, contrasena, numJugadores, arrayList);
    }

    public static ConnectionThread unirsePartida(Activity context, String partida, String password){
        return new ConnectionThread(CONNECTAR_PARTIDA, context, partida, password);
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

                case SAVE_BARAJA:
                    run = getRunSaveBaraja();
                    break;

                case BORRA_BARAJA:
                    run = getRunBorraBaraja();
                    break;

                case COGER_PARTIDA:
                    run = getRunPartidas(); // TODO Coger partida
                    break;

                case CREAR_PARTIDA:
                    run = getRunCrearPartida();
                    break;

                case CONNECTAR_PARTIDA:
                    run  = getRunConectarPartida();
                    break;

                default:
                    run = null;
                    break;
            }
            return run;
        }

        private Runnable getRunPartidas(){
            return new Runnable() {
                @Override
                public void run() {
                    Socket sk = null;
                    try {
                        if (activityContext == null)
                            throw new Exception("No context added");
                        Log.d(Connection.class.getSimpleName(), "Obtener partida");
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

                        dos.writeInt(COGER_PARTIDA);
                        SecretKey secretKey = skHandler.recibePublicKeyEnviaSecretKey();
                        int CantPartidas = skHandler.recibirInt(secretKey);
                        ArrayList<String[]> listPartidas = new ArrayList<>();
                        for (int i = 0; i < CantPartidas; i++){
                            String GameName = skHandler.recibirString(secretKey);
                            String UserName = skHandler.recibirString(secretKey);
                            String UserList = skHandler.recibirInt(secretKey)+"";
                            String MaxPlayers = skHandler.recibirInt(secretKey)+"";
                            String[] temp = new String[]{
                                    GameName,UserName,UserList,MaxPlayers
                            };
                            listPartidas.add(temp);
                        }

                        if(runOk != null){
                            runOk.setArgument(listPartidas);
                            activityContext.runOnUiThread(runOk);
                        }



                    }catch (IOException e){
                        if(runNo != null){
                            runNo.setError(UNKOWN_ERROR);
                            activityContext.runOnUiThread(runNo);
                        }
                        e.printStackTrace();
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                    }
            };
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
                        int error = (i == NO)? skHandler.recibirInt(secretKey) : 0;
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
                                runNo.setError(error);
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
                        String hexPassword = Codification.toHex(Codification.generateHashCode(password.getBytes(StandardCharsets.UTF_8)));
                        skHandler.enviarHex(hexPassword, secretKey);

                        skHandler.enviarString(name, secretKey);

                        if(skHandler.recibirInt(secretKey) == OK){

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

                            ArrayList<Object[]> args = new ArrayList<>();

                            for(int i = 0; i<sizeList; i++) {

                                String nombreEmail = skHandler.recibirString(secretKey);

                                String nombreBaraja = skHandler.recibirString(secretKey);

                                String nombreUser = skHandler.recibirString(secretKey);

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

        private Runnable getRunSaveBaraja() {
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
                        dos.writeInt(SAVE_BARAJA);

                        SecretKey secretKey = skHandler.recibePublicKeyEnviaSecretKey();

                        Baraja b = (Baraja) arguments[0];
                        ArrayList<CartaBlanca> cartasBlancas = (ArrayList<CartaBlanca>) arguments[1];
                        ArrayList<CartaNegra> cartasNegras = (ArrayList<CartaNegra>) arguments[2];

                        skHandler.enviarString(user.email, secretKey);
                        skHandler.enviarHex(user.password,secretKey);

                        int result = skHandler.recibirInt(secretKey);
                        int error = (result == NO)? skHandler.recibirInt(secretKey) : 0;
                        if(result == OK){
                            skHandler.enviarString(b.getNombre(), secretKey);
                            skHandler.enviarString(b.getIdioma(), secretKey);
                            skHandler.enviarInt(cartasBlancas.size()+cartasNegras.size(), secretKey);

                            for(CartaNegra c : cartasNegras){
                                //1 si es carta negra
                                skHandler.enviarInt(1, secretKey);
                                skHandler.enviarString(c.getNombre(), secretKey);
                                skHandler.enviarInt(c.getNumEspacios(), secretKey);
                            }

                            for(CartaBlanca c: cartasBlancas){
                                skHandler.enviarInt(0, secretKey);
                                skHandler.enviarString(c.getNombre(), secretKey);
                            }

                            if(runOk != null){
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
        }

        private Runnable getRunBorraBaraja(){
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
                        dos.writeInt(BORRA_BARAJA);

                        SecretKey secretKey = skHandler.recibePublicKeyEnviaSecretKey();

                        String nombreBaraja = (String) arguments[0];

                        skHandler.enviarString(user.email, secretKey);
                        skHandler.enviarHex(user.password, secretKey);
                        skHandler.enviarString(nombreBaraja, secretKey);

                        int result = skHandler.recibirInt(secretKey);
                        int error = (result == NO)? skHandler.recibirInt(secretKey) : 0;
                        if(result == OK){
                            if(runOk != null)
                                activityContext.runOnUiThread(runOk);
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
        }

        private Runnable getRunCrearPartida(){
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
                        dos.writeInt(CREAR_PARTIDA);

                        SecretKey secretKey = skHandler.recibePublicKeyEnviaSecretKey();

                        String nombrePartida = (String) arguments[0];
                        String contrasena = (String) arguments[1];
                        int maxPlayers = (int) arguments[2];
                        ArrayList<Baraja> listaBarajas = (ArrayList<Baraja>) arguments[3];

                        skHandler.enviarString(user.email, secretKey);
                        skHandler.enviarHex(user.password,secretKey);

                        int result = skHandler.recibirInt(secretKey);
                        int error = (result == NO)? skHandler.recibirInt(secretKey) : 1;

                        if(result == OK){
                            skHandler.enviarString(nombrePartida, secretKey);
                            skHandler.enviarString(contrasena, secretKey);
                            skHandler.enviarInt(maxPlayers, secretKey);
                            skHandler.enviarInt(listaBarajas.size(), secretKey);

                            for(Baraja baraja : listaBarajas){
                                skHandler.enviarString(baraja.getEmail(), secretKey);
                                skHandler.enviarString(baraja.getNombre(), secretKey);
                            }

                            result = skHandler.recibirInt(secretKey);
                            error = (result == NO)? skHandler.recibirInt(secretKey) : 2;
                            if(result == OK){
                                if(runOk != null){
                                    runOk.setArgument(new Object[]{skHandler, secretKey});
                                    activityContext.runOnUiThread(runOk);
                                }
                            }
                            else{
                                if(runNo != null){
                                    runNo.setError(error);
                                    activityContext.runOnUiThread(runNo);
                                }
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
                        if (runNo != null) {
                            runNo.setError(UNKOWN_ERROR);
                            activityContext.runOnUiThread(runNo);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
        }

        private Runnable getRunConectarPartida(){
            return new Runnable() {
                @Override
                public void run() {
                    Socket sk = null;
                    try {
                        if (activityContext == null)
                            throw new Exception("No context added");

                        Log.d(Connection.class.getSimpleName(), "EMPIEZA -- Connectartida");

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
                        dos.writeInt(CONNECTAR_PARTIDA);

                        SecretKey secretKey = skHandler.recibePublicKeyEnviaSecretKey();

                        String nombrePartida = (String) arguments[0];
                        String contraPartida = (String) arguments[1];

                        skHandler.enviarString(user.email, secretKey);
                        skHandler.enviarHex(user.password, secretKey);
                        skHandler.enviarString(nombrePartida, secretKey);
                        skHandler.enviarString(contraPartida, secretKey);

                        ArrayList<Jugador> jugadores = new ArrayList<>();
                        int result;
                        do{
                            result = skHandler.recibirInt(secretKey);
                            Log.d(Connection.class.getSimpleName(), String.valueOf(result));
                            if(result == GameController.NEW_PLAYER){
                                jugadores.add(new Jugador(skHandler.recibirString(secretKey), skHandler.recibirString(secretKey)));
                            }
                        }while(result == GameController.NEW_PLAYER);
                        if(result == OK) {
                            if(runOk != null){
                                Object[] objects = new Object[3];
                                objects[0] = jugadores;
                                objects[1] = skHandler;
                                objects[2] = secretKey;
                                runOk.setArgument(objects);
                                activityContext.runOnUiThread(runOk);
                            }
                        }
                        else{
                            int error = skHandler.recibirInt(secretKey);
                            if(runNo != null){
                                runNo.setError(error);
                                activityContext.runOnUiThread(runNo);
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        if (runNo != null) {
                            runNo.setError(UNKOWN_ERROR);
                            activityContext.runOnUiThread(runNo);
                        }
                    } catch (Exception e) {
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


