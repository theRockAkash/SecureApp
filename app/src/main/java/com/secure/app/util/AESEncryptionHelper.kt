package com.secure.app.util

import android.content.Context
import android.util.Log
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
    fun  encrypt(data: Any,context: Context,aesKey:String): String {
        val finalKey=Base64.getDecoder().decode(decrypt(aesKey,context,decrypt(KeyUtils.aesKey(),context)))
        val iv = ByteArray(16)
        SecureRandom().nextBytes(iv)
        val ivSpec = IvParameterSpec(iv)

        val key = SecretKeySpec(finalKey, ALGORITHM)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec)
        val encrypted = cipher.doFinal(gson.toJson(data).toByteArray())

        // Prepend IV to the encrypted message
        val ivAndEncrypted = iv + encrypted

        return Base64.getEncoder().encodeToString(ivAndEncrypted)

    }

    // Decrypt the ciphertext using the given key and IV
    private fun decrypt(ciphertext: String, context: Context,aesKey: String?=null): String {
        val decodedBytes = Base64.getDecoder().decode(ciphertext)
        val secretKey= if(aesKey!=null )
            Base64.getDecoder().decode(aesKey)
        else KeyUtils.getSig(context)?.let { toByteArray(it) }
        // Extract IV from the first 16 bytes
        val iv = decodedBytes.sliceArray(0 until 16)
        val encrypted = decodedBytes.sliceArray(16 until decodedBytes.size)
        val ivSpec = IvParameterSpec(iv)
        val key = SecretKeySpec(secretKey, ALGORITHM)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)

        val decrypted = cipher.doFinal(encrypted)
        return String(decrypted)
    }


    private fun toByteArray(hexString: String): ByteArray {
        val sanitizedString = hexString.replace(":", "")
        val len = sanitizedString.length
        val data = ByteArray(len / 2)
        for (i in 0 until len step 2) {
            data[i / 2] = ((Character.digit(sanitizedString[i], 16) shl 4) +
                    Character.digit(sanitizedString[i + 1], 16)).toByte()
        }
        return data
    }
}
