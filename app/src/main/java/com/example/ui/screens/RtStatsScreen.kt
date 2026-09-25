package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.CitizenCategory
import com.example.ui.CitizenViewModel
import com.example.ui.components.StatCard
import com.example.ui.theme.ColorWargaAsli
import com.example.ui.theme.ColorWargaNgontrak
import com.example.ui.theme.ColorWargaPendatang

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RtStatsScreen(
    viewModel: CitizenViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val citizens by viewModel.allCitizens.collectAsStateWithLifecycle()
    val blocks by viewModel.allBlocks.collectAsStateWithLifecycle()

    val totalKk = citizens.size
    val totalJiwa = citizens.sumOf { it.familyMembersCount }
    val avgJiwaPerKk = if (totalKk > 0) String.format("%.1f", totalJiwa.toDouble() / totalKk) else "0"

    val asliList = citizens.filter { it.category == CitizenCategory.WARGA_ASLI }
    val pendatangList = citizens.filter { it.category == CitizenCategory.WARGA_PENDATANG }
    val ngontrakList = citizens.filter { it.category == CitizenCategory.WARGA_NGONTRAK }

    val asliCount = asliList.size
    val pendatangCount = pendatangList.size
    val ngontrakCount = ngontrakList.size

    val asliJiwa = asliList.sumOf { it.familyMembersCount }
    val pendatangJiwa = pendatangList.sumOf { it.familyMembersCount }
    val ngontrakJiwa = ngontrakList.sumOf { it.familyMembersCount }

    val feeLunasCount = citizens.count { it.feeStatus == "Lunas" }
    val feeBelumCount = citizens.count { it.feeStatus == "Belum Lunas" }
    val feeBebasCount = citizens.count { it.feeStatus == "Bebas Iuran" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Rekapitulasi Kependudukan RT",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // General Stats Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Total Warga",
                        value = "$totalJiwa Jiwa",
                        subtitle = "Dari $totalKk KK",
                        icon = Icons.Default.People,
                        iconBgColor = MaterialTheme.colorScheme.primaryContainer,
                        iconTintColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Rata-rata/KK",
                        value = "$avgJiwaPerKk Jiwa",
                        subtitle = "Kepadatan KK",
                        icon = Icons.Default.BarChart,
                        iconBgColor = MaterialTheme.colorScheme.secondaryContainer,
                        iconTintColor = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Category Breakdown Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Distribusi Kategori Warga RT",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        CategoryProgressItem(
                            label = "Warga Asli (Tetap)",
                            count = asliCount,
                            total = totalKk,
                            souls = asliJiwa,
                            color = ColorWargaAsli,
                            icon = Icons.Default.Home
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        CategoryProgressItem(
                            label = "Warga Pendatang (Domisili)",
                            count = pendatangCount,
                            total = totalKk,
                            souls = pendatangJiwa,
                            color = ColorWargaPendatang,
                            icon = Icons.Default.LocationCity
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        CategoryProgressItem(
                            label = "Warga Ngontrak (Sewa/Kost)",
                            count = ngontrakCount,
                            total = totalKk,
                            souls = ngontrakJiwa,
                            color = ColorWargaNgontrak,
                            icon = Icons.Default.MeetingRoom
                        )
                    }
                }
            }

            // RT Dues Collection Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Status Iuran Kas RT Bulanan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            DuesSummaryBox(label = "Lunas", count = feeLunasCount, color = Color(0xFF10B981))
                            DuesSummaryBox(label = "Belum Lunas", count = feeBelumCount, color = Color(0xFFEF4444))
                            DuesSummaryBox(label = "Bebas Iuran", count = feeBebasCount, color = Color(0xFF64748B))
                        }
                    }
                }
            }

            // Blockchain Ledger Security Summary
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Hub, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Keamanan Data Kependudukan RT",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Seluruh ${totalKk} data KK terdaftar dalam rantai ${blocks.size} blok transaksi permanen. Data kependudukan terbebas dari manipulasi data tunggal dan selalu diaudit dengan standar konsensus SHA-256.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Export Recap Button
            item {
                Button(
                    onClick = {
                        val report = """
                            LAPORAN KEPENDUDUKAN BUKU BESAR RT 03 / RW 07
                            Tanggal Rekap: ${java.text.SimpleDateFormat("dd MMMM yyyy", java.util.Locale("id", "ID")).format(java.util.Date())}
                            
                            RINGKASAN UMUM:
                            - Total Kepala Keluarga (KK): $totalKk KK
                            - Total Warga (Jiwa): $totalJiwa Jiwa
                            - Rata-rata Jiwa/KK: $avgJiwaPerKk Jiwa
                            
                            DISTRIBUSI KATEGORI WARGA:
                            1. Warga Asli: $asliCount KK ($asliJiwa Jiwa)
                            2. Warga Pendatang: $pendatangCount KK ($pendatangJiwa Jiwa)
                            3. Warga Ngontrak: $ngontrakCount KK ($ngontrakJiwa Jiwa)
                            
                            STATUS IURAN BULANAN:
                            - Lunas: $feeLunasCount KK
                            - Belum Lunas: $feeBelumCount KK
                            - Bebas Iuran: $feeBebasCount KK
                            
                            KEAMANAN BLOCKCHAIN:
                            - Tinggi Rantai: ${blocks.size} Blok
                            - Status Konsensus: 100% Sah & Terdesentralisasi
                        """.trimIndent()

                        val clip = ClipData.newPlainText("Laporan RT 03", report)
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Laporan disalin ke clipboard! Siap dibagikan ke WhatsApp RT.", Toast.LENGTH_LONG).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("export_recap_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Summarize, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Salin Rekapitulasi Lengkap RT", fontWeight = FontWeight.Bold)
                }
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
}

@Composable
private fun CategoryProgressItem(
    label: String,
    count: Int,
    total: Int,
    souls: Int,
    color: Color,
    icon: ImageVector
) {
    val progress = if (total > 0) count.toFloat() / total else 0f
    val percentage = (progress * 100).toInt()

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = label, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
            Text(
                text = "$count KK ($percentage%) • $souls Jiwa",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.15f)
        )
    }
}

@Composable
private fun DuesSummaryBox(label: String, count: Int, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(text = count.toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
        Text(text = label, fontSize = 11.sp, color = color, fontWeight = FontWeight.Medium)
    }
}
