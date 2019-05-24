package com.example.cardsvshumanity.javaConCod;

import android.content.Context;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class Codification {

    static {
        ASSIMETRIC_FORMAT_CIPHER = "RSA/ECB/PKCS1PADDING";
        ASSIMETRIC_ALGORITHM = "RSA";
        SIMETRIC_ALGORITHM = "AES";
        SIMETRIC_FORMAT_CIPHER = "AES/ECB/PKCS5Padding";
        HASH_ALGORITHM = "SHA-1";
    }

    //CONSTANTES DE ALGORITMOS DE ENCRIPTACION
    public static final String ASSIMETRIC_FORMAT_CIPHER;
    public static final String ASSIMETRIC_ALGORITHM;
    public static final String SIMETRIC_FORMAT_CIPHER;
    public static final String SIMETRIC_ALGORITHM;
    public static final String HASH_ALGORITHM;

    /**
     * Recrea una public key de una string hexadecimal
     * @param hexPublicKey string hexadecimal que representa una public key
     * @return public key derivada de la cadena hexadecimal
     */
    public static PublicKey GeneratePublicKey(String hexPublicKey){
        PublicKey publicKey = null;
        try {
            publicKey = KeyFactory.getInstance(ASSIMETRIC_ALGORITHM).generatePublic(new X509EncodedKeySpec(fromHex(hexPublicKey)));
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return publicKey;
    }

    /**
     * Codifica un array de bytes usando como clave una PublicKeya una cadena hexadecimal
     * @param hexPublicKey clave usada para encriptar
     * @param bytesToEncode datos por encriptar
     * @return cadena hexadecimal que representa la array de bytes encriptada
     */
    public static String encodeWithPublicKey(String hexPublicKey, byte[] bytesToEncode) {
        byte[] encodedData = null;
        try {
            PublicKey publicKey = GeneratePublicKey(hexPublicKey);

            Cipher decipher = Cipher.getInstance(ASSIMETRIC_FORMAT_CIPHER);
            decipher.init(Cipher.ENCRYPT_MODE, publicKey);
            encodedData = decipher.doFinal(bytesToEncode);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        if(encodedData == null)
            return null;
        return toHex(encodedData);
    }

    /**
     * Codifica o descodifica un array de bytes dada una clave de de algoritmo Simetrico
     * @param bytes datos a encriptar/desencriptar
     * @param key clave de algoritmo asimetrico
     * @param encode true para codificar, false para descodificar
     * @return cadena hexadecimal que representa el array de bytes codificados/descodificados
     */
    public static byte[] encodeWithSimetricKey(byte[] bytes, SecretKey key, boolean encode){
        byte[] str = null;
        int mode = (encode)? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE;
        try {
            Cipher encrypter = Cipher.getInstance(SIMETRIC_ALGORITHM);
            encrypter.init(mode, key);
            str = encrypter.doFinal(bytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException |
                BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     *  This method decodifies/codifies a file with a symmetric key
     * @param context that is running
     * @param rawFile that has the raw/codified data
     * @param path Path that leads to the file to save the final data
     * @param symmetricKey Secret key used to decode/encode
     * @param encode true to encode, false to decode
     * @return The codified file
     */
    public static File encodeFileWithSymmetricKey(Context context, File rawFile, String path, SecretKey symmetricKey, boolean encode){

        int mode = (encode)? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE;

        File output = null;
        DataInputStream disfile = null;
        DataOutputStream dosfile = null;
        try {
            output = new File(context.getExternalFilesDir(null),path);
            if(output.exists()){
                output.delete();
            }

            output.createNewFile();

            dosfile = new DataOutputStream(new FileOutputStream(output));
            disfile = new DataInputStream(new FileInputStream(rawFile));

            long length = rawFile.length();
            long actual = 0;

            Cipher c = Cipher.getInstance(SIMETRIC_FORMAT_CIPHER);
            c.init(mode, symmetricKey);

            byte[] array = new byte[Connection.BLOCK_SIZE];
            while (length > actual){
                int leido;
                if(length - actual > Connection.BLOCK_SIZE){
                    leido = disfile.read(array, 0, Connection.BLOCK_SIZE);
                }
                else{
                    leido = disfile.read(array, 0, (int) (length-actual));
                }
                actual+=leido;
                dosfile.write(c.update(array, 0, leido));
            }
            dosfile.write(c.doFinal());
        } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException
                | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            Log.e(Codification.class.getSimpleName(), e.getMessage());
            e.printStackTrace();
        }
        finally {
            try{
                disfile.close();
            }catch(NullPointerException | IOException e){
            }
            try{
                dosfile.close();
            }catch(NullPointerException | IOException e){
            }
        }

        return output;
    }

    /**
     * Genera una clave de algoritmo simetrico
     * @return clave de algoritmo simetrico
     */
    public static SecretKey generateNewSimetricKey(){
        SecretKey secretKey = null;
        try{
            secretKey = KeyGenerator.getInstance(SIMETRIC_ALGORITHM).generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return secretKey;
    }

    /**
     * Genera un hash del array de bytes dado
     * @param toHash array de bytes para hacer hash
     * @return hash en forma de array de bytes
     */
    public static byte[] generateHashCode(byte[] toHash){
        byte[] str = null;
        try{
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            md.update(toHash);
            str = md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * Genera un array de bytes dada una cadena hexadecimal
     * @param hex cadena hexadecimal a convertir en array
     * @return array de bytes que representaba la cadena hexadecimal
     */
    public static byte[] fromHex(String hex){
        int length = hex.length()/2;
        byte[] array = new byte[length];
        for(int i = 0; i<length; i++){
            String firstPart = hex.charAt(i*2)+"", secondPart = hex.charAt(i*2+1)+"";
            byte by = 0;
            try{
                by = (byte) (Integer.parseInt(firstPart)*16);
            }catch(NumberFormatException e){
                switch(firstPart){
                    case "a":
                        by = (byte) (10*16);
                        break;
                    case "b":
                        by = (byte) (11*16);
                        break;
                    case "c":
                        by = (byte) (12*16);
                        break;
                    case "d":
                        by = (byte) (13*16);
                        break;
                    case "e":
                        by = (byte) (14*16);
                        break;
                    case "f":
                        by = (byte) (15*16);
                        break;
                }
            }
            try{
                by += (byte)(Integer.parseInt(secondPart));
            }catch(NumberFormatException e){
                switch(secondPart){
                    case "a":
                        by += 10;
                        break;
                    case "b":
                        by += 11;
                        break;
                    case "c":
                        by += 12;
                        break;
                    case "d":
                        by += 13;
                        break;
                    case "e":
                        by += 14;
                        break;
                    case "f":
                        by += 15;
                        break;
                }
            }
            array[i] = by;
        }
        return array;
    }

    /**
     * Genera una cadena hexadecimal de un array de bytes dado
     * @param array array de bytes a codificar en hexadecimal
     * @return cadena hexadecimal que representa al array de bytes
     */
    public static String toHex(byte[] array){
        StringBuilder str = new StringBuilder();

        for(int i = 0; i<array.length; i++){
            int a = array[i] & 0xf;
            int b = (array[i] & 0xf0) >> 4;
            if(b < 10){
                str.append(b);
            }else{
                str.append(Integer.toHexString(b));
            }
            if(a < 10){
                str.append(a);
            }else{
                str.append(Integer.toHexString(a));
            }
        }

        return str.toString();
    }

    /**
     * Genera una cadena hexadecimal que representa una variable Long
     * @param number variable Long a representar
     * @return cadena hexadecimal que representa el long
     */
    public static String parseLongToHex(long number){
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(number);
        return toHex(buffer.array());
    }

    /**
     * Genera una variable long dada una cadena hexadecimal
     * @param hex Cadena hexadecimal a descifrar
     * @return Long que la cadena hexadecimal representaba
     */
    public static long parseHexToLong(String hex) {
        byte[] array = fromHex(hex);
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(array);
        buffer.flip();
        return buffer.getLong();
    }


    /**
     * Genera una cadena hexadecimal que representa una variable int
     * @param number variable Int a representar
     * @return cadena hexadecimal que representa el int
     */
    public static String parseIntToHex(int number){
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt(number);
        return toHex(buffer.array());
    }

    /**
     * Genera una variable int dada una cadena hexadecimal
     * @param hex Cadena hexadecimal a descifrar
     * @return Int que la cadena hexadecimal representaba
     */
    public static int parseHexToInt(String hex){
        byte[] array = fromHex(hex);
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.put(array);
        buffer.flip();
        return buffer.getInt();
    }
}
