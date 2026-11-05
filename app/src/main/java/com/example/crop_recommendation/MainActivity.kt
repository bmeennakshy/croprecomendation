package com.example.crop_recommendation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.example.crop_recommendation.data.FarmRepository
import com.example.crop_recommendation.ui.auth.AuthViewModel
import com.example.crop_recommendation.ui.auth.LoginScreen
import com.example.crop_recommendation.ui.auth.SignUpScreen
import com.example.crop_recommendation.ui.dashboard.MainDashboardScreen
import com.example.crop_recommendation.ui.theme.Crop_recommendationTheme

class MainActivity : ComponentActivity() {
    private lateinit var repository: FarmRepository
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = FarmRepository(applicationContext)
        authViewModel = AuthViewModel(repository)

        enableEdgeToEdge()
        setContent {
            Crop_recommendationTheme {
                val currentUser by repository.currentUser.collectAsState()
                var currentScreen by remember { mutableStateOf(if (currentUser != null) "dashboard" else "login") }

                LaunchedEffect(currentUser) {
                    if (currentUser != null && currentScreen == "login") {
                        currentScreen = "dashboard"
                    } else if (currentUser == null && currentScreen == "dashboard") {
                        currentScreen = "login"
                    }
                }

                when (currentScreen) {
                    "login" -> LoginScreen(
                        viewModel = authViewModel,
                        onNavigateToSignUp = {
                            authViewModel.resetState()
                            currentScreen = "signup"
                        },
                        onLoginSuccess = {
                            currentScreen = "dashboard"
                        }
                    )
                    "signup" -> SignUpScreen(
                        viewModel = authViewModel,
                        onNavigateBack = {
                            authViewModel.resetState()
                            currentScreen = "login"
                        },
                        onSignUpSuccess = {
                            currentScreen = "dashboard"
                        }
                    )
                    "dashboard" -> MainDashboardScreen(
                        repository = repository,
                        onLogout = {
                            authViewModel.logout()
                            currentScreen = "login"
                        }
                    )
                }
            }
        }
    }
}
