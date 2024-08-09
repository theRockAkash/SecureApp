package com.secure.app.screens.home

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.secure.app.retrofit.UiState
import com.secure.app.retrofit.Api
import com.secure.app.retrofit.getErrorMessage
import com.secure.app.util.AESEncryptionHelper
import com.secure.app.util.KeyUtils
import com.secure.app.util.NetworkManager
import com.scottyab.rootbeer.RootBeer
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * @Created by akash on 12-07-2024.
 * Know more about author at https://akash.cloudemy.in
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val api: Api,
    @ApplicationContext private val context: Context,
    private val networkManager: NetworkManager
) : ViewModel() {
    var isVpnActiveFlow: Flow<Boolean> = MutableStateFlow(false)
        private set
    var isDevModeEnabled = MutableStateFlow(false)
        private set
    var isPirateInstalled = MutableStateFlow(false)
        private set
    var isDeviceRooted = MutableStateFlow(false)
        private set
    private val _serverResponseState = MutableStateFlow<UiState<Data>>(UiState.None())
    val serverResponseState = _serverResponseState.asStateFlow()

    init {
        checkNetworkConnection()

    }

    private fun checkNetworkConnection() {
        isVpnActiveFlow = callbackFlow {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            if (connectivityManager == null) {
                channel.close(IllegalStateException("connectivity manager is null"))
                return@callbackFlow
            } else {
                val callback = object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        channel.trySend(true)
                    }

                    override fun onLost(network: Network) {
                        channel.trySend(false)
                    }
                }
                connectivityManager.registerNetworkCallback(
                    NetworkRequest.Builder()
                        .addTransportType(NetworkCapabilities.TRANSPORT_VPN)
                        .removeCapability(NetworkCapabilities.NET_CAPABILITY_NOT_VPN)
                        .build(),
                    callback
                )
                awaitClose {
                    connectivityManager.unregisterNetworkCallback(callback)
                }
            }
        }
    }

    fun sendDataToServer() {
        viewModelScope.launch(Dispatchers.IO) {
            _serverResponseState.value = UiState.Loading()
            val ip = networkManager.getLocalIpAddress()
            val data = AESEncryptionHelper.encrypt(Data(ip = ip, key = KeyUtils.secretKey()))
            val map = HashMap<String,String>()
            map["key_Ip"]=data
            val res = api.sendSecureData(map)
            if (res.isSuccessful && res.body() != null) {
                _serverResponseState.value = UiState.Success(res.body()!!)
            } else {
                _serverResponseState.value = UiState.Error("Error : ${res.errorBody().getErrorMessage()}")
            }

        }
    }

    fun setDeveloperModeEnabled(enabled: Boolean) {
        isDevModeEnabled.value = !enabled
    }

    fun checkLuckyPatcher() {
        val appList = listOf(
            "com.dimonvideo.luckypatcher",
            "com.reqable.android",
            "com.chelpus.lackypatch",
            "com.android.vending.billing.InAppBillingService.LACK",
            "com.android.vending.billing.InAppBillingService.LOCK"
        )
        isPirateInstalled.value = false
        for (packageName in appList) {
            Log.w("TAG", "checkLuckyPatcher: Checking for $packageName", )
            if (packageExists(packageName)) {
                isPirateInstalled.value = true
                break
            }
        }

    }

    private fun packageExists(packageName: String): Boolean {
        try {
            val info = context.packageManager.getApplicationInfo(packageName, 0)
                ?: // No need really to test for null, if the package does not
                // exist it will really rise an exception. but in case Google
                // changes the API in the future lets be safe and test it
                return false
            Log.w("TAG", "packageExists: $info", )
            return true
        } catch (ex: Exception) {
            // If we get here only means the Package does not exist
        }

        return false
    }

    fun checkForRoot() {
        val rootBeer = RootBeer(context)
        if (rootBeer.isRooted) {
            //we found indication of root
            isDeviceRooted.value = true
        } else {
            //we didn't find indication of root
            isDeviceRooted.value = false
        }
    }

}