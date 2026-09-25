package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.model.Citizen
import com.example.model.CitizenCategory
import com.example.ui.CitizenViewModel
import com.example.ui.components.CitizenCard
import com.example.ui.components.StatCard
import com.example.ui.theme.ColorBlockchainValid
import com.example.ui.theme.ColorWargaAsli
import com.example.ui.theme.ColorWargaNgontrak
import com.example.ui.theme.ColorWargaPendatang

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: CitizenViewModel,
    onNavigateToAddCitizen: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateToExplorer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val citizens by viewModel.allCitizens.collectAsStateWithLifecycle()
    val filteredCitizens by viewModel.filteredCitizens.collectAsStateWithLifecycle()
    val allBlocks by viewModel.allBlocks.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.categoryFilter.collectAsStateWithLifecycle()

    var citizenToDelete by remember { mutableStateOf<Citizen?>(null) }

    // Quick Stats Calculation
    val totalKk = citizens.size
    val wargaAsliCount = citizens.count { it.category == CitizenCategory.WARGA_ASLI }
    val wargaPendatangCount = citizens.count { it.category == CitizenCategory.WARGA_PENDATANG }
    val wargaNgontrakCount = citizens.count { it.category == CitizenCategory.WARGA_NGONTRAK }
    val totalJiwa = citizens.sumOf { it.familyMembersCount }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToAddCitizen,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Tambah Warga", fontWeight = FontWeight.Bold) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_citizen_fab")
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // Hero Header with Banner & Blockchain Status
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.banner_rt_ledger_1790340134026),
                        contentDescription = "Buku Besar RT Blockchain",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                androidx.compose.ui.graphics.Brush.verticalGradient(
                                    listOf(
                                        Color(0x660F172A),
                                        Color(0xEE0F172A)
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // RT Info Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "BUKU BESAR KEPENDUDUKAN",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Rukun Tetangga 03 / RW 07",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "Kel. Sukamaju, Kec. Cilodong",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 12.sp
                                )
                            }

                            // Live Blockchain Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0xFF0F172A).copy(alpha = 0.8f))
                                    .border(1.dp, ColorBlockchainValid, RoundedCornerShape(20.dp))
                                    .clickable { onNavigateToExplorer() }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.VerifiedUser,
                                        contentDescription = null,
                                        tint = ColorBlockchainValid,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${allBlocks.size} Blok Sah",
                                        color = ColorBlockchainValid,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Bottom banner note: Decentralized transparency
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Data kependudukan terenkripsi permanen & terdesentralisasi",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Quick Stats Row
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Text(
                        text = "Ringkasan Warga RT",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatCard(
                            title = "Total KK",
                            value = totalKk.toString(),
                            subtitle = "$totalJiwa Jiwa",
                            icon = Icons.Default.People,
                            iconBgColor = MaterialTheme.colorScheme.primaryContainer,
                            iconTintColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Warga Asli",
                            value = wargaAsliCount.toString(),
                            subtitle = "Tetap",
                            icon = Icons.Default.Home,
                            iconBgColor = Color(0xFFDCFCE7),
                            iconTintColor = ColorWargaAsli,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.setCategoryFilter(CitizenCategory.WARGA_ASLI) }
                        )
                        StatCard(
                            title = "Pendatang",
                            value = wargaPendatangCount.toString(),
                            subtitle = "Domisili",
                            icon = Icons.Default.LocationCity,
                            iconBgColor = Color(0xFFE0F2FE),
                            iconTintColor = ColorWargaPendatang,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.setCategoryFilter(CitizenCategory.WARGA_PENDATANG) }
                        )
                        StatCard(
                            title = "Ngontrak",
                            value = wargaNgontrakCount.toString(),
                            subtitle = "Sewa",
                            icon = Icons.Default.MeetingRoom,
                            iconBgColor = Color(0xFFFEF3C7),
                            iconTintColor = ColorWargaNgontrak,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.setCategoryFilter(CitizenCategory.WARGA_NGONTRAK) }
                        )
                    }
                }
            }

            // Search Bar & Filter Chips
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("Cari nama, NIK, No. KK, atau alamat...") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Hapus pencarian")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_citizen_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Filter Chips Row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChip(
                                selected = selectedCategory == null,
                                onClick = { viewModel.setCategoryFilter(null) },
                                label = { Text("Semua Kategori (${citizens.size})") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                        item {
                            FilterChip(
                                selected = selectedCategory == CitizenCategory.WARGA_ASLI,
                                onClick = {
                                    viewModel.setCategoryFilter(
                                        if (selectedCategory == CitizenCategory.WARGA_ASLI) null else CitizenCategory.WARGA_ASLI
                                    )
                                },
                                label = { Text("Warga Asli ($wargaAsliCount)") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Home,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = ColorWargaAsli
                                    )
                                }
                            )
                        }
                        item {
                            FilterChip(
                                selected = selectedCategory == CitizenCategory.WARGA_PENDATANG,
                                onClick = {
                                    viewModel.setCategoryFilter(
                                        if (selectedCategory == CitizenCategory.WARGA_PENDATANG) null else CitizenCategory.WARGA_PENDATANG
                                    )
                                },
                                label = { Text("Pendatang ($wargaPendatangCount)") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.LocationCity,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = ColorWargaPendatang
                                    )
                                }
                            )
                        }
                        item {
                            FilterChip(
                                selected = selectedCategory == CitizenCategory.WARGA_NGONTRAK,
                                onClick = {
                                    viewModel.setCategoryFilter(
                                        if (selectedCategory == CitizenCategory.WARGA_NGONTRAK) null else CitizenCategory.WARGA_NGONTRAK
                                    )
                                },
                                label = { Text("Ngontrak ($wargaNgontrakCount)") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.MeetingRoom,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = ColorWargaNgontrak
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // Section Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Daftar Kepala Keluarga (${filteredCitizens.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = onNavigateToExplorer) {
                        Icon(
                            imageVector = Icons.Default.Hub,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Buku Besar Blockchain", fontSize = 12.sp)
                    }
                }
            }

            // Citizens List or Empty State
            if (filteredCitizens.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.People,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Tidak Ada Data Warga",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (searchQuery.isNotBlank()) "Tidak ditemukan warga yang cocok dengan kata kunci."
                                else "Belum ada data warga terdaftar. Tekan tombol Tambah Warga untuk memulai.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(
                    items = filteredCitizens,
                    key = { it.id }
                ) { citizen ->
                    CitizenCard(
                        citizen = citizen,
                        onDetailClick = { onNavigateToDetail(citizen.id) },
                        onEditClick = { onNavigateToEdit(citizen.id) },
                        onDeleteClick = { citizenToDelete = citizen },
                        onHashClick = { onNavigateToExplorer() },
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .testTag("citizen_card_${citizen.id}")
                    )
                }
            }
        }
    }

    // Delete Confirmation Dialog
    citizenToDelete?.let { citizen ->
        AlertDialog(
            onDismissRequest = { citizenToDelete = null },
            icon = { Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Hapus Data Kependudukan?") },
            text = {
                Text(
                    "Peringatan: Penghapusan warga '${citizen.fullName}' akan dicatat secara permanen di Blockchain Ledger sebagai mutasi nonaktif/pindah domisili demi menjaga transparansi audit RT."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCitizen(citizen)
                        citizenToDelete = null
                    }
                ) {
                    Text("Catat Mutasi Hapus", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { citizenToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }
}
