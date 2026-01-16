package com.example.tugasakhir.view

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tugasakhir.R
import com.example.tugasakhir.data.modeldata.Buku
import com.example.tugasakhir.viewmodel.DetailBukuViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanDetailBuku(
    viewModel: DetailBukuViewModel,
    onBack: () -> Unit
) {
    val buku by viewModel.detailUiState.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

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
    val errorColor = colorResource(id = R.color.button_danger)
    val textColorPrimary = colorResource(R.color.text_primary)

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(containerColor = backgroundColor) { padding ->
            PullToRefreshBox(
                isRefreshing = viewModel.isLoading,
                onRefresh = { viewModel.refresh() },
                modifier = Modifier.padding(padding)
            ) {
                AnimatedVisibility(
                    visible = buku != null,
                    enter = fadeIn() + slideInVertically { it / 2 }
                ) {
                    buku?.let { dataBuku ->
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
                                    text = stringResource(R.string.title_detail_buku),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColorPrimary
                                )
                            }

                            // --- ERROR MESSAGE ---
                            AnimatedVisibility(visible = viewModel.errorMessage != null) {
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                                    colors = CardDefaults.cardColors(containerColor = errorColor.copy(alpha = 0.1f)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, errorColor.copy(alpha = 0.5f))
                                ) {
                                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Error, null, tint = errorColor, modifier = Modifier.size(20.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text(viewModel.errorMessage ?: "", color = errorColor, fontSize = 13.sp)
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
                                        Text(dataBuku.judul, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = primaryColor, lineHeight = 30.sp)
                                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))

                                        val stockColor = if (dataBuku.jumlah_stok == 0) Color.Red else primaryColor
                                        val tersediaColor = if (dataBuku.jumlah_tersedia == 0) Color.Red else Color(0xFF2E7D32)

                                        DetailItem(stringResource(R.string.label_penulis), dataBuku.penulis, Icons.Default.Person, primaryColor)
                                        DetailItem(stringResource(R.string.label_penerbit), dataBuku.penerbit, Icons.Default.Business, primaryColor)
                                        DetailItem(stringResource(R.string.label_tahun), dataBuku.tahun_terbit.toString(), Icons.Default.CalendarToday, primaryColor)
                                        DetailItem(stringResource(R.string.label_stok_total), dataBuku.jumlah_stok.toString(), Icons.Default.Inventory, stockColor)
                                        DetailItem(stringResource(R.string.label_tersedia), dataBuku.jumlah_tersedia.toString(), Icons.Default.CheckCircle, tersediaColor)
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

                                    OutlinedButton(
                                        onClick = { showDeleteConfirm = true },
                                        enabled = !viewModel.isLoading,
                                        modifier = Modifier.weight(1f).height(54.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = errorColor),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, errorColor.copy(alpha = 0.3f))
                                    ) {
                                        if (viewModel.isLoading && viewModel.deleteSuccess) {
                                            CircularProgressIndicator(color = errorColor, modifier = Modifier.size(18.dp))
                                        } else {
                                            Icon(Icons.Default.Delete, null, modifier = Modifier.size(18.dp))
                                            Spacer(Modifier.width(8.dp))
                                            Text(stringResource(R.string.btn_hapus), fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            // Dialogs inside buku?.let but outside of scrollable Columns
                            if (showEditDialog) {
                                EditBukuDialog(
                                    buku = dataBuku,
                                    onDismiss = { showEditDialog = false },
                                    onConfirm = { updated ->
                                        viewModel.updateBuku(updated)
                                        showEditDialog = false
                                    },
                                    primaryColor = primaryColor
                                )
                            }

                            if (showDeleteConfirm) {
                                AlertDialog(
                                    onDismissRequest = { showDeleteConfirm = false },
                                    title = { Text(stringResource(R.string.dialog_delete_title)) },
                                    text = { Text(stringResource(R.string.dialog_delete_msg, dataBuku.judul)) },
                                    confirmButton = {
                                        Button(
                                            onClick = {
                                                viewModel.hapusBuku()
                                                showDeleteConfirm = false
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = errorColor)
                                        ) { Text(stringResource(R.string.btn_hapus)) }
                                    },
                                    dismissButton = {
                                        TextButton(onClick = { showDeleteConfirm = false }) {
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
        AnimatedVisibility(
            visible = viewModel.updateSuccess || viewModel.deleteSuccess,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut(),
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
                        text = if (viewModel.deleteSuccess) "Buku Berhasil Dihapus" else "Buku Berhasil Diperbarui",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColorPrimary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun DetailItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    primaryColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(primaryColor.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, modifier = Modifier.size(20.dp), tint = primaryColor)
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
fun EditBukuDialog(
    buku: Buku,
    onDismiss: () -> Unit,
    onConfirm: (Buku) -> Unit,
    primaryColor: Color
) {
    var judul by remember { mutableStateOf(buku.judul) }
    var penulis by remember { mutableStateOf(buku.penulis) }
    var penerbit by remember { mutableStateOf(buku.penerbit) }

    var judulError by remember { mutableStateOf<String?>(null) }
    var penulisError by remember { mutableStateOf<String?>(null) }
    var penerbitError by remember { mutableStateOf<String?>(null) }

    var expandedTahun by remember { mutableStateOf(false) }
    val tahunSekarang = Calendar.getInstance().get(Calendar.YEAR)
    val daftarTahun = (tahunSekarang downTo 1900).map { it.toString() }
    var tahunTerpilih by remember { mutableStateOf(buku.tahun_terbit.toString()) }

    var expandedStok by remember { mutableStateOf(false) }
    val daftarStok = (1..100).map { it.toString() }
    var stokTerpilih by remember { mutableStateOf(buku.jumlah_stok.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.title_edit_buku), fontWeight = FontWeight.Bold, fontSize = 20.sp) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Column {
                    OutlinedTextField(
                        value = judul,
                        onValueChange = { judul = it; judulError = null },
                        label = { Text(stringResource(R.string.hint_judul)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        isError = judulError != null,
                        singleLine = true
                    )
                    if (judulError != null) {
                        Text(judulError!!, color = MaterialTheme.colorScheme.error, fontSize = 11.sp, modifier = Modifier.padding(start = 4.dp, top = 2.dp))
                    }
                }

                Column {
                    OutlinedTextField(
                        value = penulis,
                        onValueChange = { penulis = it; penulisError = null },
                        label = { Text(stringResource(R.string.label_penulis)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        isError = penulisError != null,
                        singleLine = true
                    )
                    if (penulisError != null) {
                        Text(penulisError!!, color = MaterialTheme.colorScheme.error, fontSize = 11.sp, modifier = Modifier.padding(start = 4.dp, top = 2.dp))
                    }
                }

                Column {
                    OutlinedTextField(
                        value = penerbit,
                        onValueChange = { penerbit = it; penerbitError = null },
                        label = { Text(stringResource(R.string.label_penerbit)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        isError = penerbitError != null,
                        singleLine = true
                    )
                    if (penerbitError != null) {
                        Text(penerbitError!!, color = MaterialTheme.colorScheme.error, fontSize = 11.sp, modifier = Modifier.padding(start = 4.dp, top = 2.dp))
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = expandedTahun,
                    onExpandedChange = { expandedTahun = !expandedTahun }
                ) {
                    OutlinedTextField(
                        value = tahunTerpilih,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.label_tahun)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTahun) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedTahun,
                        onDismissRequest = { expandedTahun = false }
                    ) {
                        daftarTahun.forEach { thn ->
                            DropdownMenuItem(
                                text = { Text(thn) },
                                onClick = { tahunTerpilih = thn; expandedTahun = false }
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = expandedStok,
                    onExpandedChange = { expandedStok = !expandedStok }
                ) {
                    OutlinedTextField(
                        value = stokTerpilih,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.label_stok_total)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStok) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedStok,
                        onDismissRequest = { expandedStok = false }
                    ) {
                        daftarStok.forEach { s ->
                            DropdownMenuItem(
                                text = { Text(s) },
                                onClick = { stokTerpilih = s; expandedStok = false }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    var hasError = false
                    if (judul.isBlank()) { judulError = "Judul tidak boleh kosong"; hasError = true }
                    if (penulis.isBlank()) { penulisError = "Penulis tidak boleh kosong"; hasError = true }
                    if (penerbit.isBlank()) { penerbitError = "Penerbit tidak boleh kosong"; hasError = true }

                    if (!hasError) {
                        val stokInt = stokTerpilih.toIntOrNull() ?: buku.jumlah_stok
                        onConfirm(
                            buku.copy(
                                judul = judul.trim(),
                                penulis = penulis.trim(),
                                penerbit = penerbit.trim(),
                                tahun_terbit = tahunTerpilih.toInt(),
                                jumlah_stok = stokInt,
                                jumlah_tersedia = stokInt
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                shape = RoundedCornerShape(12.dp)
            ) { Text(stringResource(R.string.btn_simpan)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.btn_batal), color = Color.Gray)
            }
        }
    )
}
