package com.tony.autojs.tool.encrypt;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESEncrypt {
    public static SecretKey loadKeyByBytes(byte[] keyBytes) {
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, "AES");
    }

    public static byte[] generateAesKey() throws NoSuchAlgorithmException {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        // The AES key size in number of bits
        generator.init(128);
        SecretKey secKey = generator.generateKey();
        return secKey.getEncoded();
    }

    public static byte[] encrypt(byte[] content, SecretKey key) {
        Cipher aesCipher = null;
        try {
            aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.ENCRYPT_MODE, key);
            return aesCipher.doFinal(content);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("非可用算法");
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            System.err.println("明文长度不正确");
        } catch (InvalidKeyException e) {
            System.err.println("秘钥已损坏");
        }
        return null;
    }

    public static byte[] decrypt(byte[] content, SecretKey key) {
        Cipher aesCipher = null;
        try {
            aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.DECRYPT_MODE, key);
            return aesCipher.doFinal(content);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("非可用算法");
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            System.err.println("密文长度不正确");
        } catch (InvalidKeyException e) {
            System.err.println("秘钥已损坏");
        }
        return null;
    }
}
