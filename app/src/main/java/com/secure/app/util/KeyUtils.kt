package com.secure.app.util

/**
 * @Created by akash on 09-08-2024.
 * Know more about author at https://akash.cloudemy.in
 */
object KeyUtils {
    init {
        System.loadLibrary("securefile") // project name mentioned in native-lib.cpp file
    }

    external fun secretKey() : String

    external fun baseUrl() : String

    external fun aesKey() : String
}