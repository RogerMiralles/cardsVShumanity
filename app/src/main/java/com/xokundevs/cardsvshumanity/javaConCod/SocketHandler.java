package com.xokundevs.cardsvshumanity.javaConCod;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

public class SocketHandler {
    private Socket sk;
    private DataInputStream dis;
    private DataOutputStream dos;
    private SecretKey secretKey;

    public SocketHandler(Socket s) throws IOException {
        sk = s;
        dis = new DataInputStream(sk.getInputStream());
        dos = new DataOutputStream(sk.getOutputStream());
    }
    public void enviarInt(int num) throws IOException {
        dos.writeUTF(
                Codification.toHex(
                        Codification.encodeWithSimetricKey(
                                Codification.fromHex(Codification.parseIntToHex(num)),
                                secretKey,
                                true
                        )
                )
        );
    }

    public void enviarLong(long num) throws IOException {
        dos.writeUTF(
                Codification.toHex(
                        Codification.encodeWithSimetricKey(
                                Codification.fromHex(Codification.parseLongToHex(num)),
                                secretKey,
                                true)
                )
        );
    }

    public void enviarHex(String hexString) throws IOException {
        dos.writeUTF(
                Codification.toHex(
                        Codification.encodeWithSimetricKey(
                                Codification.fromHex(hexString),
                                secretKey,
                                true
                        )
                )
        );
    }

    public void enviarString(String frase) throws IOException {
        dos.writeUTF(
                Codification.toHex(
                        Codification.encodeWithSimetricKey(
                                frase.getBytes(StandardCharsets.UTF_8),
                                secretKey,
                                true
                        )
                )
        );
    }

    public int recibirInt() throws IOException {
        return Codification.parseHexToInt(
                Codification.toHex(
                        Codification.encodeWithSimetricKey(
                                Codification.fromHex(dis.readUTF()),
                                secretKey,
                                false
                        )
                )
        );
    }

    public String recibirHex() throws IOException {
        return Codification.toHex(
                Codification.encodeWithSimetricKey(
                        Codification.fromHex(dis.readUTF()),
                        secretKey,
                        false
                )
        );
    }

    public String recibirString() throws IOException {
        return new String(
                Codification.encodeWithSimetricKey(
                        Codification.fromHex(dis.readUTF()),
                        secretKey,
                        false
                ),
                StandardCharsets.UTF_8
        );
    }

    public long recibirLong() throws IOException {
        return Codification.parseHexToLong(
                Codification.toHex(
                        Codification.encodeWithSimetricKey(
                                Codification.fromHex(dis.readUTF()),
                                secretKey,
                                false
                        )
                )
        );
    }

    public SecretKey recibePublicKeyEnviaSecretKey() throws IOException {
        //Lee clave publica
        String pkHex = dis.readUTF();

        //Genera simmetric key y codifica
        SecretKey secretKey = Codification.generateNewSimetricKey();
        String secretKeyCoded = Codification.encodeWithPublicKey(pkHex, secretKey.getEncoded());

        //Envia simmetric key
        dos.writeUTF(secretKeyCoded);
        this.secretKey = secretKey;
        return secretKey;
    }

    public DataInputStream getDis() {
        return dis;
    }

    public DataOutputStream getDos() {
        return dos;
    }

    public void close(){
        try{
            dis.close();
        }catch (IOException e){}

        try{
            dos.close();
        }catch (IOException e){}


        try{
            sk.close();
        }catch (IOException e){}
    }
}
