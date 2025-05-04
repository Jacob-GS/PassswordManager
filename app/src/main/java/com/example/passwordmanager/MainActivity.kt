package com.example.passwordmanager

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.passwordmanager.ui.theme.PasswordManagerTheme
import com.google.rpc.context.AttributeContext.Auth


class MainActivity : FragmentActivity() {

    private val userViewModel = UserViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoginNavigation(userViewModel, this)
        }
    }
}

@Composable
fun LoginNavigation(userViewModel: UserViewModel, context: Context) {
    val navController = rememberNavController()
    val isAuthenticated by userViewModel.isAuthenticated

    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated) {
            navController.navigate("passScreen")
        }
    }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginClick = { username, password ->
                    userViewModel.authenticate(username, password, context)
                },
                onBiometricClick = {
                    biometricAuthScreen(context) { success ->
                        if (success) Log.d("Auth", "Biometric Success")
                    }
                },
                onCreateAccClick = {
                    navController.navigate("createProfile")
                },
                loginError = userViewModel.error.value,
                onClearError = {
                    userViewModel.clearError() // This method will clear the error in the ViewModel
                }
            )
        }

        composable("createProfile") {
            CreateProfileScreen(userViewModel, context) {
                navController.popBackStack()
            }
        }

        composable("passScreen") {
            PassListScreen(userViewModel, context) {
                navController.popBackStack()
            }
        }
    }
}
