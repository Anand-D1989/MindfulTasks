package com.mindful.tasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.mindful.tasks.network.NetworkClient
import com.mindful.tasks.ui.screens.AuthScreen
import com.mindful.tasks.ui.screens.TaskScreen
import com.mindful.tasks.ui.theme.MindfulTheme
import com.mindful.tasks.viewmodel.TaskViewModel

class MainActivity : ComponentActivity() {
    
    // Instantiate ViewModel lazily using the activity-ktx extension
    private val taskViewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MindfulTheme {
                // Main surface container utilizing background color from theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    val currentUser by taskViewModel.currentUser.collectAsState()

                    if (currentUser == null) {
                        // Show Sign In / Register if no active user session
                        AuthScreen(
                            onAuthSuccess = { username ->
                                taskViewModel.loginSession(username)
                            },
                            onLoginClick = { username, password ->
                                NetworkClient.login(username, password)
                            },
                            onRegisterClick = { username, password ->
                                NetworkClient.signup(username, password)
                            }
                        )
                    } else {
                        // Load Dashboard screen once successfully logged in
                        TaskScreen(viewModel = taskViewModel)
                    }
                }
            }
        }
    }
}
