package com.example.passwordmanager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(
    onLoginClick: (email: String, password: String) -> Unit,
    onBiometricClick: () -> Unit,
    onCreateAccClick: () -> Unit,
    loginError: String? = null,
    onClearError: () -> Unit // Add this parameter to handle clearing the error
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Clear error message when the user starts typing
    LaunchedEffect(username, password) {
        if (loginError != null && loginError.isNotEmpty()) {
            onClearError() // Call the callback to clear error
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("YouShallPass", fontSize = 28.sp, fontWeight = FontWeight.Bold)

        if (loginError != null) {
            if (loginError.isNotEmpty()) {
                Text(
                    text = loginError,
                    color = Color.Red,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column {
            Row {
                // Login with user credentials
                Button(
                    onClick = { onLoginClick(username, password) },
                    modifier = Modifier
                        .weight(.8f)
                        .padding(end = 8.dp)
                ) {
                    Text("Log in")
                }

                // Login with biometrics
                Button(
                    onClick = onBiometricClick,
                    modifier = Modifier
                        .weight(.8f)
                        .padding(end = 8.dp)
                ) {
                    Text("Use Biometrics")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Create Account Button
            Button(
                onClick = onCreateAccClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create Account")
            }
        }
    }
}

