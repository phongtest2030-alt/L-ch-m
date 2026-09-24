package com.example.ui.screens

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.getVietnamCalendar
import com.example.utils.EventUtils
import com.example.utils.LunarUtils
import com.example.utils.PhongThuyUtils
import com.example.widget.CalendarCompactWidgetProvider
import com.example.widget.CalendarLargeWidgetProvider
import com.example.widget.CalendarMediumWidgetProvider
import java.util.Calendar

@Composable
fun WidgetPreviewScreen() {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var refreshKey by remember { mutableStateOf(0) }
    var showRefreshSnackbar by remember { mutableStateOf(false) }

    val cal = getVietnamCalendar()
    val sDay = cal.get(Calendar.DAY_OF_MONTH)
    val sMonth = cal.get(Calendar.MONTH) + 1
    val sYear = cal.get(Calendar.YEAR)
    val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)

    val dayOfWeekStr = when (dayOfWeek) {
        Calendar.SUNDAY -> "Chủ nhật"
        Calendar.MONDAY -> "Thứ hai"
        Calendar.TUESDAY -> "Thứ ba"
        Calendar.WEDNESDAY -> "Thứ tư"
        Calendar.THURSDAY -> "Thứ năm"
        Calendar.FRIDAY -> "Thứ sáu"
        Calendar.SATURDAY -> "Thứ bảy"
        else -> ""
    }

    val lunar = LunarUtils.convertSolar2Lunar(sDay, sMonth, sYear)
    val lDay = lunar[0]
    val lMonth = lunar[1]
    val lYear = lunar[2]
    val isLeap = lunar[3] == 1

    val canChiDay = LunarUtils.getCanChiDay(sDay, sMonth, sYear)
    val canChiMonth = LunarUtils.getCanChiMonth(lMonth, lYear)
    val isHoangDao = PhongThuyUtils.isHoangDao(canChiDay)
    val tietKhi = PhongThuyUtils.getTietKhi(sDay, sMonth)

    val upcomingEvents = EventUtils.getUpcomingEvents(45)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("widget_preview_screen")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Widget Màn hình chính",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Xem trước & cập nhật 3 kích cỡ tiện ích",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = {
                    refreshWidgets(context)
                    refreshKey++
                    showRefreshSnackbar = true
                },
                modifier = Modifier.testTag("refresh_widgets_button")
            ) {
                Icon(
                    Icons.Filled.Refresh,
                    contentDescription = "Cập nhật dữ liệu widget",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (showRefreshSnackbar) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Widgets,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Đã làm mới dữ liệu cho tất cả widget trên màn hình chính!",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // Hướng dẫn thêm widget
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    Icons.Filled.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Cách đưa Widget ra Màn hình chính",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "1. Nhấn giữ vào một vùng trống trên màn hình chính của điện thoại.\n2. Chọn biểu tượng \"Tiện ích\" (Widgets).\n3. Tìm ứng dụng \"Lịch Âm Dương\" và chọn 1 trong 3 kích cỡ bên dưới.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // 1. Widget Kích cỡ Nhỏ (2x2)
        Text(
            text = "1. Widget Kích thước Nhỏ (2x2 - Compact)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Text(
            text = "Thích hợp đặt ở góc màn hình, hiển thị Thứ, Ngày Dương lịch to, Âm lịch và Can Chi ngày gọn gàng.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            // Khung xem trước widget 2x2
            Card(
                modifier = Modifier
                    .size(width = 160.dp, height = 160.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = dayOfWeekStr,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$sDay",
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Tháng $sMonth, $sYear",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .width(40.dp)
                            .padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    Text(
                        text = "Âm: $lDay/$lMonth",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = canChiDay,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 2. Widget Kích cỡ Vừa (4x2)
        Text(
            text = "2. Widget Kích thước Vừa (4x2 - Medium)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Text(
            text = "Thiết kế chia đôi: Dương lịch sắc nét bên trái, Âm lịch, Can Chi, Hoàng Đạo và sự kiện lễ Tết sắp tới bên phải.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(bottom = 20.dp)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Bên trái
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = dayOfWeekStr,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$sDay",
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Tháng $sMonth, $sYear",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                VerticalDivider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(horizontal = 10.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                // Bên phải
                Column(
                    modifier = Modifier.weight(1.4f),
                    verticalArrangement = Arrangement.Center
                ) {
                    val lunarMonthStr = if (isLeap) "$lMonth (Nhuận)" else "$lMonth"
                    Text(
                        text = "Âm lịch: Ngày $lDay tháng $lunarMonthStr",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "Ngày $canChiDay - $canChiMonth",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (isHoangDao) "★ Ngày Hoàng Đạo" else "• Ngày Hắc Đạo",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    val nextEvent = upcomingEvents.firstOrNull()
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val prefix = when (nextEvent?.daysAway) {
                            0 -> "Hôm nay: "
                            1 -> "Ngày mai: "
                            null -> ""
                            else -> "[Còn ${nextEvent.daysAway} ngày] "
                        }
                        Text(
                            text = "★ $prefix${nextEvent?.title ?: "Không có sự kiện sắp tới"}",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // 3. Widget Kích cỡ Lớn (4x4)
        Text(
            text = "3. Widget Kích thước Lớn (4x4 - Large Banner)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Text(
            text = "Trải nghiệm đầy đủ nhất: Dương lịch, Âm lịch, Can Chi, Tiết khí, Hoàng đạo và danh sách 3 sự kiện / ngày lễ sắp tới liên tục.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "$dayOfWeekStr, $sDay thg $sMonth",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$sDay",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Năm $sYear (Dương lịch)",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1.2f),
                        horizontalAlignment = Alignment.End
                    ) {
                        val lunarMonthStr = if (isLeap) "$lMonth (Nhuận)" else "$lMonth"
                        Text(
                            text = "Ngày $lDay thg $lunarMonthStr",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "$canChiDay - $canChiMonth",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (isHoangDao) "★ Ngày Hoàng Đạo (Tốt)" else "• Ngày Hắc Đạo",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Tiết: $tietKhi",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                Text(
                    text = "SỰ KIỆN & LỄ TẾT SẮP TỚI",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        if (upcomingEvents.isNotEmpty()) {
                            val ev1 = upcomingEvents[0]
                            val p1 = if (ev1.daysAway == 0) "Hôm nay: " else if (ev1.daysAway == 1) "Ngày mai: " else "[Còn ${ev1.daysAway} ngày] "
                            Text(
                                text = "★ $p1${ev1.title}",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (upcomingEvents.size > 1) {
                            val ev2 = upcomingEvents[1]
                            val p2 = if (ev2.daysAway == 1) "Ngày mai: " else "[Còn ${ev2.daysAway} ngày] "
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "• $p2${ev2.title}",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 11.sp
                            )
                        }
                        if (upcomingEvents.size > 2) {
                            val ev3 = upcomingEvents[2]
                            val p3 = "[Còn ${ev3.daysAway} ngày] "
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "• $p3${ev3.title}",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Nút đồng bộ cập nhật tức thì
        Button(
            onClick = {
                refreshWidgets(context)
                refreshKey++
                showRefreshSnackbar = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("force_sync_widget_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Filled.Widgets, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Đồng bộ dữ liệu ra Widget ngay", fontWeight = FontWeight.Bold)
        }
    }
}

fun refreshWidgets(context: Context) {
    val appWidgetManager = AppWidgetManager.getInstance(context)

    // Cập nhật Compact
    val compactComponent = ComponentName(context, CalendarCompactWidgetProvider::class.java)
    val compactIds = appWidgetManager.getAppWidgetIds(compactComponent)
    for (id in compactIds) {
        CalendarCompactWidgetProvider.updateWidget(context, appWidgetManager, id)
    }

    // Cập nhật Medium
    val medComponent = ComponentName(context, CalendarMediumWidgetProvider::class.java)
    val medIds = appWidgetManager.getAppWidgetIds(medComponent)
    for (id in medIds) {
        CalendarMediumWidgetProvider.updateWidget(context, appWidgetManager, id)
    }

    // Cập nhật Large
    val largeComponent = ComponentName(context, CalendarLargeWidgetProvider::class.java)
    val largeIds = appWidgetManager.getAppWidgetIds(largeComponent)
    for (id in largeIds) {
        CalendarLargeWidgetProvider.updateWidget(context, appWidgetManager, id)
    }
}
