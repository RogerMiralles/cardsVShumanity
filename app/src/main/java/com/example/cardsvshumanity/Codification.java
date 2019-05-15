package com.example.cardsvshumanity;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Codification {

    static {
        ASSIMETRIC_FORMAT_CIPHER = "RSA/ECB/PKCS1PADDING";
        ASSIMETRIC_ALGORITHM = "RSA";
        SIMETRIC_ALGORITHM = "AES";
        SIMETRIC_FORMAT_CIPHER = "AES/ECB/PKCS5Padding";
        HASH_ALGORITHM = "SHA-1";
    }

    public static final String ASSIMETRIC_FORMAT_CIPHER;
    public static final String ASSIMETRIC_ALGORITHM;
    public static final String SIMETRIC_FORMAT_CIPHER;
    public static final String SIMETRIC_ALGORITHM;
    public static final String HASH_ALGORITHM;

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

    public static String encodeWithSimetricKey(byte[] bytes, SecretKey key){
        String str = null;
        try {
            Cipher encrypter = Cipher.getInstance(SIMETRIC_ALGORITHM);
            encrypter.init(Cipher.ENCRYPT_MODE, key);
            str = toHex(encrypter.doFinal(bytes));
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException |
                BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static SecretKey generateNewSimetricKey(){
        SecretKey secretKey = null;
        try{
            secretKey = KeyGenerator.getInstance(SIMETRIC_ALGORITHM).generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return secretKey;
    }

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
}
