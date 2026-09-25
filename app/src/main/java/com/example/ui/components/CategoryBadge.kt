package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CitizenCategory

@Composable
fun CategoryBadge(
    category: CitizenCategory,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false
) {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) Color(category.darkContainerColor) else Color(category.lightContainerColor)
    val contentColor = if (isDark) Color(category.darkContentColor) else Color(category.lightContentColor)

    val icon = when (category) {
        CitizenCategory.WARGA_ASLI -> Icons.Default.Home
        CitizenCategory.WARGA_PENDATANG -> Icons.Default.LocationCity
        CitizenCategory.WARGA_NGONTRAK -> Icons.Default.MeetingRoom
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = if (isCompact) 6.dp else 10.dp, vertical = if (isCompact) 2.dp else 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(if (isCompact) 12.dp else 14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (isCompact) category.shortLabel else category.title,
                color = contentColor,
                fontSize = if (isCompact) 11.sp else 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
