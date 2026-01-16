package com.example.tugasakhir.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Check
import androidx.compose.animation.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import com.example.tugasakhir.R
import com.example.tugasakhir.viewmodel.RegisterViewModel
import com.example.tugasakhir.view.component.KomponenError

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun HalamanRegister(
    viewModel: RegisterViewModel,
    onRegisterSuccess: () -> Unit,
    onLoginClick: () -> Unit,
    navigateUp: () -> Unit = {}
) {
    // State untuk visibilitas password
    var passwordVisible by remember { mutableStateOf(false) }

    // --- NAVIGASI OTOMATIS SAAT SUKSES ---
    LaunchedEffect(viewModel.success) {
        if (viewModel.success) {
            kotlinx.coroutines.delay(800) 
            onRegisterSuccess() 
        }
    }

    Scaffold(
        // snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        val primaryColor = colorResource(id = R.color.primary_blue)
        val textColorPrimary = colorResource(id = R.color.text_primary)
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                 // Top Padding
                Spacer(modifier = Modifier.height(60.dp))

                // 1. Illustration
                androidx.compose.foundation.Image(
                    painter = painterResource(id = R.drawable.library_illustration),
                    contentDescription = null,
                    modifier = Modifier
                        .size(180.dp)
                        .padding(bottom = 16.dp)
                )



                // 2. Title
                Text(
                    text = stringResource(R.string.login_title),
                    fontSize = 25.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = primaryColor,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.app_title),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = primaryColor,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(40.dp))

                // 3. Inputs
                Column(modifier = Modifier.fillMaxWidth()) {
                    
                    // Username
                    com.example.tugasakhir.view.component.CustomTextField(
                        value = viewModel.username,
                        onValueChange = {
                            viewModel.username = it
                            viewModel.usernameError = null
                        },
                        label = "Username",
                        leadingIcon = Icons.Default.AccountCircle,
                        isError = viewModel.usernameError != null,
                        errorMessage = viewModel.usernameError,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email
                    com.example.tugasakhir.view.component.CustomTextField(
                        value = viewModel.email,
                        onValueChange = {
                            viewModel.email = it
                            viewModel.emailError = null
                        },
                        label = "Email",
                        leadingIcon = Icons.Default.Email,
                        isError = viewModel.emailError != null,
                        errorMessage = viewModel.emailError,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password
                    com.example.tugasakhir.view.component.CustomTextField(
                        value = viewModel.password,
                        onValueChange = {
                            viewModel.password = it
                            viewModel.passwordError = null
                        },
                        label = "Password",
                        leadingIcon = Icons.Default.Lock,
                        isError = viewModel.passwordError != null,
                        errorMessage = viewModel.passwordError,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = null, tint = primaryColor)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                // --- TOMBOL REGISTER ---
                Button(
                    onClick = { viewModel.register() },
                    modifier = Modifier.fillMaxWidth().height(56.dp).shadow(elevation = 8.dp, shape = RoundedCornerShape(28.dp), spotColor = primaryColor),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    enabled = !viewModel.isLoading
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("DAFTAR SEKARANG", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Link ke Login
                TextButton(onClick = onLoginClick) {
                    Text("Sudah punya akun? Masuk di sini", color = primaryColor, fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(60.dp))
            }

            // --- Success Animation Overlay ---
            androidx.compose.animation.AnimatedVisibility(
                visible = viewModel.success,
                enter = androidx.compose.animation.scaleIn() + androidx.compose.animation.fadeIn(),
                exit = androidx.compose.animation.scaleOut() + androidx.compose.animation.fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Surface(
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 20.dp,
                    modifier = Modifier.padding(32.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(vertical = 32.dp, horizontal = 48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Registrasi Berhasil!",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColorPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Silakan login untuk masuk",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}