package com.example.passwordmanager

import android.content.Context
import android.credentials.Credential
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.security.SecureRandom

data class LoginCredential(val website: String, val email: String, val password: String)

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PassListScreen(userViewModel: UserViewModel, context: Context, onNavigateBack: () -> Unit) {
    val allCredentials = remember { mutableStateListOf<LoginCredential>() }
    var credentials by remember { mutableStateOf<List<LoginCredential>>(allCredentials) }
    var selectedCredential by remember { mutableStateOf<LoginCredential?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Your passes") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Login")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(credentials) { credential ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = credential.website, modifier = Modifier.weight(1f))
                        IconButton(onClick =
                        {
                            selectedCredential = credential
                            showPasswordDialog = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Show Password"
                            )
                        }
                    }
                    Divider()
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    credentials = if (it.isBlank()) {
                        allCredentials
                    } else {
                        allCredentials.filter { c ->
                            c.website.contains(it, ignoreCase = true)
                        }
                    }
                },
                label = { Text("Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 86.dp, bottom = 14.dp),
                textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
                singleLine = true,
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                }
            )
        }

        //pull up add credentials box
        if(showAddDialog) {
            AddCredentialDialog(
                onAdd = { website, email, password ->
                    allCredentials.add(LoginCredential(website, email, password))
                    showAddDialog = false
                },
                onDismiss =  { showAddDialog = false }
            )
        }

        //pull up existing credentials box
        if(showPasswordDialog && selectedCredential != null) {
            ShowCredentialsDialog(
                credential = selectedCredential!!,
                onDismiss = { showPasswordDialog = false }
            )
        }
    }
}

@Composable
fun AddCredentialDialog (
    onAdd: (String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var website by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Login") },
        text = {
            Spacer(Modifier.height(16.dp))
            Column {
                OutlinedTextField(
                    value = website,
                    onValueChange = { website = it },
                    label = { Text("Website") },
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                Button(onClick = { password = generatePassword() }) {
                    Text("Generate Password")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if(website.isNotBlank()&& email.isNotBlank() && password.isNotBlank()) {
                    onAdd(website,email,password)
                }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ShowCredentialsDialog(
    credential: LoginCredential,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Credentials for ${credential.website}", style = TextStyle(fontSize = 16.sp), fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Spacer(modifier = Modifier.height(12.dp))
                Text("Email: ${credential.email}", style = TextStyle(fontSize = 16.sp))
                Spacer(modifier = Modifier.height(12.dp))
                Text("Password: ${credential.password}", style = TextStyle(fontSize = 16.sp))
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

fun generatePassword(length: Int = 16): String {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+[]{};:<>?/"
    val random = SecureRandom()

    val password = (1..length)
        .map { chars[random.nextInt(chars.length)] }
        .joinToString("")

    return if (password[0].isLetter() && password.isValidPassword()) {
        password
    } else {
        generatePassword(length)
    }
}