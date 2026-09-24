package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.R
import com.example.ui.components.DetailRow
import com.example.ui.components.ZenSealBadge
import com.example.ui.components.getVietnamCalendar
import com.example.ui.theme.MontserratFontFamily
import com.example.ui.theme.PoppinsFontFamily
import com.example.utils.HolidayUtils
import com.example.utils.LunarUtils
import com.example.utils.PhongThuyUtils
import java.util.Calendar

@Composable
fun HomeScreen(navController: NavController) {
    var currentDate by remember { mutableStateOf(getVietnamCalendar()) }
    val scrollState = rememberScrollState()

    // Dương lịch theo giờ chuẩn Việt Nam (UTC+7)
    val solarDay = currentDate.get(Calendar.DAY_OF_MONTH)
    val solarMonth = currentDate.get(Calendar.MONTH) + 1
    val solarYear = currentDate.get(Calendar.YEAR)
    val dayOfWeek = currentDate.get(Calendar.DAY_OF_WEEK)

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

    val monthEng = when (solarMonth) {
        1 -> "JAN"
        2 -> "FEB"
        3 -> "MAR"
        4 -> "APR"
        5 -> "MAY"
        6 -> "JUN"
        7 -> "JUL"
        8 -> "AUG"
        9 -> "SEP"
        10 -> "OCT"
        11 -> "NOV"
        12 -> "DEC"
        else -> ""
    }

    // Âm lịch
    val lunarDate = LunarUtils.convertSolar2Lunar(solarDay, solarMonth, solarYear)
    val lunarDay = lunarDate[0]
    val lunarMonthVal = lunarDate[1]
    val lunarYearVal = lunarDate[2]
    val isLeap = lunarDate[3] == 1

    // Kiểm tra tháng đủ (30 ngày) hay tháng thiếu (29 ngày)
    val nextDayCheck = (currentDate.clone() as Calendar).apply {
        add(Calendar.DAY_OF_MONTH, 30 - lunarDay)
    }
    val nextDayLunar = LunarUtils.convertSolar2Lunar(
        nextDayCheck.get(Calendar.DAY_OF_MONTH),
        nextDayCheck.get(Calendar.MONTH) + 1,
        nextDayCheck.get(Calendar.YEAR)
    )
    val thangLoai = if (nextDayLunar[0] == 30) "Tháng đủ (30 ngày)" else "Tháng thiếu (29 ngày)"

    val currentHour = currentDate.get(Calendar.HOUR_OF_DAY)
    val jd = LunarUtils.jdFromDate(solarDay, solarMonth, solarYear)
    val canChiHour = LunarUtils.getCanChiHour(jd, currentHour)

    // Can Chi
    val canChiYear = LunarUtils.getCanChiYear(lunarYearVal)
    val canChiMonth = LunarUtils.getCanChiMonth(lunarMonthVal, lunarYearVal)
    val canChiDay = LunarUtils.getCanChiDay(solarDay, solarMonth, solarYear)

    // Phong tục tập quán
    val isHoangDao = PhongThuyUtils.isHoangDao(canChiDay)
    val gioHoangDao = PhongThuyUtils.getGioHoangDao(canChiDay)
    val tietKhi = PhongThuyUtils.getTietKhi(solarDay, solarMonth)
    val truc = PhongThuyUtils.getTruc(lunarMonthVal, canChiDay)

    val holidays = HolidayUtils.getHolidays(solarDay, solarMonth, lunarDay, lunarMonthVal)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 18.dp, vertical = 14.dp)
            .testTag("home_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ==========================================
        // 1. BANNER TIÊU ĐỀ NGHỆ THUẬT PHONG CÁCH TẠP CHÍ
        // ==========================================
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                // Hình nền thủy mặc Phương Đông tinh tế ở góc phải
                Image(
                    painter = painterResource(id = R.drawable.header_zen_art_1790176040276),
                    contentDescription = "Tranh thủy mặc non nước Á Đông",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(22.dp))
                )

                // Lớp phủ Gradient trắng kem để chữ bên trái nổi bật tuyệt đối
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.82f),
                                    Color.Transparent
                                ),
                                startX = 0f,
                                endX = 750f
                            )
                        )
                )

                // Nội dung Tiêu đề & Câu đối triết lý
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Lịch Âm Dương",
                            fontFamily = MontserratFontFamily,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = (-0.5).sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Giờ chuẩn Việt Nam (UTC+7)",
                            fontFamily = PoppinsFontFamily,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Hài hòa Thiên - Địa - Nhân\nSống thuận tự nhiên",
                            fontFamily = PoppinsFontFamily,
                            fontStyle = FontStyle.Italic,
                            fontSize = 11.5.sp,
                            lineHeight = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                        )
                    }

                    // Góc phải: Thư pháp & Nút Làm mới dạng đĩa sứ tròn
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        // Nút Làm mới tròn viền mỏng thanh thoát
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 3.dp,
                            modifier = Modifier.size(36.dp)
                        ) {
                            IconButton(
                                onClick = { currentDate = getVietnamCalendar() },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .testTag("refresh_today_button")
                            ) {
                                Icon(
                                    Icons.Filled.Refresh,
                                    contentDescription = "Cập nhật ngày hôm nay",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Thư pháp chữ & Con dấu triện "Bình an"
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 4.dp)
                        ) {
                            Text(
                                text = "Vạn sự\nBình an",
                                fontFamily = PoppinsFontFamily,
                                fontStyle = FontStyle.Italic,
                                fontSize = 10.sp,
                                lineHeight = 13.sp,
                                textAlign = TextAlign.End,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            ZenSealBadge(text = "Phúc")
                        }
                    }
                }
            }
        }

        // ==========================================
        // 2. THẺ DƯƠNG LỊCH HIỆN ĐẠI (SOLAR CARD)
        // ==========================================
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f),
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )
                )
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Bên trái: Thông số Dương lịch dứt khoát, thanh lịch
                Column(
                    modifier = Modifier.weight(1.05f),
                    verticalArrangement = Arrangement.Center
                ) {
                    // Nhãn DƯƠNG LỊCH bo góc mềm
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFDE8E6)
                    ) {
                        Text(
                            text = "DƯƠNG LỊCH",
                            fontFamily = MontserratFontFamily,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = dayOfWeekStr,
                        fontFamily = MontserratFontFamily,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Số ngày Dương lịch lớn, nổi bật với font Montserrat Black
                    Text(
                        text = "$solarDay",
                        fontFamily = MontserratFontFamily,
                        fontSize = 72.sp,
                        lineHeight = 74.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = (-2).sp
                    )

                    Text(
                        text = "Tháng $solarMonth năm $solarYear",
                        fontFamily = MontserratFontFamily,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Bên phải: Tác phẩm Lịch để bàn bằng gỗ sang trọng
                Box(
                    modifier = Modifier
                        .weight(0.95f)
                        .height(160.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .shadow(4.dp, RoundedCornerShape(16.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.solar_desk_cal_1790176061497),
                        contentDescription = "Lịch để bàn phong cách tối giản",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Tấm thẻ lật lịch mô phỏng chính xác ngày thực tế
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            color = Color(0xFFFFFDF9).copy(alpha = 0.94f),
                            shape = RoundedCornerShape(10.dp),
                            shadowElevation = 3.dp,
                            modifier = Modifier
                                .width(94.dp)
                                .padding(top = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = monthEng,
                                    fontFamily = MontserratFontFamily,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF9E1B1B)
                                )
                                Text(
                                    text = "$solarDay",
                                    fontFamily = MontserratFontFamily,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF1C1917)
                                )
                                Text(
                                    text = "$solarYear",
                                    fontFamily = MontserratFontFamily,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF57534E)
                                )
                            }
                        }

                        // Đế gỗ thông điệp tích cực
                        Surface(
                            color = Color(0xFF332014).copy(alpha = 0.88f),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.padding(bottom = 6.dp)
                        ) {
                            Text(
                                text = "Mỗi ngày là một khởi đầu tốt đẹp",
                                fontFamily = PoppinsFontFamily,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFFFF8E7),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            // Sự kiện ngày lễ nổi bật nếu có
            if (holidays.isNotEmpty()) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 18.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
                Surface(
                    color = Color(0xFFFDE8E6),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                        holidays.forEach { holiday ->
                            Text(
                                text = "★ $holiday",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                fontFamily = MontserratFontFamily,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // 3. THẺ ÂM LỊCH NGHỆ THUẬT (LUNAR CARD)
        // ==========================================
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f),
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )
                )
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Họa tiết mây lành cát tường nghệ thuật ở hai bên
                Text(
                    text = "☁",
                    fontSize = 32.sp,
                    color = Color(0xFFD4AF37).copy(alpha = 0.25f),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = (-4).dp, y = 14.dp)
                )
                Text(
                    text = "☁",
                    fontSize = 32.sp,
                    color = Color(0xFFD4AF37).copy(alpha = 0.25f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = 14.dp)
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Nhãn ÂM LỊCH màu hoàng kim trang nhã
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFBF2DC)
                    ) {
                        Text(
                            text = "ÂM LỊCH",
                            fontFamily = MontserratFontFamily,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF8D6210),
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Ngày $lunarDay",
                        fontFamily = MontserratFontFamily,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = (-0.5).sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Tháng ${if (isLeap) "$lunarMonthVal (Nhuận)" else "$lunarMonthVal"} năm $lunarYearVal",
                        fontFamily = MontserratFontFamily,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = thangLoai,
                        fontFamily = PoppinsFontFamily,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )

                    // 4 Cột Can Chi được bố trí cân đối, thoáng đãng
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CanChiColumn(label = "Giờ hiện tại", value = canChiHour, modifier = Modifier.weight(1f))
                        VerticalDivider(
                            modifier = Modifier.height(36.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        )
                        CanChiColumn(label = "Ngày", value = canChiDay, modifier = Modifier.weight(1f))
                        VerticalDivider(
                            modifier = Modifier.height(36.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        )
                        CanChiColumn(label = "Tháng", value = canChiMonth, modifier = Modifier.weight(1f))
                        VerticalDivider(
                            modifier = Modifier.height(36.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        )
                        CanChiColumn(label = "Năm", value = canChiYear, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // ==========================================
        // 4. THẺ PHONG TỤC & TẬP QUÁN (CULTURE & FENG SHUI)
        // ==========================================
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 18.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f),
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )
                )
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Tác phẩm hoa sen và thư pháp "An tâm Sống tốt" ở góc dưới phải
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 2.dp, end = 2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        Text(
                            text = "An\ntâm\nSống\ntốt",
                            fontFamily = PoppinsFontFamily,
                            fontStyle = FontStyle.Italic,
                            fontSize = 11.sp,
                            lineHeight = 14.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        ZenSealBadge(text = "An")
                    }

                    Image(
                        painter = painterResource(id = R.drawable.lotus_zen_art_1790176118325),
                        contentDescription = "Hoa sen ngát hương",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                }

                // Dòng dữ liệu phong tục tập quán
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.74f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Text(
                            text = "☯",
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "Thông tin phong tục & tập quán",
                            fontFamily = MontserratFontFamily,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    DetailRow(
                        label = "Đánh giá ngày:",
                        value = if (isHoangDao) "Ngày Hoàng Đạo (Tốt lành)" else "Ngày Hắc Đạo",
                        icon = "★",
                        isHighlighted = isHoangDao
                    )

                    DetailRow(
                        label = "Giờ Hoàng Đạo:",
                        value = gioHoangDao,
                        icon = "📅"
                    )

                    DetailRow(
                        label = "Tiết khí:",
                        value = tietKhi,
                        icon = "🍃"
                    )

                    DetailRow(
                        label = "Trực ngày:",
                        value = truc,
                        icon = "🍂"
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Văn hóa truyền thống dân gian Việt Nam",
                        fontFamily = PoppinsFontFamily,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
private fun CanChiColumn(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.padding(horizontal = 4.dp)
    ) {
        Text(
            text = label,
            fontFamily = PoppinsFontFamily,
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = value,
            fontFamily = MontserratFontFamily,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}
