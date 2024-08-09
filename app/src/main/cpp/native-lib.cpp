#include <jni.h>
#include <string>

// Function to return the secret key
extern "C"
JNIEXPORT jstring JNICALL
Java_com_secure_app_util_KeyUtils_secretKey(JNIEnv *env, jobject thiz) {
    std::string secret_key = "ThisIsAPrivateKey 007";
    return env->NewStringUTF(secret_key.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_secure_app_util_KeyUtils_aesKey(JNIEnv *env, jobject thiz) {
    std::string aes_key = "at+EnFJmQIG5pHDRu8URdCrmKNaoCxx6qkx2+JuSZy0=";

    return env->NewStringUTF(aes_key.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_secure_app_util_KeyUtils_baseUrl(JNIEnv *env, jobject thiz) {
    std::string base_url = "https://api.khata360.com/api/";

    return env->NewStringUTF(base_url.c_str());
}