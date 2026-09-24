package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.getVietnamCalendar
import com.example.utils.HolidayUtils
import com.example.utils.LunarUtils
import com.example.utils.PhongThuyUtils
import java.util.Calendar

data class LookupResultData(
    val solarDay: Int,
    val solarMonth: Int,
    val solarYear: Int,
    val lunarDay: Int,
    val lunarMonth: Int,
    val lunarYear: Int,
    val isLeap: Boolean,
    val canChiDay: String,
    val canChiMonth: String,
    val canChiYear: String,
    val isHoangDao: Boolean,
    val gioHoangDao: String,
    val tietKhi: String,
    val nguHanh: String,
    val huongXuatHanh: String,
    val holidays: List<String>
)

@Composable
fun LookupScreen() {
    var isSolarToLunar by remember { mutableStateOf(true) }

    val today = getVietnamCalendar()
    var dayInput by remember { mutableStateOf(today.get(Calendar.DAY_OF_MONTH).toString()) }
    var monthInput by remember { mutableStateOf((today.get(Calendar.MONTH) + 1).toString()) }
    var yearInput by remember { mutableStateOf(today.get(Calendar.YEAR).toString()) }
    var isLeapMonth by remember { mutableStateOf(false) }

    var resultData by remember { mutableStateOf<LookupResultData?>(null) }
    var errorMessage by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("lookup_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Tra cứu ngày Âm – Dương",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Bộ chuyển đổi chiều tra cứu
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Chiều chuyển đổi",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Dương sang Âm",
                        fontWeight = if (isSolarToLunar) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSolarToLunar) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Switch(
                        checked = !isSolarToLunar,
                        onCheckedChange = {
                            isSolarToLunar = !it
                            resultData = null
                            errorMessage = ""
                        },
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .testTag("conversion_direction_switch")
                    )
                    Text(
                        text = "Âm sang Dương",
                        fontWeight = if (!isSolarToLunar) FontWeight.Bold else FontWeight.Medium,
                        color = if (!isSolarToLunar) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Ô nhập ngày, tháng, năm
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isSolarToLunar) "Nhập ngày Dương lịch cần đổi" else "Nhập ngày Âm lịch cần đổi",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = dayInput,
                        onValueChange = { dayInput = it },
                        label = { Text("Ngày") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("day_input_field")
                    )
                    OutlinedTextField(
                        value = monthInput,
                        onValueChange = { monthInput = it },
                        label = { Text("Tháng") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("month_input_field")
                    )
                    OutlinedTextField(
                        value = yearInput,
                        onValueChange = { yearInput = it },
                        label = { Text("Năm") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("year_input_field")
                    )
                }

                if (!isSolarToLunar) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 10.dp)
                    ) {
                        Checkbox(
                            checked = isLeapMonth,
                            onCheckedChange = { isLeapMonth = it }
                        )
                        Text(
                            text = "Đây là tháng nhuận âm lịch",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val now = getVietnamCalendar()
                            dayInput = now.get(Calendar.DAY_OF_MONTH).toString()
                            monthInput = (now.get(Calendar.MONTH) + 1).toString()
                            yearInput = now.get(Calendar.YEAR).toString()
                            isLeapMonth = false
                            errorMessage = ""
                            resultData = null
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Ngày hôm nay", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            errorMessage = ""
                            try {
                                val d = dayInput.trim().toInt()
                                val m = monthInput.trim().toInt()
                                val y = yearInput.trim().toInt()

                                if (d !in 1..31 || m !in 1..12 || y !in 1800..2199) {
                                    errorMessage = "Vui lòng nhập ngày (1-31), tháng (1-12) và năm (1800-2199) hợp lệ."
                                    resultData = null
                                    return@Button
                                }

                                if (isSolarToLunar) {
                                    val lunar = LunarUtils.convertSolar2Lunar(d, m, y)
                                    val ld = lunar[0]
                                    val lm = lunar[1]
                                    val ly = lunar[2]
                                    val leap = lunar[3] == 1

                                    val canChiY = LunarUtils.getCanChiYear(ly)
                                    val canChiM = LunarUtils.getCanChiMonth(lm, ly)
                                    val canChiD = LunarUtils.getCanChiDay(d, m, y)

                                    resultData = LookupResultData(
                                        solarDay = d,
                                        solarMonth = m,
                                        solarYear = y,
                                        lunarDay = ld,
                                        lunarMonth = lm,
                                        lunarYear = ly,
                                        isLeap = leap,
                                        canChiDay = canChiD,
                                        canChiMonth = canChiM,
                                        canChiYear = canChiY,
                                        isHoangDao = PhongThuyUtils.isHoangDao(canChiD),
                                        gioHoangDao = PhongThuyUtils.getGioHoangDao(canChiD),
                                        tietKhi = PhongThuyUtils.getTietKhi(d, m),
                                        nguHanh = PhongThuyUtils.getNguHanh(canChiD.split(" ")[0], canChiD.split(" ")[1]),
                                        huongXuatHanh = PhongThuyUtils.getHuongXuatHanh(canChiD),
                                        holidays = HolidayUtils.getHolidays(d, m, ld, lm)
                                    )
                                } else {
                                    val solar = LunarUtils.convertLunar2Solar(d, m, y, if (isLeapMonth) 1 else 0)
                                    val sd = solar[0]
                                    val sm = solar[1]
                                    val sy = solar[2]

                                    val canChiY = LunarUtils.getCanChiYear(y)
                                    val canChiM = LunarUtils.getCanChiMonth(m, y)
                                    val canChiD = LunarUtils.getCanChiDay(sd, sm, sy)

                                    resultData = LookupResultData(
                                        solarDay = sd,
                                        solarMonth = sm,
                                        solarYear = sy,
                                        lunarDay = d,
                                        lunarMonth = m,
                                        lunarYear = y,
                                        isLeap = isLeapMonth,
                                        canChiDay = canChiD,
                                        canChiMonth = canChiM,
                                        canChiYear = canChiY,
                                        isHoangDao = PhongThuyUtils.isHoangDao(canChiD),
                                        gioHoangDao = PhongThuyUtils.getGioHoangDao(canChiD),
                                        tietKhi = PhongThuyUtils.getTietKhi(sd, sm),
                                        nguHanh = PhongThuyUtils.getNguHanh(canChiD.split(" ")[0], canChiD.split(" ")[1]),
                                        huongXuatHanh = PhongThuyUtils.getHuongXuatHanh(canChiD),
                                        holidays = HolidayUtils.getHolidays(sd, sm, d, m)
                                    )
                                }
                            } catch (e: Exception) {
                                errorMessage = "Định dạng ngày không hợp lệ. Vui lòng kiểm tra lại số liệu."
                                resultData = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("convert_button")
                    ) {
                        Text("Tra cứu ngay", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Hiển thị thông báo lỗi
        if (errorMessage.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(14.dp),
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Hiển thị kết quả tra cứu
        resultData?.let { res ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Kết quả tra cứu chi tiết",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))

                    Text(
                        text = "Dương lịch: Ngày ${res.solarDay} tháng ${res.solarMonth} năm ${res.solarYear}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "Âm lịch: Ngày ${res.lunarDay} tháng ${if (res.isLeap) "${res.lunarMonth} (Nhuận)" else "${res.lunarMonth}"} năm ${res.lunarYear}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "• Can Chi ngày: ${res.canChiDay}", color = MaterialTheme.colorScheme.onSurface)
                    Text(text = "• Can Chi tháng: ${res.canChiMonth}", color = MaterialTheme.colorScheme.onSurface)
                    Text(text = "• Can Chi năm: ${res.canChiYear}", color = MaterialTheme.colorScheme.onSurface)

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "• Đánh giá: ${if (res.isHoangDao) "Ngày Hoàng Đạo (Tốt lành)" else "Ngày Hắc Đạo"}",
                        color = if (res.isHoangDao) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "• Tiết khí: ${res.tietKhi}", color = MaterialTheme.colorScheme.onSurface)
                    Text(text = "• Ngũ hành: ${res.nguHanh}", color = MaterialTheme.colorScheme.onSurface)
                    Text(text = "• Giờ Hoàng Đạo: ${res.gioHoangDao}", color = MaterialTheme.colorScheme.onSurface)
                    Text(text = "• Hướng xuất hành: ${res.huongXuatHanh}", color = MaterialTheme.colorScheme.onSurface)

                    if (res.holidays.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        res.holidays.forEach { h ->
                            Text(
                                text = "★ Ngày lễ: $h",
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
