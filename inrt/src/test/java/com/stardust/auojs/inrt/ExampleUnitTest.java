package com.stardust.auojs.inrt;

import com.stardust.util.AdvancedEncryptionStandard;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        String key = "e7c2e459ba3069ce2c9f37ea680ddb07";
        String vec = "0e491e6efc69ddc3";
        byte[] encrypt = new AdvancedEncryptionStandard(key.getBytes(), vec).encrypt("xxx".getBytes());
        System.out.println(Arrays.toString(encrypt));
        byte[] decrypt = new AdvancedEncryptionStandard(key.getBytes(), vec).decrypt(encrypt, 0, encrypt.length);
        System.out.println(new String(decrypt));
    }
}