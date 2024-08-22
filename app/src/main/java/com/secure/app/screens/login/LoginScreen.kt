package com.secure.app.screens.login

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.secure.app.R
import com.secure.app.retrofit.UiState


@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = hiltViewModel(),
    onLogin: () -> Unit
) {
    val state = loginViewModel.state

    LaunchedEffect(state) {
        if (state is UiState.Success) {
            onLogin()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Image(painterResource(id = R.drawable.ic_launcher_foreground), contentDescription = "Logo")
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                stringResource(id = R.string.app_name),
                modifier = Modifier.padding(start = 4.dp),
                fontWeight = FontWeight.Black,
                letterSpacing = 3.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = TextStyle(
                    shadow = Shadow(
                        color = MaterialTheme.colorScheme.scrim,
                        blurRadius = 4f,
                        offset = Offset(2f, 3f)
                    )
                ),
                fontFamily = FontFamily.Cursive,
                fontSize = 30.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            val googleSignInLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult()
            ) { result ->
                loginViewModel.loginWithGoogleResult(activityResult = result)
            }

            if (state is UiState.Error) {
                Text(
                    text = state.msg,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            SignInGoogleButton(
                isLoading = state is UiState.Loading,
                onClick = {
                    if (state !is UiState.Loading) {
                        loginViewModel.loginWithGoogleLauncher(launcher = googleSignInLauncher)
                    }
                }
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
@Composable
fun SignInGoogleButton(
    isLoading: Boolean = false,
    onClick: () -> Unit
) {
    Button(
        modifier = Modifier.padding(32.dp).fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        onClick = onClick
    ) {
        if (isLoading)
            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
        else {
            Image(
                painter = painterResource(
                    id = R.drawable.ic_google_logo
                ),
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Sign In with Google"
            )
        }

    }
}