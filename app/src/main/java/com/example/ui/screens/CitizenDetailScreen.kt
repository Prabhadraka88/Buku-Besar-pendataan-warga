package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.blockchain.BlockchainEngine
import com.example.model.Citizen
import com.example.ui.CitizenViewModel
import com.example.ui.components.BlockItemCard
import com.example.ui.components.CategoryBadge
import com.example.ui.components.DigitalCertificateCard
import com.example.ui.theme.ColorBlockchainValid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitizenDetailScreen(
    citizenId: Long,
    viewModel: CitizenViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val citizens by viewModel.allCitizens.collectAsStateWithLifecycle()
    val allBlocks by viewModel.allBlocks.collectAsStateWithLifecycle()

    val citizen = citizens.firstOrNull { it.id == citizenId }
    val citizenBlocks = citizen?.let { c ->
        allBlocks.filter { it.citizenKk == c.kkNumber }
    } ?: emptyList()

    var selectedTab by remember { mutableIntStateOf(0) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (citizen == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Data warga tidak ditemukan.")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onNavigateBack) { Text("Kembali") }
            }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profil Kepala Keluarga",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToEdit(citizen.id) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Warga")
                    }
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                edgePadding = 16.dp
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Profil & Kontak", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Sertifikat Digital", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Audit Ledger (${citizenBlocks.size})", fontWeight = FontWeight.Bold) }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (selectedTab) {
                    0 -> ProfileAndContactTab(citizen = citizen, context = context)
                    1 -> CertificateTab(citizen = citizen, context = context)
                    2 -> BlockchainAuditTab(blocks = citizenBlocks, viewModel = viewModel)
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            icon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Hapus Warga dari Buku RT?") },
            text = {
                Text("Tindakan ini akan menambang mutasi HAPUS_WARGA ke dalam Blockchain RT secara permanen.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCitizen(citizen)
                        showDeleteConfirm = false
                        onNavigateBack()
                    }
                ) {
                    Text("Hapus & Catat Mutasi", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun ProfileAndContactTab(citizen: Citizen, context: Context) {
    // Top Hero Card
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = citizen.fullName.firstOrNull()?.uppercase() ?: "W",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = citizen.fullName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    CategoryBadge(category = citizen.category)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(14.dp))

            // Quick Contact Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { dialPhone(context, citizen.phone) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Telepon", fontSize = 12.sp)
                }

                Button(
                    onClick = { openWaChat(context, citizen.phone, citizen.fullName) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ColorBlockchainValid)
                ) {
                    Icon(Icons.Outlined.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("WhatsApp", fontSize = 12.sp)
                }
            }
        }
    }

    // Detail Sections
    DetailCardSection(title = "Data Identitas Kepala Keluarga") {
        DetailItem(label = "Nomor KK (16 digit)", value = citizen.kkNumber, isMono = true)
        DetailItem(label = "NIK Kepala Keluarga", value = citizen.nikHead, isMono = true)
        DetailItem(label = "Pekerjaan", value = citizen.occupation)
        DetailItem(label = "Jumlah Anggota Keluarga", value = "${citizen.familyMembersCount} Jiwa")
        if (citizen.familyMembersNames.isNotBlank()) {
            DetailItem(label = "Daftar Anggota Keluarga", value = citizen.familyMembersNames)
        }
    }

    DetailCardSection(title = "Alamat Lengkap & Domisili RT") {
        DetailItem(label = "Alamat Rumah", value = citizen.addressStreet)
        DetailItem(label = "Wilayah", value = citizen.rtRw)
        DetailItem(label = "Kode Pos", value = citizen.postalCode)
        DetailItem(label = "Status Rumah", value = citizen.houseOwnership)
        DetailItem(label = "Menetap Sejak", value = citizen.settledSince)
    }

    DetailCardSection(title = "Data Kontak & Darurat") {
        DetailItem(label = "No. Telepon / WhatsApp", value = citizen.phone)
        if (citizen.email.isNotBlank()) {
            DetailItem(label = "Alamat Email", value = citizen.email)
        }
        if (citizen.emergencyContactName.isNotBlank()) {
            DetailItem(
                label = "Kontak Darurat",
                value = "${citizen.emergencyContactName} (${citizen.emergencyContactPhone})"
            )
        }
    }

    DetailCardSection(title = "Administrasi RT & Catatan") {
        DetailItem(label = "Status Iuran RT", value = citizen.feeStatus)
        if (citizen.notes.isNotBlank()) {
            DetailItem(label = "Catatan Khusus", value = citizen.notes)
        }
    }
}

@Composable
private fun CertificateTab(citizen: Citizen, context: Context) {
    DigitalCertificateCard(citizen = citizen)

    Spacer(modifier = Modifier.height(8.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = {
                val clip = ClipData.newPlainText("Block Hash RT", citizen.lastBlockHash)
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Hash Blockchain disalin ke papan klip!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Salin Hash", fontSize = 12.sp)
        }

        Button(
            onClick = {
                val shareText = """
                    SERTIFIKAT KEPENDUDUKAN DIGITAL RT 03 / RW 07
                    Nama: ${citizen.fullName}
                    No KK: ${citizen.kkNumber}
                    Kategori: ${citizen.category.title}
                    Alamat: ${citizen.addressStreet}, ${citizen.rtRw}
                    Blockchain Hash: ${citizen.lastBlockHash}
                    Status: Sah & Terdaftar Permanen
                """.trimIndent()
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "Sertifikat Warga RT - ${citizen.fullName}")
                    putExtra(Intent.EXTRA_TEXT, shareText)
                }
                context.startActivity(Intent.createChooser(intent, "Bagikan Sertifikat Digital RT"))
            },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Bagikan", fontSize = 12.sp)
        }
    }
}

@Composable
private fun BlockchainAuditTab(blocks: List<com.example.model.Block>, viewModel: CitizenViewModel) {
    if (blocks.isEmpty()) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Hub, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(10.dp))
                Text("Belum ada blok transaksi tercatat khusus KK ini.")
            }
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "Riwayat Blok Transaksi di Rantai RT",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            blocks.forEach { block ->
                BlockItemCard(
                    block = block,
                    onTamperClick = { viewModel.simulateTamper(block.index) }
                )
            }
        }
    }
}

@Composable
private fun DetailCardSection(title: String, content: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun DetailItem(label: String, value: String, isMono: Boolean = false) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = if (isMono) FontFamily.Monospace else FontFamily.Default,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun dialPhone(context: Context, number: String) {
    try {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Gagal memanggil nomor telepon", Toast.LENGTH_SHORT).show()
    }
}

private fun openWaChat(context: Context, number: String, name: String) {
    try {
        val formatted = number.replace("+", "").replace("-", "").replace(" ", "").let {
            if (it.startsWith("0")) "62" + it.substring(1) else it
        }
        val url = "https://api.whatsapp.com/send?phone=$formatted&text=Halo%20Bpk/Ibu%20$name,%20Pengurus%20RT%2003%20ingin%20berkoordinasi."
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    } catch (e: Exception) {
        Toast.makeText(context, "Aplikasi WhatsApp tidak ditemukan", Toast.LENGTH_SHORT).show()
    }
}
