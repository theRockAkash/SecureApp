# Secure Secret Keys in Android Applications

This repository demonstrates the most secure method to embed hardcoded keys such as base URLs, API keys, and encryption keys directly within your Android APK. By leveraging the power of C++ and JNI (Java Native Interface), sensitive information is stored securely in native code, making it much harder for attackers to extract keys from the APK.

## Key Features

- **Secure Storage:** Sensitive keys are stored in a C++ file, reducing the risk of exposure through reverse engineering.
- **JNI Integration:** A JNI interface is used to securely access these keys from Kotlin code, ensuring that keys remain protected within the native layer.
- **Practical Examples:** The repository includes practical examples of securely storing and retrieving keys in Android projects.

## Steps

1. **Store Keys in C++:** (app/src/main/cpp/native-lib.cpp)

   - Place your sensitive keys in a C++ file to minimize exposure.

   ```cpp
    // native-lib.cpp
    #include <jni.h>
    #include <string>

    // Function to return the secret key
    extern "C"
    JNIEXPORT jstring JNICALL
   //here Java is prefix with full package name with
   //class/object name (com.secure.app.util + KeyUtils) of Kotlin Object KeyUtils
   //that will call these fucntion 
    Java_com_secure_app_util_KeyUtils_secretKey(JNIEnv *env, jobject thiz) {
    std::string secret_key = "This is a highly secure data";
    return env->NewStringUTF(secret_key.c_str());
    }
   
   ```
   
3.  **Kotlin Object to access Keys from C++ file:**
   
    ```kotlin
    package com.secure.app.util

    /**
     * @Created by akash on 09-08-2024.
     * Know more about author at https://akash.cloudemy.in
     */
    object KeyUtils {
      init {
          System.loadLibrary("securefile") // project name mentioned in CMakeLists.txt file
      }
      // function name should be same as declared in c++ file (Java_com_secure_app_util_KeyUtils_secretKey)
      external fun secretKey() : String
    }
    ```

4. Add CMakeFile.txt and copy paste code from this repo's file
5. Finally, Configure CMakeFile in build.gradle (app-level)

   ```.gradle
   android{
   ...
   ..
      externalNativeBuild {
         cmake {
             path = file("src/main/cpp/CMakeLists.txt") // path to CMakeLists file
             version = "3.22.1" // your downloaded CMake version
         }
      }
   }
   ```
6. Additional Step: Install NDK Tools & CMake using SDK Manager
 
   In Android Studio, Go to
   Settings-> Language & Frameworks -> Android SDk -> SDK Tools -> select NDK (Side by Side) & CMake -> Click Apply
   
  
   
