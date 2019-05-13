package com.example.cardsvshumanity;

import android.util.Log;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class Connection {

    public static void crearCon(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Socket sk = new Socket("192.168.137.1",55555);
                    DataInputStream dis;
                    DataOutputStream dos;

                    dis = new DataInputStream(sk.getInputStream());
                    dos = new DataOutputStream(sk.getOutputStream());

                    dos.writeUTF("Fuck Renfe");
                    sk.close();

                }catch (Exception e){
                    Log.d("Connection", "ha fallado");
                    e.printStackTrace();
                }

            }
        }).start();



    }


}
