package com.stardust.util

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class AdvancedEncryptionStandard(private val key: ByteArray, private val initVector: String) {

    /**
     * Encrypts the given plain text
     *
     * @param plainText The plain text to encrypt
     */
    @Throws(Exception::class)
    fun encrypt(plainText: ByteArray): ByteArray {
        val secretKey = SecretKeySpec(key, ALGORITHM)
        val cipher = Cipher.getInstance(FULL_ALGORITHM)
        val ivParameterSpec = IvParameterSpec(initVector.toByteArray())
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)
        return cipher.doFinal(plainText)
    }

    /**
     * Decrypts the given byte array
     *
     * @param cipherText The data to decrypt
     */
    @Throws(Exception::class)
    fun decrypt(cipherText: ByteArray, start: Int = 0, end: Int = cipherText.size): ByteArray {
        val secretKey = SecretKeySpec(key, ALGORITHM)
        val ivParameterSpec = IvParameterSpec(initVector.toByteArray())
        val cipher = Cipher.getInstance(FULL_ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec)
        return cipher.doFinal(cipherText, start, end - start)
    }

    companion object {

        private const val ALGORITHM = "AES"
        private const val FULL_ALGORITHM = "AES/CBC/PKCS5Padding"
    }
}