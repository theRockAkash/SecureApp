package com.secure.app.screens.home

import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.secure.app.retrofit.UiState
import com.secure.app.R
import com.secure.app.screens.login.LoginScreen
import com.secure.app.ui.theme.StarWarsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: HomeViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            StarWarsTheme {
                Scaffold(modifier = Modifier.fillMaxSize(),
                    topBar = { TopAppBar(title = { Text("Secure App") }) }) { innerPadding ->
                    var state by remember {
                        mutableStateOf(Firebase.auth.currentUser!=null)
                    }
                    LaunchedEffect(key1 = Unit) {
                        viewModel.setAuthListener {
                            state=it
                        }
                    }
                    if(state )
                    HomeScreen(paddingValues = innerPadding, viewModel = viewModel)
                    else LoginScreen {
                        state =true
                    }
                }
            }
        }
    }

    override fun onStart() {
        val isEnabled: Boolean = Settings.Secure.getInt(
            this.contentResolver,
            Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
            0
        ) == 1
        viewModel.setDeveloperModeEnabled(isEnabled)
        viewModel.checkLuckyPatcher()
        viewModel.checkForRoot()
        super.onStart()
    }
}

@Composable
fun HomeScreen(paddingValues: PaddingValues, viewModel: HomeViewModel) {

    val isVpnActive by viewModel.isVpnActiveFlow.collectAsState(initial = false)
    val isDevModeEnabled by viewModel.isDevModeEnabled.collectAsState(initial = false)
    val isPirateInstalled by viewModel.isPirateInstalled.collectAsState(initial = false)
    val isDeviceRooted by viewModel.isDeviceRooted.collectAsState(initial = false)
    if (isVpnActive || isDevModeEnabled || isPirateInstalled || isDeviceRooted) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (isVpnActive)
                    "VPN Detected"
                else if (isDevModeEnabled)
                    "Disable Developer Mode"
                else if (isPirateInstalled)
                    "Pirate Apps Found"
                else "Rooted Device Detected",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                if (isVpnActive)
                    "To ensure your data is secure, please disconnect your VPN before proceeding."
                else if (isDevModeEnabled)
                    "To ensure your data is secure, please turn off developer mode."
                else if (isPirateInstalled)
                    "To ensure your data is secure, please uninstall pirate apps e.g. Lucky Patcher"
                else "You cannot proceed with a rooted device.",
                Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

        }

    } else {
        SecureHomeScreen(paddingValues, viewModel)
    }
}

@Composable
fun SecureHomeScreen(paddingValues: PaddingValues, viewModel: HomeViewModel) {

    val resState by viewModel.serverResponseState.collectAsState()
    Column(
        Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Security Check Complete",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Image(
            painter = painterResource(id = R.drawable.baseline_check_circle_24),
            contentDescription = "Check",
            modifier = Modifier
                .padding(top = 16.dp, bottom = 48.dp)
        )

        if (resState !is UiState.Loading) {
            Button(onClick = { viewModel.sendDataToServer() }) {
                Text(text = "Send Data to Server")
            }
        }
        when (resState) {
            is UiState.Error -> {
                Text(
                    resState.msg,
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }

            is UiState.Loading -> {
                CircularProgressIndicator(
                    Modifier.size(48.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            is UiState.None -> {
            }

            is UiState.Success -> {
                Text(
                    "Response: \nMessage: " +
                            (resState.data?.key ?: "") +
                            "\nIP Address: ${(resState.data?.ip ?: "")}",
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

    }

}

