package com.example.passwordmanager

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.biometric.BiometricManager
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentActivity

fun biometricAuthScreen(
    context: Context,
    onAuthResult: (Boolean) -> Unit
) {
    val biometricManager = BiometricManager.from(context)
    val canAuthenticate = biometricManager.canAuthenticate(
        BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL
    )

    if (canAuthenticate != BiometricManager.BIOMETRIC_SUCCESS) {
        onAuthResult(false)
        return
    }

    val executor = ContextCompat.getMainExecutor(context)

    val callback = object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            onAuthResult(true)
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            onAuthResult(false)
        }

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            onAuthResult(false)
        }
    }

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Biometric Login")
        .setSubtitle("Use your fingerprint to log in")
        .setNegativeButtonText("Cancel")
        .build()

    val biometricPrompt = BiometricPrompt(context as FragmentActivity, executor, callback)
    biometricPrompt.authenticate(promptInfo)
}