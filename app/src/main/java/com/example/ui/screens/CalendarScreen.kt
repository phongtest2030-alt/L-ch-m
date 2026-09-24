package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.model.AppState
import com.example.ui.components.DetailRow
import com.example.ui.components.getVietnamCalendar
import com.example.ui.components.isSameDay
import com.example.ui.components.isToday
import com.example.ui.theme.MontserratFontFamily
import com.example.ui.theme.PoppinsFontFamily
import com.example.utils.HolidayUtils
import com.example.utils.LunarUtils
import com.example.utils.PhongThuyUtils
import java.util.Calendar

@Composable
fun CalendarScreen(navController: NavController) {
    var selectedDate by remember { mutableStateOf(getVietnamCalendar()) }
    var currentMonth by remember { mutableStateOf(getVietnamCalendar()) }
    var selectedDateInfo by remember { mutableStateOf<DateInfo?>(null) }
    var showMonthYearPicker by remember { mutableStateOf(false) }
    var emphasizeLunar by remember { mutableStateOf(false) }

    val daysOfWeek = if (AppState.startWeekOnMonday) {
        listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
    } else {
        listOf("CN", "T2", "T3", "T4", "T5", "T6", "T7")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("calendar_screen")
    ) {
        // Hàng điều hướng tháng/năm
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    currentMonth = (currentMonth.clone() as Calendar).apply { add(Calendar.MONTH, -1) }
                },
                modifier = Modifier.testTag("prev_month_button")
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Tháng trước",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Surface(
                onClick = { showMonthYearPicker = true },
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.DateRange,
                        contentDescription = "Chọn tháng năm",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tháng ${currentMonth.get(Calendar.MONTH) + 1} năm ${currentMonth.get(Calendar.YEAR)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            IconButton(
                onClick = {
                    currentMonth = (currentMonth.clone() as Calendar).apply { add(Calendar.MONTH, 1) }
                },
                modifier = Modifier.testTag("next_month_button")
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Tháng sau",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Chuyển đổi hiển thị ưu tiên Dương lịch / Âm lịch
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = !emphasizeLunar,
                onClick = { emphasizeLunar = false },
                label = { Text("Ưu tiên Dương lịch", fontWeight = FontWeight.SemiBold) },
                modifier = Modifier.padding(end = 8.dp)
            )
            FilterChip(
                selected = emphasizeLunar,
                onClick = { emphasizeLunar = true },
                label = { Text("Ưu tiên Âm lịch", fontWeight = FontWeight.SemiBold) }
            )
        }

        // Các thứ trong tuần
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                daysOfWeek.forEach { day ->
                    val isSunday = day == "CN"
                    val isSaturday = day == "T7"
                    val textColor = when {
                        isSunday -> MaterialTheme.colorScheme.error
                        isSaturday -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Lưới các ngày trong tháng
        val daysInMonth = getDaysInMonthArray(currentMonth, AppState.startWeekOnMonday)
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.weight(1f)
        ) {
            items(daysInMonth) { dateInfo ->
                if (dateInfo != null) {
                    val holidays = HolidayUtils.getHolidays(
                        dateInfo.solarDay,
                        dateInfo.calendar.get(Calendar.MONTH) + 1,
                        dateInfo.lunarDay,
                        dateInfo.lunarMonth
                    )

                    DayCell(
                        dateInfo = dateInfo,
                        isSelected = isSameDay(dateInfo.calendar, selectedDate),
                        isToday = isToday(dateInfo.calendar),
                        isHoliday = holidays.isNotEmpty(),
                        emphasizeLunar = emphasizeLunar,
                        onClick = {
                            selectedDate = dateInfo.calendar
                            selectedDateInfo = dateInfo
                        }
                    )
                } else {
                    Box(modifier = Modifier.aspectRatio(1f))
                }
            }
        }

        // Xem nhanh ngày được chọn bên dưới lưới
        selectedDateInfo?.let { info ->
            val holidays = HolidayUtils.getHolidays(
                info.solarDay,
                info.calendar.get(Calendar.MONTH) + 1,
                info.lunarDay,
                info.lunarMonth
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Ngày ${info.solarDay}/${info.calendar.get(Calendar.MONTH) + 1}/${info.calendar.get(Calendar.YEAR)} Dương lịch",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Âm lịch: Ngày ${info.lunarDay} tháng ${info.lunarMonth} năm ${info.lunarYear}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (holidays.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Dịp lễ: ${holidays.joinToString(", ")}",
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    Button(
                        onClick = { selectedDateInfo = info },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("Chi tiết", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Hộp thoại Chọn nhanh Tháng và Năm
    if (showMonthYearPicker) {
        MonthYearPickerDialog(
            currentMonth = currentMonth.get(Calendar.MONTH) + 1,
            currentYear = currentMonth.get(Calendar.YEAR),
            onDismiss = { showMonthYearPicker = false },
            onConfirm = { m, y ->
                currentMonth = (currentMonth.clone() as Calendar).apply {
                    set(Calendar.YEAR, y)
                    set(Calendar.MONTH, m - 1)
                }
                showMonthYearPicker = false
            }
        )
    }

    // Hộp thoại Chi tiết ngày
    selectedDateInfo?.let { info ->
        DateDetailDialog(
            dateInfo = info,
            onDismiss = { selectedDateInfo = null }
        )
    }
}

data class DateInfo(
    val calendar: Calendar,
    val solarDay: Int,
    val lunarDay: Int,
    val lunarMonth: Int,
    val lunarYear: Int,
    val isLeap: Boolean
)

fun getDaysInMonthArray(monthCal: Calendar, startOnMonday: Boolean): List<DateInfo?> {
    val result = mutableListOf<DateInfo?>()
    val cal = monthCal.clone() as Calendar
    cal.set(Calendar.DAY_OF_MONTH, 1)

    var firstDayOfWeek = if (startOnMonday) {
        cal.get(Calendar.DAY_OF_WEEK) - 2
    } else {
        cal.get(Calendar.DAY_OF_WEEK) - 1
    }
    if (firstDayOfWeek < 0) firstDayOfWeek += 7

    for (i in 0 until firstDayOfWeek) {
        result.add(null)
    }

    val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    for (i in 1..maxDays) {
        val current = cal.clone() as Calendar
        val solarDay = current.get(Calendar.DAY_OF_MONTH)
        val solarMonth = current.get(Calendar.MONTH) + 1
        val solarYear = current.get(Calendar.YEAR)
        val lunarDate = LunarUtils.convertSolar2Lunar(solarDay, solarMonth, solarYear)
        result.add(
            DateInfo(
                calendar = current,
                solarDay = solarDay,
                lunarDay = lunarDate[0],
                lunarMonth = lunarDate[1],
                lunarYear = lunarDate[2],
                isLeap = lunarDate[3] == 1
            )
        )
        cal.add(Calendar.DAY_OF_MONTH, 1)
    }
    return result
}

@Composable
fun DayCell(
    dateInfo: DateInfo,
    isSelected: Boolean,
    isToday: Boolean,
    isHoliday: Boolean,
    emphasizeLunar: Boolean,
    onClick: () -> Unit
) {
    val bgColor = when {
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        isToday -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surface
    }

    val solarColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
        isHoliday -> MaterialTheme.colorScheme.error
        isToday -> MaterialTheme.colorScheme.onSecondaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }

    val lunarColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        dateInfo.lunarDay == 1 || dateInfo.lunarDay == 15 -> MaterialTheme.colorScheme.primary
        isToday -> MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.85f)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .background(bgColor, RoundedCornerShape(8.dp))
            .border(
                width = if (isToday && !isSelected) 2.dp else if (isSelected) 1.5.dp else 0.dp,
                color = if (isToday && !isSelected) MaterialTheme.colorScheme.primary else if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .testTag("day_cell_${dateInfo.solarDay}"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!emphasizeLunar) {
                Text(
                    text = dateInfo.solarDay.toString(),
                    fontFamily = MontserratFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = solarColor
                )
                Text(
                    text = if (dateInfo.lunarDay == 1 || dateInfo.lunarDay == 15) {
                        "${dateInfo.lunarDay}/${dateInfo.lunarMonth}"
                    } else {
                        "${dateInfo.lunarDay}"
                    },
                    fontFamily = PoppinsFontFamily,
                    fontSize = 11.sp,
                    fontWeight = if (dateInfo.lunarDay == 1 || dateInfo.lunarDay == 15) FontWeight.ExtraBold else FontWeight.Medium,
                    color = lunarColor
                )
            } else {
                Text(
                    text = "${dateInfo.lunarDay}",
                    fontFamily = MontserratFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (dateInfo.lunarDay == 1 || dateInfo.lunarDay == 15) MaterialTheme.colorScheme.primary else solarColor
                )
                Text(
                    text = "Dương ${dateInfo.solarDay}",
                    fontFamily = PoppinsFontFamily,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = lunarColor
                )
            }
        }
    }
}

@Composable
fun DateDetailDialog(
    dateInfo: DateInfo,
    onDismiss: () -> Unit
) {
    val solarDay = dateInfo.solarDay
    val solarMonth = dateInfo.calendar.get(Calendar.MONTH) + 1
    val solarYear = dateInfo.calendar.get(Calendar.YEAR)
    val dayOfWeek = dateInfo.calendar.get(Calendar.DAY_OF_WEEK)

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

    val canChiYear = LunarUtils.getCanChiYear(dateInfo.lunarYear)
    val canChiMonth = LunarUtils.getCanChiMonth(dateInfo.lunarMonth, dateInfo.lunarYear)
    val canChiDay = LunarUtils.getCanChiDay(solarDay, solarMonth, solarYear)

    val isHoangDao = PhongThuyUtils.isHoangDao(canChiDay)
    val gioHoangDao = PhongThuyUtils.getGioHoangDao(canChiDay)
    val tietKhi = PhongThuyUtils.getTietKhi(solarDay, solarMonth)
    val truc = PhongThuyUtils.getTruc(dateInfo.lunarMonth, canChiDay)
    val nguHanh = PhongThuyUtils.getNguHanh(canChiDay.split(" ")[0], canChiDay.split(" ")[1])
    val huongXuatHanh = PhongThuyUtils.getHuongXuatHanh(canChiDay)
    val holidays = HolidayUtils.getHolidays(solarDay, solarMonth, dateInfo.lunarDay, dateInfo.lunarMonth)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Chi tiết ngày",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Đóng",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                // Ngày Dương
                Text(
                    text = "Dương lịch",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "$dayOfWeekStr, ngày $solarDay tháng $solarMonth năm $solarYear",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (holidays.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    holidays.forEach { h ->
                        Text(
                            text = "★ $h",
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Ngày Âm
                Text(
                    text = "Âm lịch",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Ngày ${dateInfo.lunarDay} tháng ${if (dateInfo.isLeap) "${dateInfo.lunarMonth} (Nhuận)" else "${dateInfo.lunarMonth}"} năm ${dateInfo.lunarYear}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "• Can Chi ngày: $canChiDay",
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "• Can Chi tháng: $canChiMonth",
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "• Can Chi năm: $canChiYear",
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Thông tin phong tục tập quán
                Text(
                    text = "Thông tin phong tục & tập quán",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                DetailRow("Đánh giá ngày:", if (isHoangDao) "Ngày Hoàng Đạo (Tốt lành)" else "Ngày Hắc Đạo")
                DetailRow("Giờ Hoàng Đạo:", gioHoangDao)
                DetailRow("Tiết khí:", tietKhi)
                DetailRow("Trực ngày:", truc)
                DetailRow("Ngũ hành ngày:", nguHanh)
                DetailRow("Hướng xuất hành:", huongXuatHanh)

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Lưu ý: Các thông tin phong tục trên mang tính chất tham khảo văn hóa truyền thống, giúp sắp xếp sinh hoạt hàng ngày thuận tiện.",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Đóng", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun MonthYearPickerDialog(
    currentMonth: Int,
    currentYear: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    var selectedMonth by remember { mutableStateOf(currentMonth) }
    var selectedYear by remember { mutableStateOf(currentYear) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Chọn Tháng & Năm",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Bộ chọn tháng
                Text(
                    text = "Tháng: $selectedMonth",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalButton(
                        onClick = { if (selectedMonth > 1) selectedMonth-- },
                        enabled = selectedMonth > 1
                    ) {
                        Text("-1", fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = "Tháng $selectedMonth",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    FilledTonalButton(
                        onClick = { if (selectedMonth < 12) selectedMonth++ },
                        enabled = selectedMonth < 12
                    ) {
                        Text("+1", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bộ chọn năm
                Text(
                    text = "Năm: $selectedYear",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalButton(onClick = { selectedYear-- }) {
                        Text("-1", fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = "Năm $selectedYear",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    FilledTonalButton(onClick = { selectedYear++ }) {
                        Text("+1", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Hủy", fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onConfirm(selectedMonth, selectedYear) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("Xác nhận", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
