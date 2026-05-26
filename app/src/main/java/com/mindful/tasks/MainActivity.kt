package com.mindful.tasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
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
                    TaskScreen(viewModel = taskViewModel)
                }
            }
        }
    }
}
