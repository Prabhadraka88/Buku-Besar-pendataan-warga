package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.blockchain.BlockchainEngine
import com.example.model.Citizen
import com.example.ui.theme.ColorBlockchainValid

@Composable
fun DigitalCertificateCard(
    citizen: Citizen,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    listOf(
                        Color(0xFF0284C7),
                        Color(0xFF10B981),
                        Color(0xFFF59E0B)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Certificate Top Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0284C7).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = Color(0xFF0284C7),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "RUKUN TETANGGA 03 / RW 07",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "SERTIFIKAT KEPENDUDUKAN DIGITAL",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Buku Besar Blockchain Terdesentralisasi",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(ColorBlockchainValid.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = ColorBlockchainValid,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                thickness = 1.dp
            )
            Spacer(modifier = Modifier.height(14.dp))

            // Citizen Data Rows
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CertRow(label = "NAMA KEPALA KELUARGA", value = citizen.fullName, isBold = true)
                CertRow(label = "NOMOR KARTU KELUARGA (KK)", value = citizen.kkNumber, isMono = true)
                CertRow(label = "NIK KEPALA KELUARGA", value = citizen.nikHead, isMono = true)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "KATEGORI WARGA",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                    CategoryBadge(category = citizen.category, isCompact = true)
                }

                CertRow(label = "ALAMAT LENGKAP", value = "${citizen.addressStreet}, ${citizen.rtRw}")
                CertRow(label = "STATUS TEMPAT TINGGAL", value = "${citizen.houseOwnership} (Menetap sejak ${citizen.settledSince})")
                CertRow(label = "JUMLAH ANGGOTA KELUARGA", value = "${citizen.familyMembersCount} Jiwa")
                CertRow(label = "KONTAK UTAMA", value = citizen.phone)
                if (citizen.emergencyContactName.isNotBlank()) {
                    CertRow(
                        label = "KONTAK DARURAT",
                        value = "${citizen.emergencyContactName} (${citizen.emergencyContactPhone})"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Blockchain Seal & QR Code simulation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // QR Code visual representation
                    QrCodeMatrixVisual(
                        seed = citizen.lastBlockHash.ifBlank { citizen.kkNumber },
                        modifier = Modifier.size(68.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = null,
                                tint = ColorBlockchainValid,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "BLOCK HASH IDENTIFIER",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorBlockchainValid
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = citizen.lastBlockHash.ifBlank { "GENESIS-BLOCK-0" },
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            lineHeight = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Konsensus: Terverifikasi Permanen",
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CertRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    isMono: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(0.45f)
        )
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            fontFamily = if (isMono) FontFamily.Monospace else FontFamily.Default,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(0.55f)
        )
    }
}

@Composable
fun QrCodeMatrixVisual(
    seed: String,
    modifier: Modifier = Modifier
) {
    val dark = Color(0xFF0F172A)
    val light = Color.White

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(light)
            .padding(4.dp)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val gridSize = 11
            val cellSize = size.width / gridSize
            val hashInt = seed.hashCode()

            for (row in 0 until gridSize) {
                for (col in 0 until gridSize) {
                    // Corners position detection patterns
                    val isCorner1 = row < 3 && col < 3
                    val isCorner2 = row < 3 && col >= gridSize - 3
                    val isCorner3 = row >= gridSize - 3 && col < 3

                    val fill = when {
                        isCorner1 || isCorner2 || isCorner3 -> true
                        else -> {
                            // Pseudo-random pseudo pattern based on seed hash and position
                            val bit = ((hashInt ushr (row * 2 + col)) and 1) == 1
                            bit || ((row + col) % 3 == 0)
                        }
                    }

                    if (fill) {
                        drawRect(
                            color = dark,
                            topLeft = Offset(col * cellSize, row * cellSize),
                            size = Size(cellSize - 0.5f, cellSize - 0.5f)
                        )
                    }
                }
            }
        }
    }
}
