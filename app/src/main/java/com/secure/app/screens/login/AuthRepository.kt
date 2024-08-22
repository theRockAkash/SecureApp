package com.secure.app.screens.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.secure.app.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * @Created by akash on 24-06-2024.
 * Know more about author at https://akash.cloudemy.in
 */
class AuthRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val signInClient = Identity.getSignInClient(context)

      fun getCurrentUser(): FirebaseUser? {
        return Firebase.auth.currentUser
    }



    // GOOGLE SIGN IN
    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

      fun signInWithGoogleResult(task: Task<GoogleSignInAccount>): Result<GoogleSignInAccount> {
        return try {
            val account = task.getResult(ApiException::class.java)
            Result.success(account)
        } catch (e: ApiException) {
            Result.failure(e)
        }
    }

      suspend fun signInWithGoogleCredential(credential: AuthCredential): Result<FirebaseUser> {
        return try {
            val firebaseUser =  Firebase.auth.signInWithCredential(credential).await()
            firebaseUser.user?.let {
                Result.success(it)
            } ?:Result.failure(Exception("Sign in with Google failed."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

      fun signInWithGoogle(googleSignInLauncher: ActivityResultLauncher<Intent>) {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    // SIGN OUT
      fun signOut() {
        Firebase.auth.signOut()
        signInClient.signOut()
    }

    fun uploadUserData(user: FirebaseUser ,onResult:(Result<String> )->Unit){
        val ref= FirebaseDatabase.getInstance().getReference("Users").child(user.uid)
        @SuppressLint("HardwareIds")
        val androidId =
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                 if(snapshot.exists()&&snapshot.hasChildren()){
                     onResult.invoke(Result.success("Successfully uploaded"))
                 }else{
                     val userData=HashMap<String,Any>()
                     userData["name"]=user.displayName?:"N/A"
                     userData["email"]=user.email?:"N/A"
                     userData["createdAt"]=ServerValue.TIMESTAMP
                     userData["androidId"]=androidId
                     ref.setValue(userData).addOnCompleteListener {
                         onResult.invoke(if(it.isSuccessful){
                             Result.success("Successfully uploaded")
                         }else{
                             signOut()
                             Result.failure(it.exception?:Throwable("Failed to Upload data"))
                         })
                     }
                 }
            }
            override fun onCancelled(error: DatabaseError) {
                signOut()
                onResult.invoke( Result.failure(error.toException()))
            }

        })


    }
}