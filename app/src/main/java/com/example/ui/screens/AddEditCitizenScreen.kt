package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Citizen
import com.example.model.CitizenCategory
import com.example.ui.CitizenViewModel
import com.example.ui.theme.ColorWargaAsli
import com.example.ui.theme.ColorWargaNgontrak
import com.example.ui.theme.ColorWargaPendatang

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCitizenScreen(
    viewModel: CitizenViewModel,
    existingCitizen: Citizen?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEdit = existingCitizen != null

    var kkNumber by remember { mutableStateOf(existingCitizen?.kkNumber ?: "") }
    var nikHead by remember { mutableStateOf(existingCitizen?.nikHead ?: "") }
    var fullName by remember { mutableStateOf(existingCitizen?.fullName ?: "") }
    var category by remember { mutableStateOf(existingCitizen?.category ?: CitizenCategory.WARGA_ASLI) }

    var phone by remember { mutableStateOf(existingCitizen?.phone ?: "") }
    var email by remember { mutableStateOf(existingCitizen?.email ?: "") }
    var emergencyContactName by remember { mutableStateOf(existingCitizen?.emergencyContactName ?: "") }
    var emergencyContactPhone by remember { mutableStateOf(existingCitizen?.emergencyContactPhone ?: "") }

    var addressStreet by remember { mutableStateOf(existingCitizen?.addressStreet ?: "") }
    var rtRw by remember { mutableStateOf(existingCitizen?.rtRw ?: "RT 03 / RW 07") }
    var postalCode by remember { mutableStateOf(existingCitizen?.postalCode ?: "15412") }
    var houseOwnership by remember { mutableStateOf(existingCitizen?.houseOwnership ?: "Milik Sendiri") }
    var settledSince by remember { mutableStateOf(existingCitizen?.settledSince ?: "2024") }

    var familyMembersCount by remember { mutableIntStateOf(existingCitizen?.familyMembersCount ?: 1) }
    var familyMembersNames by remember { mutableStateOf(existingCitizen?.familyMembersNames ?: "") }
    var occupation by remember { mutableStateOf(existingCitizen?.occupation ?: "Karyawan Swasta") }
    var notes by remember { mutableStateOf(existingCitizen?.notes ?: "") }
    var feeStatus by remember { mutableStateOf(existingCitizen?.feeStatus ?: "Lunas") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEdit) "Perbarui Data Warga RT" else "Pendaftaran Kepala Keluarga Baru",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Blockchain Info Banner
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Hub,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Konsensus Buku Besar Blockchain",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Setiap data kependudukan baru atau perubahan akan ditambang (mined) ke dalam rantai blok dengan SHA-256 dan disimpan permanen.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }

            // SECTION 1: KATEGORI KEPENDUDUKAN
            FormSectionHeader(title = "1. Kategori Kependudukan Warga RT")
            CategorySelectionGroup(
                selected = category,
                onSelected = {
                    category = it
                    if (it == CitizenCategory.WARGA_ASLI && houseOwnership == "Sewa/Kontrak") {
                        houseOwnership = "Milik Sendiri"
                    } else if (it == CitizenCategory.WARGA_NGONTRAK) {
                        houseOwnership = "Sewa/Kontrak"
                    }
                }
            )

            // SECTION 2: IDENTITAS KEPALA KELUARGA
            FormSectionHeader(title = "2. Identitas Kepala Keluarga (KK)")
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Nama Lengkap Kepala Keluarga *") },
                placeholder = { Text("Contoh: Budi Santoso") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_full_name")
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = kkNumber,
                    onValueChange = { if (it.length <= 16) kkNumber = it },
                    label = { Text("Nomor KK (16 digit) *") },
                    placeholder = { Text("320101XXXXXXXXXX") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_kk_number")
                )
                OutlinedTextField(
                    value = nikHead,
                    onValueChange = { if (it.length <= 16) nikHead = it },
                    label = { Text("NIK Kepala KK (16 digit) *") },
                    placeholder = { Text("320101XXXXXXXXXX") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_nik")
                )
            }

            // SECTION 3: KONTAK & DARURAT
            FormSectionHeader(title = "3. Data Kontak & Darurat")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Nomor HP / WhatsApp *") },
                    placeholder = { Text("Contoh: 081234567890") },
                    leadingIcon = { Icon(Icons.Default.Call, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_phone")
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email (Opsional)") },
                    placeholder = { Text("nama@email.com") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = emergencyContactName,
                    onValueChange = { emergencyContactName = it },
                    label = { Text("Nama Kontak Darurat") },
                    placeholder = { Text("Keluarga terdekat / Saudara") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = emergencyContactPhone,
                    onValueChange = { emergencyContactPhone = it },
                    label = { Text("No. HP Darurat") },
                    placeholder = { Text("08XXXXXXXXXX") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            // SECTION 4: ALAMAT LENGKAP RT
            FormSectionHeader(title = "4. Alamat Lengkap & Domisili")
            OutlinedTextField(
                value = addressStreet,
                onValueChange = { addressStreet = it },
                label = { Text("Jalan / Blok / Nomor Rumah *") },
                placeholder = { Text("Contoh: Jl. Mawar No. 12 Blok B") },
                leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_address")
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = rtRw,
                    onValueChange = { rtRw = it },
                    label = { Text("RT / RW") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = postalCode,
                    onValueChange = { postalCode = it },
                    label = { Text("Kode Pos") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = houseOwnership,
                    onValueChange = { houseOwnership = it },
                    label = { Text("Status Kepemilikan Rumah") },
                    placeholder = { Text("Milik Sendiri / Sewa / Menumpang") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = settledSince,
                    onValueChange = { settledSince = it },
                    label = { Text("Menetap Sejak") },
                    placeholder = { Text("Tahun / Bulan") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            // SECTION 5: ANGGOTA KELUARGA & KETERANGAN
            FormSectionHeader(title = "5. Data Keluarga & Iuran RT")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = familyMembersCount.toString(),
                    onValueChange = {
                        val parsed = it.toIntOrNull()
                        if (parsed != null && parsed >= 1) familyMembersCount = parsed
                    },
                    label = { Text("Jumlah Jiwa dalam KK *") },
                    leadingIcon = { Icon(Icons.Default.People, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = occupation,
                    onValueChange = { occupation = it },
                    label = { Text("Pekerjaan Kepala KK") },
                    leadingIcon = { Icon(Icons.Default.Work, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = familyMembersNames,
                onValueChange = { familyMembersNames = it },
                label = { Text("Daftar Nama Anggota Keluarga (Opsional)") },
                placeholder = { Text("1. Budi (Kepala)\n2. Siti (Istri)\n3. Rian (Anak)") },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
            )

            // Status Iuran RT Selector
            Column {
                Text(
                    text = "Status Iuran RT:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Lunas", "Belum Lunas", "Bebas Iuran").forEach { status ->
                        val isSelected = feeStatus == status
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable { feeStatus = status }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = status,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Catatan / Keterangan Kependudukan") },
                placeholder = { Text("Contoh: Warga aktif ronda, surat domisili berlaku s/d 2026") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Action Button
            Button(
                onClick = {
                    if (fullName.isBlank()) {
                        errorMessage = "Nama lengkap kepala keluarga wajib diisi."
                        return@Button
                    }
                    if (kkNumber.length < 5) {
                        errorMessage = "Nomor Kartu Keluarga wajib diisi dengan benar."
                        return@Button
                    }
                    if (phone.isBlank()) {
                        errorMessage = "Nomor HP / WhatsApp wajib diisi untuk kontak warga."
                        return@Button
                    }
                    if (addressStreet.isBlank()) {
                        errorMessage = "Alamat rumah / jalan wajib diisi."
                        return@Button
                    }

                    errorMessage = null
                    val citizen = Citizen(
                        id = existingCitizen?.id ?: 0,
                        kkNumber = kkNumber.trim(),
                        nikHead = nikHead.trim().ifBlank { "3201010000000000" },
                        fullName = fullName.trim(),
                        category = category,
                        phone = phone.trim(),
                        email = email.trim(),
                        emergencyContactName = emergencyContactName.trim(),
                        emergencyContactPhone = emergencyContactPhone.trim(),
                        addressStreet = addressStreet.trim(),
                        rtRw = rtRw.trim(),
                        postalCode = postalCode.trim(),
                        houseOwnership = houseOwnership.trim(),
                        settledSince = settledSince.trim(),
                        familyMembersCount = familyMembersCount,
                        familyMembersNames = familyMembersNames.trim(),
                        occupation = occupation.trim(),
                        notes = notes.trim(),
                        feeStatus = feeStatus,
                        lastBlockHash = existingCitizen?.lastBlockHash ?: ""
                    )

                    if (isEdit) {
                        viewModel.updateCitizen(citizen, existingCitizen?.category)
                    } else {
                        viewModel.registerCitizen(citizen)
                    }
                    onNavigateBack()
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_citizen_button")
            ) {
                Icon(imageVector = Icons.Default.Security, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isEdit) "Tambang Mutasi ke Blockchain" else "Daftarkan & Tambang Blok Baru",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun FormSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun CategorySelectionGroup(
    selected: CitizenCategory,
    onSelected: (CitizenCategory) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        CitizenCategory.entries.forEach { cat ->
            val isSelected = selected == cat
            val (accentColor, icon) = when (cat) {
                CitizenCategory.WARGA_ASLI -> ColorWargaAsli to Icons.Default.Home
                CitizenCategory.WARGA_PENDATANG -> ColorWargaPendatang to Icons.Default.LocationCity
                CitizenCategory.WARGA_NGONTRAK -> ColorWargaNgontrak to Icons.Default.MeetingRoom
            }

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) accentColor.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) accentColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onSelected(cat) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = cat.title,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = cat.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    RadioButton(
                        selected = isSelected,
                        onClick = { onSelected(cat) }
                    )
                }
            }
        }
    }
}
