package com.xokundevs.cardsvshumanity.repository;

import android.util.Log;

import com.xokundevs.cardsvshumanity.actiPartida.Jugador;
import com.xokundevs.cardsvshumanity.javaConCod.Codification;
import com.xokundevs.cardsvshumanity.javaConCod.Connection;
import com.xokundevs.cardsvshumanity.javaConCod.GameController;
import com.xokundevs.cardsvshumanity.javaConCod.SocketHandler;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceCreateGameDeckDataInput;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceCreateGameInput;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceCreateUserInput;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceEraseDeckInput;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceEraseUserInput;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceGetCardsDeckInput;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceJoinGameInput;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceLoginInput;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceSaveDeckBlackCardInput;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceSaveDeckInput;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceSaveDeckWhiteCardInput;
import com.xokundevs.cardsvshumanity.serviceoutput.ServiceCardBlackInfoOutput;
import com.xokundevs.cardsvshumanity.serviceoutput.ServiceCardWhiteInfoOutput;
import com.xokundevs.cardsvshumanity.serviceoutput.ServiceCreateGameOutput;
import com.xokundevs.cardsvshumanity.serviceoutput.ServiceGetCardsDeckOutput;
import com.xokundevs.cardsvshumanity.serviceoutput.ServiceGetGameDataOutput;
import com.xokundevs.cardsvshumanity.serviceoutput.ServiceGetGameDataItemOutput;
import com.xokundevs.cardsvshumanity.serviceoutput.ServiceJoinGameOutput;
import com.xokundevs.cardsvshumanity.serviceoutput.ServiceSimpleDeckInfoListOutput;
import com.xokundevs.cardsvshumanity.serviceoutput.ServiceSimpleDeckInfoOutput;
import com.xokundevs.cardsvshumanity.utils.ServiceError;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.crypto.SecretKey;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableEmitter;
import io.reactivex.rxjava3.core.CompletableOnSubscribe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;

import static com.xokundevs.cardsvshumanity.javaConCod.Connection.BORRA_BARAJA;
import static com.xokundevs.cardsvshumanity.javaConCod.Connection.AVALIABLE_LOBBIES;
import static com.xokundevs.cardsvshumanity.javaConCod.Connection.CONNECTAR_PARTIDA;
import static com.xokundevs.cardsvshumanity.javaConCod.Connection.CREAR_PARTIDA;
import static com.xokundevs.cardsvshumanity.javaConCod.Connection.GET_BASIC_INFO_BARAJA;
import static com.xokundevs.cardsvshumanity.javaConCod.Connection.GET_CARTAS_BARAJA;
import static com.xokundevs.cardsvshumanity.javaConCod.Connection.HOST;
import static com.xokundevs.cardsvshumanity.javaConCod.Connection.LOGIN_USER;
import static com.xokundevs.cardsvshumanity.javaConCod.Connection.OK;
import static com.xokundevs.cardsvshumanity.javaConCod.Connection.PORT;
import static com.xokundevs.cardsvshumanity.javaConCod.Connection.SAVE_BARAJA;
import static com.xokundevs.cardsvshumanity.javaConCod.Connection.SOCKET_DISCONNECTED;
import static com.xokundevs.cardsvshumanity.javaConCod.Connection.UNKNOWN_ERROR;
import static com.xokundevs.cardsvshumanity.javaConCod.Connection.USER_NOT_LOGINED;


public class CcaRepository {
    private static CcaRepository INSTANCE;

    private CcaRepository() {
    }

