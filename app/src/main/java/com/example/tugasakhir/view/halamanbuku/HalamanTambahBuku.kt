package com.example.tugasakhir.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tugasakhir.R
import com.example.tugasakhir.viewmodel.TambahBukuViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanTambahBuku(
    viewModel: TambahBukuViewModel,
    onBack: () -> Unit
) {
    // State Input
    var judul by remember { mutableStateOf("") }
    var penulis by remember { mutableStateOf("") }
    var penerbit by remember { mutableStateOf("") }
    var tahunTerbit by remember { mutableStateOf(2025) }
    var stok by remember { mutableStateOf(1) }

    // Navigation delay after success
    LaunchedEffect(viewModel.success) {
        if (viewModel.success) {
            kotlinx.coroutines.delay(800)
            onBack()
        }
    }

    val primaryColor = colorResource(id = R.color.primary_blue)
    val backgroundColor = colorResource(id = R.color.background)
    val textColorPrimary = colorResource(id = R.color.text_primary)
    val errorColor = MaterialTheme.colorScheme.error

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = backgroundColor
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // --- HEADER SECTION ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.9f))
                            .shadow(2.dp, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.desc_back),
                            tint = primaryColor
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = stringResource(R.string.title_add_book),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = textColorPrimary,
                            letterSpacing = 1.sp
                        )
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(4.dp)
                                .background(primaryColor, RoundedCornerShape(2.dp))
                        )
                    }
                }

                // --- TOP ERROR MESSAGE (General) ---
                androidx.compose.animation.AnimatedVisibility(visible = viewModel.errorMessage != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = errorColor.copy(alpha = 0.1f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, errorColor.copy(alpha = 0.5f))
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Error, null, tint = errorColor, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = viewModel.errorMessage ?: "", color = errorColor, fontSize = 13.sp)
                        }
                    }
                }

                // --- FORM SECTION ---
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .shadow(16.dp, RoundedCornerShape(28.dp)),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        CustomInputForm(
                            value = judul,
                            onValueChange = { judul = it; viewModel.judulError = null },
                            label = stringResource(R.string.label_judul),
                            icon = Icons.Default.MenuBook,
                            isError = viewModel.judulError != null,
                            errorMessage = viewModel.judulError ?: "",
                            primaryColor = primaryColor
                        )

                        CustomInputForm(
                            value = penulis,
                            onValueChange = { penulis = it; viewModel.penulisError = null },
                            label = stringResource(R.string.label_penulis),
                            icon = Icons.Default.Person,
                            isError = viewModel.penulisError != null,
                            errorMessage = viewModel.penulisError ?: "",
                            primaryColor = primaryColor
                        )

                        CustomInputForm(
                            value = penerbit,
                            onValueChange = { penerbit = it; viewModel.penerbitError = null },
                            label = stringResource(R.string.label_penerbit),
                            icon = Icons.Default.Business,
                            isError = viewModel.penerbitError != null,
                            errorMessage = viewModel.penerbitError ?: "",
                            primaryColor = primaryColor
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                DropdownAngkaPremium(
                                    label = stringResource(R.string.label_tahun_singkat),
                                    selected = tahunTerbit,
                                    range = (1980..2025).toList().reversed(),
                                    onSelected = { tahunTerbit = it },
                                    primaryColor = primaryColor
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                DropdownAngkaPremium(
                                    label = stringResource(R.string.label_stok_singkat),
                                    selected = stok,
                                    range = (1..100).toList(),
                                    onSelected = { stok = it },
                                    primaryColor = primaryColor
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                viewModel.simpanBuku(
                                    judul = judul,
                                    penulis = penulis,
                                    penerbit = penerbit,
                                    tahun = tahunTerbit,
                                    stok = stok
                                )
                            },
                            enabled = !viewModel.isLoading,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                        ) {
                            if (viewModel.isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Icon(Icons.Default.CloudUpload, contentDescription = null)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = stringResource(R.string.btn_simpan_buku),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        // --- SUCCESS OVERLAY ---
        androidx.compose.animation.AnimatedVisibility(
            visible = viewModel.success,
            enter = androidx.compose.animation.scaleIn() + androidx.compose.animation.fadeIn(),
            exit = androidx.compose.animation.scaleOut() + androidx.compose.animation.fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Surface(
                modifier = Modifier.size(220.dp),
                shape = RoundedCornerShape(28.dp),
                color = Color.White,
                tonalElevation = 8.dp,
                shadowElevation = 12.dp
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Data Berhasil Disimpan!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColorPrimary
                    )
                }
            }
        }
    }
}

// ... (CustomInputForm dan DropdownAngkaPremium tetap sama seperti sebelumnya)
@Composable
fun CustomInputForm(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isError: Boolean,
    errorMessage: String,
    primaryColor: Color
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            leadingIcon = {
                Icon(icon, contentDescription = null, tint = if (isError) MaterialTheme.colorScheme.error else primaryColor)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            isError = isError,
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
                focusedLabelColor = primaryColor,
                unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
            )
        )
        if (isError) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 12.dp, top = 4.dp),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownAngkaPremium(
    label: String,
    selected: Int,
    range: List<Int>,
    onSelected: (Int) -> Unit,
    primaryColor: Color
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected.toString(),
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
                focusedLabelColor = primaryColor,
                unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            range.forEach { angka ->
                DropdownMenuItem(
                    text = { Text(text = angka.toString(), fontWeight = FontWeight.Medium) },
                    onClick = {
                        onSelected(angka)
                        expanded = false
                    }
                )
            }
        }
    }
}