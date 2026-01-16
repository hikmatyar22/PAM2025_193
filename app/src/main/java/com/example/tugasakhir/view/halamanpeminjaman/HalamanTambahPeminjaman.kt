package com.example.tugasakhir.view.halamanpeminjaman

import android.app.DatePickerDialog
import android.widget.DatePicker
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tugasakhir.R
import com.example.tugasakhir.view.CustomInputForm
import com.example.tugasakhir.viewmodel.TambahPeminjamanViewModel
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanTambahPeminjaman(
    viewModel: TambahPeminjamanViewModel,
    onBack: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()
    val daftarBuku by viewModel.daftarBuku.collectAsState(initial = emptyList())
    val context = LocalContext.current

    // Navigation on success
    LaunchedEffect(viewModel.success) {
        if (viewModel.success) {
            kotlinx.coroutines.delay(800)
            onNavigateBack()
        }
    }

    val primaryColor = colorResource(id = R.color.primary_blue)
    val backgroundColor = colorResource(id = R.color.background)
    val textColorPrimary = colorResource(id = R.color.text_primary)

    // Helper DatePicker
    fun tampilkanCalendar(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, day: Int ->
                val formattedMonth = (month + 1).toString().padStart(2, '0')
                val formattedDay = day.toString().padStart(2, '0')
                onDateSelected("$year-$formattedMonth-$formattedDay")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(containerColor = backgroundColor) { padding ->
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
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = primaryColor)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.title_add_loan),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = textColorPrimary
                        )
                        Box(modifier = Modifier.width(40.dp).height(4.dp).background(primaryColor, RoundedCornerShape(2.dp)))
                    }
                }

                // --- ERROR MESSAGE (General) ---
                androidx.compose.animation.AnimatedVisibility(visible = viewModel.errorMessage != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Error, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(viewModel.errorMessage ?: "", color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                        }
                    }
                }

                // --- FORM SECTION ---
                androidx.compose.animation.AnimatedVisibility(
                    visible = true, // To apply similar transition as Books
                    enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.slideInVertically { it / 2 }
                ) {
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
                            // Input Nama
                            CustomInputForm(
                                value = uiState.peminjamanEvent.nama_peminjam,
                                onValueChange = {
                                    viewModel.updateUiState(uiState.peminjamanEvent.copy(nama_peminjam = it))
                                },
                                label = stringResource(R.string.label_nama_peminjam),
                                icon = Icons.Default.Person,
                                isError = viewModel.namaError != null,
                                errorMessage = viewModel.namaError ?: "",
                                primaryColor = primaryColor
                            )

                            // Input Email
                            CustomInputForm(
                                value = uiState.peminjamanEvent.email_peminjam,
                                onValueChange = {
                                    viewModel.updateUiState(uiState.peminjamanEvent.copy(email_peminjam = it))
                                },
                                label = stringResource(R.string.label_email),
                                icon = Icons.Default.Email,
                                isError = viewModel.emailError != null,
                                errorMessage = viewModel.emailError ?: "",
                                primaryColor = primaryColor
                            )

                            // Dropdown Buku
                            var expanded by remember { mutableStateOf(false) }
                            val selectedBook = daftarBuku.find { it.id_buku == uiState.peminjamanEvent.id_buku }

                            Column {
                                ExposedDropdownMenuBox(
                                    expanded = expanded,
                                    onExpandedChange = { expanded = !expanded }
                                ) {
                                    OutlinedTextField(
                                        value = selectedBook?.judul ?: stringResource(R.string.label_pilih_buku),
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text(stringResource(R.string.label_pilih_buku)) },
                                        leadingIcon = {
                                            Icon(Icons.Default.MenuBook, null, tint = if (viewModel.bukuError != null) MaterialTheme.colorScheme.error else primaryColor)
                                        },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        isError = viewModel.bukuError != null,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = primaryColor,
                                            unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
                                        )
                                    )
                                    ExposedDropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false },
                                        modifier = Modifier.background(Color.White)
                                    ) {
                                        daftarBuku.forEach { buku ->
                                            DropdownMenuItem(
                                                text = { Text("${buku.judul} (Tersedia: ${buku.jumlah_tersedia})") },
                                                onClick = {
                                                    if (buku.jumlah_tersedia > 0) {
                                                        viewModel.updateUiState(uiState.peminjamanEvent.copy(id_buku = buku.id_buku))
                                                        expanded = false
                                                    }
                                                },
                                                enabled = buku.jumlah_tersedia > 0
                                            )
                                        }
                                    }
                                }
                                if (viewModel.bukuError != null) {
                                    Text(
                                        text = viewModel.bukuError!!,
                                        color = MaterialTheme.colorScheme.error,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                                    )
                                }
                            }

                            // Tanggal Pinjam
                            CustomDatePickerForm(
                                value = uiState.peminjamanEvent.tanggal_pinjam,
                                label = stringResource(R.string.label_tgl_pinjam),
                                isError = viewModel.tglPinjamError != null,
                                errorMessage = viewModel.tglPinjamError ?: "",
                                onDateClick = {
                                    tampilkanCalendar {
                                        viewModel.updateUiState(uiState.peminjamanEvent.copy(tanggal_pinjam = it))
                                    }
                                },
                                primaryColor = primaryColor
                            )

                            // Tanggal Jatuh Tempo
                            CustomDatePickerForm(
                                value = uiState.peminjamanEvent.tanggal_jatuh_tempo,
                                label = stringResource(R.string.label_tgl_tempo),
                                isError = viewModel.tglTempoError != null,
                                errorMessage = viewModel.tglTempoError ?: "",
                                onDateClick = {
                                    tampilkanCalendar {
                                        viewModel.updateUiState(uiState.peminjamanEvent.copy(tanggal_jatuh_tempo = it))
                                    }
                                },
                                primaryColor = primaryColor
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Tombol Simpan
                            Button(
                                onClick = { viewModel.simpanPeminjaman() },
                                enabled = !viewModel.isLoading,
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                            ) {
                                if (viewModel.isLoading) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                                } else {
                                    Icon(Icons.Default.AssignmentTurnedIn, null)
                                    Spacer(Modifier.width(10.dp))
                                    Text(stringResource(R.string.btn_simpan_transaksi), fontWeight = FontWeight.Bold)
                                }
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
                        text = "Peminjaman Berhasil Disimpan",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColorPrimary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun CustomDatePickerForm(
    value: String,
    label: String,
    isError: Boolean,
    errorMessage: String,
    onDateClick: () -> Unit,
    primaryColor: Color
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            leadingIcon = {
                Icon(Icons.Default.DateRange, null, tint = if (isError) MaterialTheme.colorScheme.error else primaryColor)
            },
            trailingIcon = {
                IconButton(onClick = onDateClick) {
                    Icon(Icons.Default.CalendarMonth, null, tint = primaryColor)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            isError = isError,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
            )
        )
        if (isError) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 12.dp, top = 4.dp)
            )
        }
    }
}
