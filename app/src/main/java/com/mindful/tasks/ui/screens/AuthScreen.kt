package com.mindful.tasks.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindful.tasks.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    onAuthSuccess: (String) -> Unit,
    onLoginClick: suspend (String, String) -> Boolean,
    onRegisterClick: suspend (String, String) -> Pair<Boolean, String>
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRegisterMode by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Elegant Icon / Title Logo
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(EmeraldLight, EmeraldZen)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🧘",
                    fontSize = 40.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "MindfulTasks",
                style = Typography.displayLarge,
                color = EmeraldZen,
                fontWeight = FontWeight.ExtraBold
            )
            
            Text(
                text = if (isRegisterMode) "Create your focus profile" else "Welcome back, plan your day",
                style = Typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Auth Card Panel
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderGlass, RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = DeepCharcoal),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = if (isRegisterMode) "Register Account" else "Sign In",
                        style = Typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )

                    errorMessage?.let { error ->
                        Text(
                            text = error,
                            color = LightRose,
                            style = Typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Username Input
                    OutlinedTextField(
                        value = username,
                        onValueChange = { 
                            username = it
                            errorMessage = null 
                        },
                        label = { Text("Unique Username") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = EmeraldZen,
                            unfocusedBorderColor = BorderGlass,
                            focusedContainerColor = ObsidianBlack,
                            unfocusedContainerColor = ObsidianBlack
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Password Input
                    OutlinedTextField(
                        value = password,
                        onValueChange = { 
                            password = it
                            errorMessage = null 
                        },
                        label = { Text("Password") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = EmeraldZen,
                            unfocusedBorderColor = BorderGlass,
                            focusedContainerColor = ObsidianBlack,
                            unfocusedContainerColor = ObsidianBlack
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Action Button (Sign In / Register)
                    Button(
                        onClick = {
                            if (username.isBlank() || password.isBlank()) {
                                errorMessage = "Username and password cannot be empty."
                                return@Button
                            }
                            isLoading = true
                            errorMessage = null
                            
                            // Start Coroutine thread for network query
                            scope.launch {
                                if (isRegisterMode) {
                                    val (success, message) = onRegisterClick(username, password)
                                    isLoading = false
                                    if (success) {
                                        isRegisterMode = false
                                        errorMessage = "Registration successful! Please Sign In."
                                    } else {
                                        errorMessage = message
                                    }
                                } else {
                                    val success = onLoginClick(username, password)
                                    isLoading = false
                                    if (success) {
                                        onAuthSuccess(username)
                                    } else {
                                        errorMessage = "Invalid credentials or Server offline."
                                    }
                                }
                            }
                        },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldZen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = ObsidianBlack, modifier = Modifier.size(24.dp))
                        } else {
                            Text(
                                text = if (isRegisterMode) "Register" else "Sign In",
                                color = ObsidianBlack,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Switch Mode Text Button
            TextButton(
                onClick = {
                    isRegisterMode = !isRegisterMode
                    errorMessage = null
                    username = ""
                    password = ""
                }
            ) {
                Text(
                    text = if (isRegisterMode) "Already have an account? Sign In" else "Create a new account? Register",
                    color = EmeraldZen,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
