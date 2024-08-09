package com.secure.app.util

import com.google.gson.Gson
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


/**
 * @Created by akash on 07-08-2024.
 * Know more about author at https://akash.cloudemy.in
 */

object AESEncryptionHelper {

    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/CBC/PKCS7Padding"
    private val gson = Gson()

    // Encrypt the plaintext using the given key and IV
    fun  encrypt(data: Any): String {

        val secretKey=Base64.getDecoder().decode(KeyUtils.aesKey())
        val iv = ByteArray(16)
        SecureRandom().nextBytes(iv)
        val ivSpec = IvParameterSpec(iv)

        val key = SecretKeySpec(secretKey, ALGORITHM)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec)

        val encrypted = cipher.doFinal(gson.toJson(data).toByteArray())

        // Prepend IV to the encrypted message
        val ivAndEncrypted = iv + encrypted

        return Base64.getEncoder().encodeToString(ivAndEncrypted)

    }

    // Decrypt the ciphertext using the given key and IV
    fun decrypt(ciphertext: String): String {
        val decodedBytes = Base64.getDecoder().decode(ciphertext)

        // Extract IV from the first 16 bytes
        val iv = decodedBytes.sliceArray(0 until 16)
        val encrypted = decodedBytes.sliceArray(16 until decodedBytes.size)
        val ivSpec = IvParameterSpec(iv)
        val secretKey=Base64.getDecoder().decode(KeyUtils.aesKey())
        val key = SecretKeySpec(secretKey, ALGORITHM)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)

        val decrypted = cipher.doFinal(encrypted)
        return String(decrypted)
    }



}
