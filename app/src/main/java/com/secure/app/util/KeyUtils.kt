package com.secure.app.util

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.secure.app.BuildConfig
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * @Created by akash on 09-08-2024.
 * Know more about author at https://akash.cloudemy.in
 */
object KeyUtils {
    init {
        System.loadLibrary("securefile") // project name mentioned in native-lib.cpp file
    }


    external fun baseUrl() : String

    external fun aesKey() : String

    fun getSig(context: Context): String? {
        try {
            val info = context.packageManager.getPackageInfo(
                BuildConfig.APPLICATION_ID,
                PackageManager.GET_SIGNING_CERTIFICATES
            )
            for (signature in info.signingInfo.apkContentsSigners) {
                val md = MessageDigest.getInstance("SHA256")
                md.update(signature.toByteArray())
                val digest = md.digest()
                val toRet = StringBuilder()
                for (i in digest.indices) {
                    if (i != 0) toRet.append(":")
                    val b = digest[i].toInt() and 0xff
                    val hex = Integer.toHexString(b)
                    if (hex.length == 1) toRet.append("0")
                    toRet.append(hex)
                }
                val s = toRet.toString()
                return s

            }
        } catch (e1: PackageManager.NameNotFoundException) {
            Log.e("name not found", e1.toString())
        } catch (e: NoSuchAlgorithmException) {
            Log.e("no such an algorithm", e.toString())
        } catch (e: Exception) {
            Log.e("exception", e.toString())
        }
        return null
    }
}