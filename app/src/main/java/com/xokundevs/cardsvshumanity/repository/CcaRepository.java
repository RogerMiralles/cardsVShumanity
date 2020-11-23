package com.xokundevs.cardsvshumanity.repository;

import android.util.Log;

import com.xokundevs.cardsvshumanity.javaConCod.Codification;
import com.xokundevs.cardsvshumanity.javaConCod.Connection;
import com.xokundevs.cardsvshumanity.javaConCod.SocketHandler;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceCreateGameDeckDataInput;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceCreateGameInput;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceEraseDeckInput;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceGetCardsDeckInput;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceLoginInput;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceSaveDeckBlackCardInput;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceSaveDeckInput;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceSaveDeckWhiteCardInput;
import com.xokundevs.cardsvshumanity.serviceoutput.ServiceCardBlackInfoOutput;
import com.xokundevs.cardsvshumanity.serviceoutput.ServiceCardWhiteInfoOutput;
import com.xokundevs.cardsvshumanity.serviceoutput.ServiceCreateGameOutput;
import com.xokundevs.cardsvshumanity.serviceoutput.ServiceGetCardsDeckOutput;
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
import static com.xokundevs.cardsvshumanity.javaConCod.Connection.CREAR_PARTIDA;
import static com.xokundevs.cardsvshumanity.javaConCod.Connection.GET_BASIC_INFO_BARAJA;
import static com.xokundevs.cardsvshumanity.javaConCod.Connection.GET_CARTAS_BARAJA;
import static com.xokundevs.cardsvshumanity.javaConCod.Connection.HOST;
import static com.xokundevs.cardsvshumanity.javaConCod.Connection.LOGIN_USER;
import static com.xokundevs.cardsvshumanity.javaConCod.Connection.OK;
import static com.xokundevs.cardsvshumanity.javaConCod.Connection.PORT;
import static com.xokundevs.cardsvshumanity.javaConCod.Connection.SAVE_BARAJA;
import static com.xokundevs.cardsvshumanity.javaConCod.Connection.SOCKET_DISCONNECTED;
import static com.xokundevs.cardsvshumanity.javaConCod.Connection.UNKOWN_ERROR;
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
                    emitter.onError(new ServiceError(UNKOWN_ERROR));
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
                    emitter.onError(new ServiceError(UNKOWN_ERROR));
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
                    emitter.onError(new ServiceError(UNKOWN_ERROR));
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
                    emitter.onError(new ServiceError(UNKOWN_ERROR));
                }
                Log.d(Connection.class.getSimpleName(), "ACABA -- LogIn");
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
                    emitter.onError(new ServiceError(UNKOWN_ERROR));
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
                    emitter.onError(new ServiceError(UNKOWN_ERROR));
                }
            }
        });
    }

    private static void ConnectSocket(Socket sk) throws IOException {
        sk.connect(new InetSocketAddress(HOST, PORT), 10000);
    }
}
