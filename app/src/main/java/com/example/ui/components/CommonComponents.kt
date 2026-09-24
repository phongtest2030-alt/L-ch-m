package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MontserratFontFamily
import com.example.ui.theme.PoppinsFontFamily
import java.util.Calendar
import java.util.TimeZone

val VIETNAM_TIME_ZONE: TimeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh")

fun getVietnamCalendar(): Calendar {
    return Calendar.getInstance(VIETNAM_TIME_ZONE)
}

@Composable
fun DetailRow(
    label: String,
    value: String,
    icon: String? = null,
    isHighlighted: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Text(
                text = icon,
                fontSize = 14.sp,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
        Text(
            text = label,
            fontFamily = MontserratFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.widthIn(min = 105.dp, max = 130.dp)
        )
        Text(
            text = value,
            fontFamily = PoppinsFontFamily,
            fontWeight = if (isHighlighted) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 13.sp,
            color = if (isHighlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Con dấu triện đỏ truyền thống phong cách Á Đông (Oriental Red Seal)
 */
@Composable
fun ZenSealBadge(
    text: String = "Phúc",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(22.dp)
            .background(Color(0xFF9E1B1B), RoundedCornerShape(4.dp))
            .border(1.dp, Color(0xFFD4AF37), RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color(0xFFFFF7ED),
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Thẻ mây vàng phong thủy cát tường nhẹ nhàng
 */
@Composable
fun AuspiciousCloudWatermark(modifier: Modifier = Modifier) {
    Text(
        text = "☁️",
        fontSize = 28.sp,
        color = Color(0xFFD4AF37).copy(alpha = 0.35f),
        modifier = modifier
    )
}

fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
           cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

fun isToday(cal: Calendar): Boolean {
    return isSameDay(cal, getVietnamCalendar())
}
