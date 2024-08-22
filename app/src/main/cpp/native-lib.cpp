#include <jni.h>
#include <string>


extern "C"
JNIEXPORT jstring JNICALL
Java_com_secure_app_util_KeyUtils_aesKey(JNIEnv *env, jobject thiz) {
    std::string aes_key = "sxPHTix4CLQms3U9Lv8y2ihqhJmYwLzuzrFacLQ1vSNkulEuHI+G+6lVjx0FXEcWZF6EfxWV0/JKn6Gk4Rx6ZQ==";

    return env->NewStringUTF(aes_key.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_secure_app_util_KeyUtils_baseUrl(JNIEnv *env, jobject thiz) {
    std::string base_url = "https://api.khata360.com/api/";

    return env->NewStringUTF(base_url.c_str());
}