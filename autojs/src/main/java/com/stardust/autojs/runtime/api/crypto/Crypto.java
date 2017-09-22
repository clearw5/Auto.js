package com.stardust.autojs.runtime.api.crypto;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import static javax.crypto.Cipher.ENCRYPT_MODE;

/**
 * Created by Stardust on 2017/9/20.
 */

public class Crypto {

    public Cipher createCipher(String algorithm, String password) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
        javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance(algorithm);
        KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
        keyGenerator.init(128, new SecureRandom(password.getBytes()));
        SecretKey secretKey = keyGenerator.generateKey();
        cipher.init(ENCRYPT_MODE, secretKey);
        return cipher;
    }
}