    public static CcaRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CcaRepository();
        }
        return INSTANCE;
    }

    public Observable<ServiceSimpleDeckInfoListOutput> getBarajaBasic() {
        return Observable.create(new ObservableOnSubscribe<ServiceSimpleDeckInfoListOutput>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<ServiceSimpleDeckInfoListOutput> emitter) throws Throwable {
                Log.d(Connection.class.getSimpleName(), "Connection code: " + GET_BASIC_INFO_BARAJA);
                try (Socket sk = new Socket()) {

                    if (Connection.getUser() == null) {
                        throw new ServiceError(USER_NOT_LOGINED);
                    }

                    ConnectSocket(sk);
                    sk.setSoTimeout(0);

                    SocketHandler skHandler = new SocketHandler(sk);

                    DataInputStream dis;
                    DataOutputStream dos;

                    dis = skHandler.getDis();
                    dos = skHandler.getDos();

                    //Envia orden
                    dos.writeInt(Connection.GET_BASIC_INFO_BARAJA);

                    //Lee clave publica
                    skHandler.recibePublicKeyEnviaSecretKey();

                    skHandler.enviarString(Connection.getUser().getEmail());
                    skHandler.enviarHex(Connection.getUser().getPassword());


                    int result = skHandler.recibirInt();
                    int error = (result == OK) ? 0 : skHandler.recibirInt();
                    if (result == OK) {
                        int sizeList = skHandler.recibirInt();

                        ArrayList<ServiceSimpleDeckInfoOutput> listBarajas = new ArrayList<>();
                        ServiceSimpleDeckInfoOutput baraja = null;

                        for (int i = 0; i < sizeList; i++) {

                            String nombreEmail = skHandler.recibirString();

                            String nombreBaraja = skHandler.recibirString();

                            String nombreUser = skHandler.recibirString();

                            int cantidadCartas = skHandler.recibirInt();

                            String idiomaBaraja = skHandler.recibirString();

                            baraja = new ServiceSimpleDeckInfoOutput();

                            baraja.setDeckEmail(nombreEmail);
                            baraja.setDeckName(nombreBaraja);
                            baraja.setDeckUsername(nombreUser);
                            baraja.setDeckSize(cantidadCartas);
                            baraja.setDeckLanguage(idiomaBaraja);
                            baraja.setEditable(Connection.getEmail().equals(baraja.getDeckEmail()));

                            listBarajas.add(baraja);
                        }
                        ServiceSimpleDeckInfoListOutput serviceSimpleDeckInfoListOutput = new ServiceSimpleDeckInfoListOutput();
                        serviceSimpleDeckInfoListOutput.setListBarajas(listBarajas);
                        emitter.onNext(serviceSimpleDeckInfoListOutput);
                        emitter.onComplete();
                    } else {
                        throw new ServiceError(error);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    emitter.onError(new ServiceError(SOCKET_DISCONNECTED));
                } catch (ServiceError e) {
                    emitter.onError(e);
                } catch (Exception e) {
                    emitter.onError(new ServiceError(UNKNOWN_ERROR));
                    e.printStackTrace();
                }
            }
        });
    }

    public Completable eraseUserDeck(ServiceEraseDeckInput input) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@NonNull CompletableEmitter emitter) throws Throwable {
                Log.d(Connection.class.getSimpleName(), "Connection code: " + BORRA_BARAJA);
                try (Socket sk = new Socket()) {

                    ConnectSocket(sk);
                    sk.setSoTimeout(0);

                    SocketHandler skHandler = new SocketHandler(sk);

                    DataInputStream dis;
                    DataOutputStream dos;

                    dis = skHandler.getDis();
                    dos = skHandler.getDos();

                    //Envia orden
                    dos.writeInt(Connection.BORRA_BARAJA);

                    skHandler.recibePublicKeyEnviaSecretKey();

                    skHandler.enviarString(input.getUserEmail());
                    skHandler.enviarHex(input.getUserPassword());
                    skHandler.enviarString(input.getTargetDeckName());

                    int result = skHandler.recibirInt();
                    if (result == OK) {
                        emitter.onComplete();
                    } else {
                        int error = skHandler.recibirInt();
                        throw new ServiceError(error);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    emitter.onError(new ServiceError(SOCKET_DISCONNECTED));
                } catch (ServiceError e) {
                    emitter.onError(e);
                } catch (Exception e) {
                    emitter.onError(new ServiceError(UNKNOWN_ERROR));
                    e.printStackTrace();
                }
            }
        });
    }

    public Completable saveUserDeck(ServiceSaveDeckInput input) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@NonNull CompletableEmitter emitter) throws Throwable {
                Log.d(Connection.class.getSimpleName(), "Connection code: " + SAVE_BARAJA);
                try (Socket sk = new Socket()) {

                    if (Connection.getUser() == null) {
                        throw new ServiceError(USER_NOT_LOGINED);
                    }

                    ConnectSocket(sk);
                    sk.setSoTimeout(0);

                    SocketHandler skHandler = new SocketHandler(sk);

                    DataInputStream dis;
                    DataOutputStream dos;

                    dis = skHandler.getDis();
                    dos = skHandler.getDos();

                    //Envia orden
                    dos.writeInt(Connection.SAVE_BARAJA);

                    skHandler.recibePublicKeyEnviaSecretKey();

                    skHandler.enviarString(Connection.getEmail());
                    skHandler.enviarHex(Connection.getPassword());

                    skHandler.enviarString(input.getDeckName());
                    skHandler.enviarString(input.getDeckLanguage());
                    skHandler.enviarInt(input.getDeckSize());

                    for (ServiceSaveDeckBlackCardInput c : input.getBlackCards()) {
                        //1 si es carta negra
                        skHandler.enviarInt(1);
                        skHandler.enviarString(c.getNombre());
                        skHandler.enviarInt(c.getNumEspacios());
                    }

                    for (ServiceSaveDeckWhiteCardInput c : input.getWhiteCards()) {
                        //0 si es cara blanca
                        skHandler.enviarInt(0);
                        skHandler.enviarString(c.getNombre());
                    }

                    int result = skHandler.recibirInt();
                    if (result == OK) {
                        emitter.onComplete();
                    } else {
                        int error = skHandler.recibirInt();
                        throw new ServiceError(error);
                    }
                } catch (IOException e) {
                    emitter.onError(new ServiceError(SOCKET_DISCONNECTED));
                } catch (ServiceError ex) {
                    emitter.onError(ex);
                } catch (Exception e) {
                    Log.e(Connection.class.getSimpleName(), e.getMessage());
                    emitter.onError(new ServiceError(UNKNOWN_ERROR));
                }
            }
        });
    }

    public Completable logInUser(final ServiceLoginInput input) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@NonNull CompletableEmitter emitter) throws Throwable {
                Log.d(Connection.class.getSimpleName(), "Connection code: " + LOGIN_USER);
                try (Socket sk = new Socket()) {

                    ConnectSocket(sk);

                    sk.setSoTimeout(0);

                    SocketHandler skHandler = new SocketHandler(sk);


                    DataInputStream dis;
                    DataOutputStream dos;

                    dis = skHandler.getDis();
                    dos = skHandler.getDos();

                    //Envia orden
                    dos.writeInt(Connection.LOGIN_USER);

                    //Lee clave publica
                    skHandler.recibePublicKeyEnviaSecretKey();

                    //envia email contrasenya y nombre codificados con clave simetrica
                    skHandler.enviarString(input.getEmail());

                    String hexPassword = Codification.toHex(Codification.generateHashCode(input.getPassword().getBytes(StandardCharsets.UTF_8)));
                    skHandler.enviarHex(hexPassword);

                    int i = skHandler.recibirInt();

                    if (i == OK) {
                        String name = skHandler.recibirString();
                        int wins = skHandler.recibirInt();

                        Connection.setUser(input.getEmail(), hexPassword, name, wins);
                        emitter.onComplete();
                    } else {
                        int error = skHandler.recibirInt();
                        throw new ServiceError(error);
                    }
                } catch (IOException e) {
                    emitter.onError(new ServiceError(SOCKET_DISCONNECTED));
                } catch (ServiceError ex) {
                    emitter.onError(ex);
                } catch (Exception e) {
                    Log.e(Connection.class.getSimpleName(), e.getMessage());
                    emitter.onError(new ServiceError(UNKNOWN_ERROR));
                }
                Log.d(Connection.class.getSimpleName(), "ACABA -- LogIn");
            }
        });
    }

    public Completable createUser(ServiceCreateUserInput param) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@NonNull CompletableEmitter emitter) throws Throwable {

                try (Socket sk = new Socket()) {

                    ConnectSocket(sk);

                    sk.setSoTimeout(0);
                    //sk = new Socket("192.168.137.1", PORT);
                    SocketHandler skHandler = new SocketHandler(sk);
                    DataInputStream dis;
                    DataOutputStream dos;

                    dis = skHandler.getDis();
                    dos = skHandler.getDos();

                    //Envia orden
                    dos.writeInt(Connection.CREATE_USER);

                    skHandler.recibePublicKeyEnviaSecretKey();

                    //envia email contrasenya y nombre codificados con clave simetrica
                    skHandler.enviarString(param.getEmail());
                    String hexPassword = Codification.toHex(Codification.generateHashCode(param.getPassword().getBytes(StandardCharsets.UTF_8)));
                    skHandler.enviarHex(hexPassword);

                    skHandler.enviarString(param.getUsername());

                    if (skHandler.recibirInt() == OK) {

                        Connection.newLoginUser(param.getEmail(), hexPassword, param.getUsername(), 0);
                        emitter.onComplete();

                    } else {
                        int error = skHandler.recibirInt();
                        throw new ServiceError(error);
                    }
                } catch (IOException e) {
                    emitter.onError(new ServiceError(SOCKET_DISCONNECTED));
                    e.printStackTrace();
                } catch (ServiceError e) {
                    emitter.onError(e);
                } catch (Exception e) {
                    emitter.onError(new ServiceError(UNKNOWN_ERROR));
                    e.printStackTrace();
                }
            }
        });
    }

    public Completable eraseUser(ServiceEraseUserInput params) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@NonNull CompletableEmitter emitter) throws Throwable {

                try (Socket socket = new Socket()) {

                    if(!Connection.isLogined()){
                        throw new ServiceError(USER_NOT_LOGINED);
                    }

                    ConnectSocket(socket);

                    SocketHandler skHandler = new SocketHandler(socket);
                    DataOutputStream dos = skHandler.getDos();
                    DataInputStream dis = skHandler.getDis();

                    //Envia la order
                    dos.writeInt(Connection.ERASE_USER);

                    skHandler.recibePublicKeyEnviaSecretKey();

                    String password = Codification.toHex(
                            Codification.generateHashCode(params.getPassword().getBytes(StandardCharsets.UTF_8))
                    );

                    skHandler.enviarHex(password);

                    skHandler.enviarString(Connection.getEmail());

                    int result = skHandler.recibirInt();
                    if (result == OK) {
                        emitter.onComplete();
                    } else {
                        emitter.onError(new ServiceError(skHandler.recibirInt()));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    emitter.onError(new ServiceError(SOCKET_DISCONNECTED));
                } catch(ServiceError e){
                    emitter.onError(e);
                } catch (Exception e) {
                    e.printStackTrace();
                    emitter.onError(new ServiceError(UNKNOWN_ERROR));
                }
            }
        });
    }

    public Observable<ServiceGetCardsDeckOutput> getCardsFromDeck(ServiceGetCardsDeckInput input) {
        return Observable.create(new ObservableOnSubscribe<ServiceGetCardsDeckOutput>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<ServiceGetCardsDeckOutput> emitter) throws Throwable {
                Log.d(Connection.class.getSimpleName(), "Connection code: " + GET_CARTAS_BARAJA);
                try (Socket sk = new Socket()) {

                    if (Connection.getUser() == null) {
                        throw new ServiceError(USER_NOT_LOGINED);
                    }

                    ConnectSocket(sk);
                    sk.setSoTimeout(0);

                    SocketHandler skHandler = new SocketHandler(sk);

                    DataInputStream dis;
                    DataOutputStream dos;

                    dis = skHandler.getDis();
                    dos = skHandler.getDos();

                    //Envia orden
                    dos.writeInt(Connection.GET_CARTAS_BARAJA);

                    skHandler.recibePublicKeyEnviaSecretKey();

                    skHandler.enviarString(input.getDeckEmailOwner());
                    skHandler.enviarString(input.getDeckName());

                    int result = skHandler.recibirInt();

                    if (result == OK) {

                        int numeroCartas = skHandler.recibirInt();

                        System.out.println(numeroCartas);

                        ArrayList<ServiceCardWhiteInfoOutput> cartasBlancas = new ArrayList<>();
                        ArrayList<ServiceCardBlackInfoOutput> cartasNegras = new ArrayList<>();

                        for (int i = 0; i < numeroCartas; i++) {

                            int isNegra = skHandler.recibirInt();


                            String texto = skHandler.recibirString();

                            int codigo = skHandler.recibirInt();

                            if (isNegra == 1) {
                                int cantidadEspacios = skHandler.recibirInt();

                                cartasNegras.add(new ServiceCardBlackInfoOutput(texto, cantidadEspacios));
                            } else {
                                cartasBlancas.add(new ServiceCardWhiteInfoOutput(texto));
                            }
                        }

                        ServiceGetCardsDeckOutput output = new ServiceGetCardsDeckOutput(cartasBlancas, cartasNegras);

                        emitter.onNext(output);
                        emitter.onComplete();
                    } else {
                        int error = skHandler.recibirInt();
                        throw new ServiceError(error);
                    }
                } catch (IOException e) {
                    emitter.onError(new ServiceError(SOCKET_DISCONNECTED));
                } catch (ServiceError e) {
                    emitter.onError(e);
                } catch (Exception e) {
                    emitter.onError(new ServiceError(UNKNOWN_ERROR));
                }
            }
        });
    }

    public Observable<ServiceCreateGameOutput> getCreateGame(ServiceCreateGameInput param) {
        return Observable.create(new ObservableOnSubscribe<ServiceCreateGameOutput>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<ServiceCreateGameOutput> emitter) throws Throwable {
                Log.d(Connection.class.getSimpleName(), "Connection code: " + CREAR_PARTIDA);
                try (Socket sk = new Socket()) {
                    Log.d(Connection.class.getSimpleName(), "EMPIEZA -- LogIn");

                    if (!Connection.isLogined()) {
                        throw new ServiceError(USER_NOT_LOGINED);
                    }

                    ConnectSocket(sk);

                    sk.setSoTimeout(0);

                    SocketHandler skHandler = new SocketHandler(sk);

                    DataInputStream dis;
                    DataOutputStream dos;

                    dis = skHandler.getDis();
                    dos = skHandler.getDos();

                    //Envia orden
                    dos.writeInt(CREAR_PARTIDA);

                    SecretKey secretKey = skHandler.recibePublicKeyEnviaSecretKey();

                    skHandler.enviarString(Connection.getEmail());
                    skHandler.enviarHex(Connection.getPassword());

                    int result = skHandler.recibirInt();

                    if (result == OK) {
                        skHandler.enviarString(param.getGameName());
                        skHandler.enviarString(param.getGamePass());
                        skHandler.enviarInt(param.getMaxPlayers());
                        skHandler.enviarInt(param.getDeckData().size());

                        for (ServiceCreateGameDeckDataInput deckData : param.getDeckData()) {
                            skHandler.enviarString(deckData.getDeckEmail());
                            skHandler.enviarString(deckData.getDeckName());
                        }

                        result = skHandler.recibirInt();
                        if (result == OK) {
                            ServiceCreateGameOutput output = new ServiceCreateGameOutput();
                            output.setSecretKey(secretKey);
                            output.setSocketHandler(skHandler);
                            emitter.onNext(output);
                        } else {
                            //ERROR IN GETTING DATA
                            int error = skHandler.recibirInt();
                            emitter.onError(new ServiceError(error));
                        }
                    } else {
                        //ERROR IN LOGIN
                        int error = skHandler.recibirInt();
                        emitter.onError(new ServiceError(error));
                    }
                } catch (IOException e) {
                    emitter.onError(new ServiceError(SOCKET_DISCONNECTED));
                } catch (ServiceError e) {
                    emitter.onError(e);
                } catch (Exception e) {
                    e.printStackTrace();
                    emitter.onError(new ServiceError(UNKNOWN_ERROR));
                }
            }
        });
    }

    public Observable<ServiceGetGameDataOutput> getAvaliableLobbies() {
        return Observable.create(new ObservableOnSubscribe<ServiceGetGameDataOutput>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<ServiceGetGameDataOutput> emitter) throws Throwable {
                Log.d(Connection.class.getSimpleName(), "Connection code: " + AVALIABLE_LOBBIES);
                try (Socket sk = new Socket()) {
                    if (!Connection.isLogined()) {
                        throw new ServiceError(USER_NOT_LOGINED);
                    }

                    Log.d(Connection.class.getSimpleName(), "Obtener partida");
                    ConnectSocket(sk);
                    sk.setSoTimeout(0);

                    SocketHandler skHandler = new SocketHandler(sk);


                    DataInputStream dis;
                    DataOutputStream dos;

                    dis = skHandler.getDis();
                    dos = skHandler.getDos();

                    dos.writeInt(AVALIABLE_LOBBIES);
                    skHandler.recibePublicKeyEnviaSecretKey();
                    int CantPartidas = skHandler.recibirInt();

                    ArrayList<ServiceGetGameDataItemOutput> listPartidas = new ArrayList<>();
                    for (int i = 0; i < CantPartidas; i++) {
                        String gameName = skHandler.recibirString();
                        String userName = skHandler.recibirString();
                        int userList = skHandler.recibirInt();
                        int maxPlayers = skHandler.recibirInt();

                        listPartidas.add(new ServiceGetGameDataItemOutput(gameName, userName, userList, maxPlayers));
                    }

                    ServiceGetGameDataOutput gameDataOutput = new ServiceGetGameDataOutput();
                    gameDataOutput.setGameDataList(listPartidas);

                    emitter.onNext(gameDataOutput);

                } catch (IOException e) {
                    ServiceError serviceError = new ServiceError(SOCKET_DISCONNECTED);
                    serviceError.initCause(e);
                    emitter.onError(serviceError);
                    e.printStackTrace();
                } catch (ServiceError e) {
                    emitter.onError(e);
                    e.printStackTrace();
                } catch (Exception ex) {
                    ServiceError serviceError = new ServiceError(UNKNOWN_ERROR);
                    serviceError.initCause(ex);
                    emitter.onError(serviceError);
                    serviceError.printStackTrace();
                }
            }
        });
    }

    public Observable<ServiceJoinGameOutput> joinGame(ServiceJoinGameInput params) {
        return Observable.create(new ObservableOnSubscribe<ServiceJoinGameOutput>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<ServiceJoinGameOutput> emitter) throws Throwable {

                Log.d(Connection.class.getSimpleName(), "Connection code: " + CONNECTAR_PARTIDA);
                try (Socket sk = new Socket()) {


                    if (!Connection.isLogined()) {
                        throw new ServiceError(USER_NOT_LOGINED);
                    }

                    ConnectSocket(sk);

                    sk.setSoTimeout(0);

                    SocketHandler skHandler = new SocketHandler(sk);

                    DataInputStream dis;
                    DataOutputStream dos;

                    dis = skHandler.getDis();
                    dos = skHandler.getDos();

                    //Envia orden
                    dos.writeInt(CONNECTAR_PARTIDA);

                    SecretKey secretKey = skHandler.recibePublicKeyEnviaSecretKey();

                    skHandler.enviarString(Connection.getEmail());
                    skHandler.enviarHex(Connection.getPassword());
                    skHandler.enviarString(params.getGameName());
                    skHandler.enviarString(params.getPasswordName());

                    ArrayList<Jugador> jugadores = new ArrayList<>();
                    int result;
                    do {
                        result = skHandler.recibirInt();
                        Log.d(Connection.class.getSimpleName(), String.valueOf(result));
                        if (result == GameController.NEW_PLAYER) {
                            jugadores.add(new Jugador(skHandler.recibirString(), skHandler.recibirString()));
                        }
                    } while (result == GameController.NEW_PLAYER);
                    if (result == OK) {
                        ServiceJoinGameOutput output = new ServiceJoinGameOutput(jugadores, skHandler, secretKey, params.getGameName());
                        emitter.onNext(output);
                    } else {
                        int error = skHandler.recibirInt();
                        throw new ServiceError(error);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    emitter.onError(new ServiceError(SOCKET_DISCONNECTED));
                } catch (ServiceError e) {
                    e.printStackTrace();
                    emitter.onError(e);
                } catch (Exception e) {
                    e.printStackTrace();
                    emitter.onError(new ServiceError(UNKNOWN_ERROR));
                }
            }
        });
    }

    private static void ConnectSocket(Socket sk) throws IOException {
        sk.connect(new InetSocketAddress(HOST, PORT), 10000);
    }
}
