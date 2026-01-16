package com.example.tugasakhir.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tugasakhir.R
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import com.example.tugasakhir.view.component.KomponenError
import com.example.tugasakhir.view.route.DestinasiLogin // Import objek rute yang baru
import com.example.tugasakhir.viewmodel.EntryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun HalamanEntry(
    viewModel: EntryViewModel,
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    navigateUp: () -> Unit = {}
) {
    var passwordVisible by remember { mutableStateOf(false) }

    // Warna dari resources
    val primaryColor = colorResource(id = R.color.primary_blue)
    val textColorPrimary = colorResource(id = R.color.text_primary)

    Scaffold { paddingValues ->
        val scrollState = androidx.compose.foundation.rememberScrollState()

        // --- NAVIGASI OTOMATIS SAAT SUKSES ---
        LaunchedEffect(viewModel.loginSuccess) {
            if (viewModel.loginSuccess) {
                kotlinx.coroutines.delay(800) // Tampilkan animasi sebentar
                onLoginSuccess() // Panggil navigasi
            }
        }

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
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(60.dp))

                // 1. Illustration (Top)
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

                // 3. Input Fields
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Input Username
                    com.example.tugasakhir.view.component.CustomTextField(
                        value = viewModel.username,
                        onValueChange = {
                            viewModel.username = it
                            viewModel.usernameError = null
                        },
                        label = stringResource(R.string.username_hint),
                        leadingIcon = Icons.Filled.AccountCircle,
                        isError = viewModel.usernameError != null,
                        errorMessage = viewModel.usernameError,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Input Password
                    com.example.tugasakhir.view.component.CustomTextField(
                        value = viewModel.password,
                        onValueChange = {
                            viewModel.password = it
                            viewModel.passwordError = null
                        },
                        label = stringResource(R.string.password_hint),
                        leadingIcon = Icons.Filled.Lock,
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

                // 4. Tombol Masuk
                Button(
                    onClick = { viewModel.login(onLoginSuccess) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(28.dp),
                            spotColor = primaryColor
                        ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = stringResource(R.string.btn_login),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 5. Link Register
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Belum punya akun?",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    TextButton(onClick = onRegisterClick) {
                        Text(
                            text = stringResource(R.string.btn_register),
                            color = primaryColor,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(60.dp))
            }

            // --- Success Animation Overlay ---
            androidx.compose.animation.AnimatedVisibility(
                visible = viewModel.loginSuccess,
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
                        // Icon Check Animated could be here, but using standard Icon for now
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Login Berhasil!",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColorPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Selamat Datang Kembali",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}