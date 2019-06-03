package com.example.cardsvshumanity.javaConCod;

import android.app.Activity;
import android.util.Log;

import com.example.cardsvshumanity.actiPartida.Jugador;
import com.example.cardsvshumanity.cosasRecicler.CartaBlanca;
import com.example.cardsvshumanity.cosasRecicler.CartaNegra;

import java.io.IOException;
import java.util.ArrayList;

import javax.crypto.SecretKey;

public class GameController {
    private static GameController INSTANCE;

    public static final int EMPEZAR_PARTIDA = 1001;
    public static final int REPARTIR_CARTAS = 1002;
    public static final int ESCOGER_TZAR = 1003;
    public static final int MUESTRA_CARTA_NEGRA = 1004;
    public static final int ESCOGER_CARTAS = 1005;
    public static final int TZAR_ESCOGE_GANADOR = 1006;
    public static final int REPARTIR_CARTAS_FASE_2 = 1007;
    public static final int YA_HAY_GANADOR = 1008;
    public static final int TZAR_YA_HA_ESCOGIDO_GANADOR = 1009;
    public static final int CERRAR_PARTIDA = 1500;
    public static final int NEW_PLAYER = 1100;
    public static final int ERROR_NO_SUFFICIENT_PLAYERS = -1001;
    public static final int ERROR_FILLED_ROOM = -1002;
    public static final int ERROR_PLAYER_DISCONNECTED = -1003;

    private SocketHandler skHandler;
    private SecretKey secretKey;
    private GameController(SocketHandler skHandler, SecretKey secretKey){
        this.skHandler = skHandler;
        this.secretKey = secretKey;
    }
    public static void GenerateGameController(SocketHandler skHandler, SecretKey secretKey){
        INSTANCE = new GameController(skHandler, secretKey);
    }

    public static GameController getInstance() {
        return INSTANCE;
    }

    public void StartListeningGame(final Activity activity, final ArgumentableRunnable runnable){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int result;
                    do {
                        result = skHandler.recibirInt(secretKey);
                        runnable.order = result;
                        Log.d(GameController.class.getSimpleName(), "Estado: "+result);

                        synchronized (GameController.class){
                            if (result == REPARTIR_CARTAS) {
                                int cantidad = skHandler.recibirInt(secretKey);
                                ArrayList<CartaBlanca> cartasTexto = new ArrayList<>();
                                for (int i = 0; i < cantidad; i++) {
                                    cartasTexto.add(new CartaBlanca(skHandler.recibirString(secretKey)));
                                }
                                runnable.argument = cartasTexto;
                                activity.runOnUiThread(runnable);
                            } else if (result == ERROR_PLAYER_DISCONNECTED || result == ESCOGER_TZAR) {
                                runnable.argument = skHandler.recibirString(secretKey);
                                activity.runOnUiThread(runnable);
                            } else if (result == MUESTRA_CARTA_NEGRA) {
                                String texto = skHandler.recibirString(secretKey);
                                int num = skHandler.recibirInt(secretKey);
                                CartaNegra c = new CartaNegra(texto);
                                c.setNumEspacios(num);
                                runnable.argument = c;
                                activity.runOnUiThread(runnable);
                            } else if (result == ESCOGER_CARTAS) {
                                activity.runOnUiThread(runnable);
                            } else if (result == TZAR_ESCOGE_GANADOR) {
                                int jugadores = skHandler.recibirInt(secretKey);
                                ArrayList<ArrayList<CartaBlanca>> cartasJugadores = new ArrayList<>();
                                Log.d(GameController.class.getSimpleName(), "Cantidad jugadores: " + jugadores);
                                for (int i = 0; i < jugadores; i++) {
                                    int cantidadCartas = skHandler.recibirInt(secretKey);
                                    Log.d(GameController.class.getSimpleName(), "Cantidad cartas de jugador " + i +": "+ cantidadCartas);
                                    cartasJugadores.add(new ArrayList<CartaBlanca>());
                                    for (int j = 0; j < cantidadCartas; j++) {
                                        cartasJugadores.get(i).add(new CartaBlanca(skHandler.recibirString(secretKey)));
                                    }
                                }
                                runnable.argument = cartasJugadores;
                                activity.runOnUiThread(runnable);
                            } else if (result == TZAR_YA_HA_ESCOGIDO_GANADOR) {
                                String ganador = skHandler.recibirString(secretKey);
                                int puntos = skHandler.recibirInt(secretKey);
                                runnable.argument = new Object[]{ganador, puntos};
                                activity.runOnUiThread(runnable);
                            } else if (result == YA_HAY_GANADOR) {
                                runnable.argument = skHandler.recibirString(secretKey);
                                activity.runOnUiThread(runnable);
                            } else if (result == CERRAR_PARTIDA) {
                                activity.runOnUiThread(runnable);
                            }

                            GameController.class.wait();
                        }
                    } while (result != CERRAR_PARTIDA);

                }catch(IOException | InterruptedException e){
                    runnable.order = Connection.SOCKET_DISCONNECTED;
                    activity.runOnUiThread(runnable);
                }
            }
        }).start();
    }

    public void enviarCartasEscogidas(final int[] cartasEscogidas){
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            skHandler.enviarInt(cartasEscogidas.length, secretKey);
                            for(int i : cartasEscogidas){
                                skHandler.enviarInt(i, secretKey);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).start();
    }

    public void enviarComenzarPartida(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    skHandler.enviarInt(EMPEZAR_PARTIDA, secretKey);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void enviarGanador(final int winner){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    skHandler.enviarInt(winner, secretKey);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void waitUntilGameStarts(final Activity activity, final ArgumentableRunnable runnable){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int result;
                    do{
                        result = skHandler.recibirInt(secretKey);
                        if(result == NEW_PLAYER){
                            String email = skHandler.recibirString(secretKey);
                            String nombre = skHandler.recibirString(secretKey);
                            runnable.argument = new Jugador(email, nombre);
                            runnable.order = NEW_PLAYER;
                            activity.runOnUiThread(runnable);
                        }
                        else{
                            runnable.order = result;
                            activity.runOnUiThread(runnable);
                        }
                    }while(result != EMPEZAR_PARTIDA && result != CERRAR_PARTIDA);
                } catch (IOException e) {
                    e.printStackTrace();
                    runnable.order = Connection.SOCKET_DISCONNECTED;
                }
            }
        }).start();
    }

    public static abstract class ArgumentableRunnable implements Runnable{
        private Object argument;
        private int order;

        public int getOrder() {
            return order;
        }

        public Object getArgument() {
            return argument;
        }
    }

    public void close(){
        skHandler.close();
    }
}
