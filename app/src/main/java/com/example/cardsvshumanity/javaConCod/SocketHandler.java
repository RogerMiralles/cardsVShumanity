package com.example.cardsvshumanity.javaConCod;

import org.xml.sax.Parser;

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

    public SocketHandler(Socket s) throws IOException {
        sk = s;
        dis = new DataInputStream(sk.getInputStream());
        dos = new DataOutputStream(sk.getOutputStream());
    }
    public void enviarInt(int num, SecretKey secretKey) throws IOException {
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

    public void enviarLong(long num, SecretKey secretKey) throws IOException {
        dos.writeUTF(
                Codification.toHex(
                        Codification.encodeWithSimetricKey(
                                Codification.fromHex(Codification.parseLongToHex(num)),
                                secretKey,
                                true)
                )
        );
    }

    public void enviarHex(String hexString, SecretKey secretKey) throws IOException {
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

    public void enviarString(String frase, SecretKey secretKey) throws IOException {
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

    public int recibirInt(SecretKey secretKey) throws IOException {
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

    public String recibirHex(SecretKey secretKey) throws IOException {
        return Codification.toHex(
                Codification.encodeWithSimetricKey(
                        Codification.fromHex(dis.readUTF()),
                        secretKey,
                        false
                )
        );
    }

    public String recibirString(SecretKey secretKey) throws IOException {
        return new String(
                Codification.encodeWithSimetricKey(
                        Codification.fromHex(dis.readUTF()),
                        secretKey,
                        false
                ),
                StandardCharsets.UTF_8
        );
    }

    public long recibirLong(SecretKey secretKey) throws IOException {
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
