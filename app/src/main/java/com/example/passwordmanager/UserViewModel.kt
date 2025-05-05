package com.example.passwordmanager

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import java.io.File
import java.security.MessageDigest

class UserViewModel : ViewModel() {
    // The current error message, if any
    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    // To hold user authentication status
    private val _isAuthenticated = mutableStateOf(false)
    val isAuthenticated: State<Boolean> = _isAuthenticated

    // Function to clear the error
    fun clearError() {
        _error.value = null
    }

    fun authenticate(username: String, password: String, context: Context) {
        Log.d("LoginAttempt", "Attempting to login with credentials: $username:$password")
        val hashedInputPassword = hashPassword(password)
        val userFile = File(context.filesDir, "userdata/user_profiles.txt")

        if (!userFile.exists()) {
            Log.d("LoginAttempt", "User Login file not found")
            _error.value = "User data not found"
            _isAuthenticated.value = false
            return
        }

        val match = userFile.readLines().any { line ->
            val parts = line.split(":")
            parts.size == 2 && parts[0] == username && parts[1] == hashedInputPassword
        }

        if (match) {
            Log.d("LoginAttempt", "Credentials Accepted: $username:$password")
            _isAuthenticated.value = true
            _error.value = null
        } else {
            Log.d("LoginAttempt", "Incorrect credentials entered: $username:$password")
            _isAuthenticated.value = false
            _error.value = "Invalid username or password"
        }
    }

    fun createUserProfile(username: String, password: String, context: Context): Boolean {
        Log.d("UserViewModel", "Creating user with credentials: $username, $password")

        if (!password.isValidPassword()) {
            Log.d("UserViewModel", "Password does not meet requirements")
            _error.value = "Password must contain at least one number, one capital letter, and one special character"
            return false
        }

        try {
            val hashedPassword = hashPassword(password)

            val dir = File(context.filesDir, "userdata")
            if (!dir.exists()) dir.mkdirs()

            val userFile = File(dir, "user_profiles.txt")

            // Check for duplicate username
            if (userFile.exists()) {
                val exists = userFile.readLines().any { line ->
                    line.split(":").firstOrNull() == username
                }
                if (exists) {
                    _error.value = "Username already exists"
                    return false
                }
            }

            // Save the new user
            userFile.appendText("$username:$hashedPassword\n")
            Log.d("UserViewModel", "User profile created with credentials: $username:$password")
            Log.d("UserViewModel", "File saved at: ${userFile.absolutePath}")
            return true
        } catch (e: Exception) {
            Log.e("UserViewModel", "Error creating user: ${e.message}")
            _error.value = "Failed to create user profile"
            return false
        }
    }

    // Function to hash the password using SHA-256
    private fun hashPassword(password: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val hashBytes = messageDigest.digest(password.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}

fun String.isValidPassword(): Boolean {
    val passwordRegex = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\$%^&*(),.?\":{}|<>]).{8,}\$".toRegex()
    return this.matches(passwordRegex)
}