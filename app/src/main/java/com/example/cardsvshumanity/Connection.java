package com.example.cardsvshumanity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.Task;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

public class Connection {

    private static final int OK = 1;
    private static final int NO = -1;

    private static final int CREATE_USER = 101;

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

    public void RegistrarUsuario(final Runnable runnable, final String email, final String password, final String name){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Socket sk = new Socket("192.168.137.1",55555);
                    DataInputStream dis;
                    DataOutputStream dos;

                    dis = new DataInputStream(sk.getInputStream());
                    dos = new DataOutputStream(sk.getOutputStream());

                    dos.writeInt(CREATE_USER);

                    String pkHex = dis.readUTF();

                    SecretKey secretKey = Codification.generateNewSimetricKey();
                    String secretKeyCoded = Codification.encodeWithPublicKey(pkHex, secretKey.getEncoded());

                    dos.writeUTF(secretKeyCoded);

                    dos.writeUTF(Codification.encodeWithSimetricKey(email.getBytes(StandardCharsets.UTF_8), secretKey));
                    dos.writeUTF(Codification.encodeWithSimetricKey(
                            Codification.generateHashCode(password.getBytes(StandardCharsets.UTF_8))
                            , secretKey
                            )
                    );


                    //dos.writeUTF(Codification.encodeWithSimetricKey(Codification.parseLongToHex()));
                    Log.d(Connection.class.getSimpleName(), Codification.toHex(Codification.generateHashCode(password.getBytes(StandardCharsets.UTF_8))));
                    dos.writeUTF(Codification.encodeWithSimetricKey(name.getBytes(StandardCharsets.UTF_8), secretKey));

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
