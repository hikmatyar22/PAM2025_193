package com.example.tugasakhir.view.halamanpeminjaman

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import com.example.tugasakhir.data.modeldata.Buku
import com.example.tugasakhir.data.modeldata.Peminjaman
import com.example.tugasakhir.viewmodel.DetailPeminjamanViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanDetailPeminjaman(
    viewModel: DetailPeminjamanViewModel,
    onBack: () -> Unit
) {
    val peminjaman by viewModel.detailUiState.collectAsState()
    val bukuTerkait by viewModel.bukuTerkait.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }
    var showSelesaiConfirm by remember { mutableStateOf(false) }

    // Success Navigation logic
    LaunchedEffect(viewModel.updateSuccess, viewModel.deleteSuccess) {
        if (viewModel.updateSuccess) {
            kotlinx.coroutines.delay(800)
            viewModel.updateSuccess = false
        }
        if (viewModel.deleteSuccess) {
            kotlinx.coroutines.delay(800)
            onBack()
        }
    }

    val primaryColor = colorResource(id = R.color.primary_blue)
    val backgroundColor = colorResource(id = R.color.background)
    val successColor = Color(0xFF2E7D32)
    val textColorPrimary = colorResource(R.color.text_primary)

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(containerColor = backgroundColor) { padding ->
            PullToRefreshBox(
                isRefreshing = viewModel.isLoading,
                onRefresh = { viewModel.refresh() },
                modifier = Modifier.padding(padding)
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = peminjaman != null,
                    enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.slideInVertically { it / 2 }
                ) {
                    peminjaman?.let { data ->
                        Column(modifier = Modifier.fillMaxSize()) {

                            // --- HEADER ---
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
                                    Icon(Icons.Default.ArrowBack, stringResource(R.string.desc_back), tint = primaryColor)
                                }
                                Spacer(Modifier.width(16.dp))
                                Text(
                                    text = stringResource(R.string.title_detail_peminjaman),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColorPrimary
                                )
                            }

                            // --- ERROR MESSAGE ---
                            androidx.compose.animation.AnimatedVisibility(visible = viewModel.errorMessage != null) {
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
                                ) {
                                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Error, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = viewModel.errorMessage ?: "", color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                                    }
                                }
                            }

                            Column(
                                modifier = Modifier.padding(horizontal = 24.dp).verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // --- CARD DETAIL ---
                                Card(
                                    modifier = Modifier.fillMaxWidth().shadow(12.dp, RoundedCornerShape(28.dp)),
                                    shape = RoundedCornerShape(28.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Column(modifier = Modifier.padding(24.dp)) {
                                        Text(text = data.nama_peminjam, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = primaryColor)
                                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))

                                        DetailItemPeminjaman(stringResource(R.string.label_buku_dipinjam), data.judul_buku ?: "...", Icons.Default.MenuBook, primaryColor)
                                        DetailItemPeminjaman(stringResource(R.string.label_email), data.email_peminjam ?: "-", Icons.Default.Email, primaryColor)
                                        DetailItemPeminjaman(stringResource(R.string.label_tgl_pinjam), data.tanggal_pinjam, Icons.Default.CalendarToday, primaryColor)
                                        DetailItemPeminjaman(stringResource(R.string.label_jatuh_tempo), data.tanggal_jatuh_tempo, Icons.Default.EventBusy, Color.Red)
                                    }
                                }

                                // --- ACTION BUTTONS ---
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Button(
                                        onClick = { showEditDialog = true },
                                        enabled = !viewModel.isLoading,
                                        modifier = Modifier.weight(1f).height(54.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                                    ) {
                                        if (viewModel.isLoading && !viewModel.deleteSuccess) {
                                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                                        } else {
                                            Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp))
                                            Spacer(Modifier.width(8.dp))
                                            Text(stringResource(R.string.btn_edit), fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Button(
                                        onClick = { showSelesaiConfirm = true },
                                        enabled = !viewModel.isLoading,
                                        modifier = Modifier.weight(1f).height(54.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = successColor)
                                    ) {
                                        if (viewModel.isLoading && viewModel.deleteSuccess) {
                                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                        } else {
                                            Icon(Icons.Default.AssignmentReturn, null, modifier = Modifier.size(18.dp))
                                            Spacer(Modifier.width(8.dp))
                                            Text(stringResource(R.string.btn_selesai), fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            // --- DIALOGS ---
                            if (showEditDialog && peminjaman != null) {
                                EditPeminjamanDialog(
                                    peminjaman = data,
                                    onDismiss = { showEditDialog = false },
                                    onConfirm = { updated -> viewModel.updatePeminjaman(updated); showEditDialog = false },
                                    primaryColor = primaryColor
                                )
                            }

                            if (showSelesaiConfirm) {
                                AlertDialog(
                                    onDismissRequest = { showSelesaiConfirm = false },
                                    title = { Text(stringResource(R.string.dialog_selesai_title)) },
                                    text = { Text(stringResource(R.string.dialog_selesai_msg)) },
                                    confirmButton = {
                                        Button(
                                            onClick = {
                                                showSelesaiConfirm = false
                                                viewModel.selesaikanPeminjaman()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = successColor)
                                        ) { Text(stringResource(R.string.btn_ya_selesai)) }
                                    },
                                    dismissButton = {
                                        TextButton(onClick = { showSelesaiConfirm = false }) {
                                            Text(stringResource(R.string.btn_batal), color = Color.Gray)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- SUCCESS OVERLAY ---
        androidx.compose.animation.AnimatedVisibility(
            visible = viewModel.updateSuccess || viewModel.deleteSuccess,
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
                    Icon(Icons.Default.Check, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(80.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (viewModel.deleteSuccess) "Peminjaman Telah Selesai" else "Data Berhasil Diperbarui",
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
fun DetailItemPeminjaman(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(iconColor.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, modifier = Modifier.size(20.dp), tint = iconColor)
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(label, fontSize = 12.sp, color = Color.Gray)
            Text(
                value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.text_primary)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPeminjamanDialog(
    peminjaman: Peminjaman,
    onDismiss: () -> Unit,
    onConfirm: (Peminjaman) -> Unit,
    primaryColor: Color
) {
    var nama by remember { mutableStateOf(peminjaman.nama_peminjam) }
    var email by remember { mutableStateOf(peminjaman.email_peminjam ?: "") }
    var tglTempo by remember { mutableStateOf(peminjaman.tanggal_jatuh_tempo) }
    var namaError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var tglError by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                stringResource(R.string.title_edit_peminjam),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Column {
                    OutlinedTextField(
                        value = nama,
                        onValueChange = { 
                            nama = it
                            if (it.isNotBlank()) namaError = null
                        },
                        label = { Text(stringResource(R.string.label_nama_peminjam)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        isError = namaError != null,
                        leadingIcon = { Icon(Icons.Default.Person, null, tint = if (namaError != null) MaterialTheme.colorScheme.error else primaryColor) }
                    )
                    if (namaError != null) {
                        Text(
                            text = namaError!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                        )
                    }
                }

                Column {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { 
                            email = it
                            if (it.isNotBlank()) emailError = null
                        },
                        label = { Text(stringResource(R.string.label_email)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        isError = emailError != null,
                        leadingIcon = { Icon(Icons.Default.Email, null, tint = if (emailError != null) MaterialTheme.colorScheme.error else primaryColor) }
                    )
                    if (emailError != null) {
                        Text(
                            text = emailError!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = tglTempo,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text(stringResource(R.string.label_jatuh_tempo)) },
                    leadingIcon = { Icon(Icons.Default.EventBusy, null, tint = primaryColor) },
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(
                                Icons.Default.CalendarMonth,
                                null,
                                tint = primaryColor
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    isError = tglError != null,
                    supportingText = { if (tglError != null) Text(tglError!!, color = MaterialTheme.colorScheme.error) }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val dPinjam = try { sdf.parse(peminjaman.tanggal_pinjam) } catch(e: Exception) { null }
                    val dTempo = try { sdf.parse(tglTempo) } catch(e: Exception) { null }

                    if (nama.isBlank()) {
                        namaError = "Nama peminjam wajib diisi"
                    } else if (email.isBlank()) {
                        emailError = "Email peminjam wajib diisi"
                    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        emailError = "Format email tidak valid"
                    } else if (dPinjam != null && dTempo != null && dTempo.before(dPinjam)) {
                        tglError = "Tanggal jatuh tempo tidak boleh sebelum tanggal pinjam (${peminjaman.tanggal_pinjam})"
                    } else {
                        onConfirm(
                            peminjaman.copy(
                                nama_peminjam = nama.trim(),
                                email_peminjam = email.trim(),
                                tanggal_jatuh_tempo = tglTempo
                            )
                        )
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                shape = RoundedCornerShape(12.dp)
            ) { Text(stringResource(R.string.btn_simpan)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    stringResource(R.string.btn_batal),
                    color = Color.Gray
                )
            }
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        tglTempo = sdf.format(Date(millis))
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(
                        stringResource(R.string.btn_batal)
                    )
                }
            }
        ) { DatePicker(state = datePickerState) }
    }
}
